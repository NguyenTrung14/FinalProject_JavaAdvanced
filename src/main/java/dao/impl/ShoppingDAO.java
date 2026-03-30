package dao.impl;

import model.CartItem;
import model.Coupon;
import model.FlashSale;
import model.Product;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class ShoppingDAO {
    private final FlashSaleDAO flashSaleDAO = new FlashSaleDAO();
    private final CouponDAO couponDAO = new CouponDAO();

    public boolean checkout(int userId, List<CartItem> cartItems, String couponCode) {
        Connection conn = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            double originalTotal = 0;
            double flashDiscountTotal = 0;

            for (CartItem item : cartItems) {
                Product latestProduct = findProductById(conn, item.getProduct().getProductId());
                int quantity = item.getQuantity();

                if (latestProduct == null || quantity <= 0 || latestProduct.getStock() < quantity) {
                    conn.rollback();
                    return false;
                }

                originalTotal += latestProduct.getPrice() * quantity;

                FlashSale flashSale = flashSaleDAO.findActiveByProductId(conn, latestProduct.getProductId());
                if (flashSale != null) {
                    if (flashSale.getRemainingQuantity() < quantity) {
                        conn.rollback();
                        return false;
                    }

                    double itemDiscount = latestProduct.getPrice() * quantity * flashSale.getDiscountPercent() / 100.0;
                    flashDiscountTotal += itemDiscount;
                }
            }

            double afterFlashTotal = originalTotal - flashDiscountTotal;
            double couponDiscount = 0;
            Coupon coupon = null;

            if (couponCode != null && !couponCode.trim().isEmpty()) {
                coupon = couponDAO.findValidCoupon(conn, couponCode.trim().toUpperCase());

                if (coupon == null) {
                    conn.rollback();
                    return false;
                }

                if (afterFlashTotal < coupon.getMinOrderAmount()) {
                    conn.rollback();
                    return false;
                }

                couponDiscount = afterFlashTotal * coupon.getDiscountPercent() / 100.0;
            }

            double finalTotal = afterFlashTotal - couponDiscount;
            if (finalTotal < 0) {
                finalTotal = 0;
            }

            int orderId = insertOrder(
                    conn,
                    userId,
                    finalTotal,
                    coupon == null ? null : coupon.getCode(),
                    flashDiscountTotal + couponDiscount);

            if (orderId <= 0) {
                conn.rollback();
                return false;
            }

            for (CartItem item : cartItems) {
                Product product = findProductById(conn, item.getProduct().getProductId());
                int quantity = item.getQuantity();
                double finalUnitPrice = product.getPrice();

                FlashSale flashSale = flashSaleDAO.findActiveByProductId(conn, product.getProductId());
                if (flashSale != null) {
                    finalUnitPrice = product.getPrice() * (100 - flashSale.getDiscountPercent()) / 100.0;

                    boolean soldUpdated = flashSaleDAO.increaseSoldQuantity(conn, flashSale.getFlashSaleId(), quantity);
                    if (!soldUpdated) {
                        conn.rollback();
                        return false;
                    }
                }

                if (!insertOrderDetail(conn, orderId, product.getProductId(), quantity, finalUnitPrice)) {
                    conn.rollback();
                    return false;
                }

                if (!decreaseStock(conn, product.getProductId(), quantity)) {
                    conn.rollback();
                    return false;
                }
            }

            if (coupon != null) {
                boolean usedUpdated = couponDAO.increaseUsedCount(conn, coupon.getCouponId());
                if (!usedUpdated) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private int insertOrder(Connection conn, int userId, double totalAmount, String couponCode, double discountAmount)
            throws SQLException {
        String sql = """
                insert into orders(user_id, total_amount, status, created_at, coupon_code, discount_amount)
                values(?, ?, 'PENDING', now(), ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setDouble(2, totalAmount);
            ps.setString(3, couponCode);
            ps.setDouble(4, discountAmount);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }

        return 0;
    }

    private boolean insertOrderDetail(Connection conn, int orderId, int productId, int quantity, double price)
            throws SQLException {
        String sql = "insert into order_details(order_id, product_id, quantity, price) values(?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.setDouble(4, price);
            return ps.executeUpdate() > 0;
        }
    }

    private boolean decreaseStock(Connection conn, int productId, int quantity) throws SQLException {
        String sql = "update products set stock = stock - ? where product_id = ? and stock >= ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            return ps.executeUpdate() > 0;
        }
    }

    private Product findProductById(Connection conn, int productId) throws SQLException {
        String sql = "select * from products where product_id = ? and status = 'ACTIVE'";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Product p = new Product();
                    p.setProductId(rs.getInt("product_id"));
                    p.setProductName(rs.getString("product_name"));
                    p.setStorage(rs.getString("storage"));
                    p.setColor(rs.getString("color"));
                    p.setPrice(rs.getDouble("price"));
                    p.setStock(rs.getInt("stock"));
                    p.setDescription(rs.getString("description"));
                    p.setCategoryId(rs.getInt("category_id"));
                    p.setStatus(rs.getString("status"));
                    return p;
                }
            }
        }

        return null;
    }
}