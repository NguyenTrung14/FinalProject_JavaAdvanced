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
import java.util.List;

public class ShoppingDAO {
    private final FlashSaleDAO flashSaleDAO = new FlashSaleDAO();
    private final CouponDAO couponDAO = new CouponDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final CartDAO cartDAO = new CartDAO();

    public boolean addToCartAndReserve(int userId, int productId, int quantity) {
        Connection conn = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            Product product = productDAO.findByIdForUpdate(conn, productId);
            if (product == null || quantity <= 0) {
                conn.rollback();
                return false;
            }

            if (product.getStock() < quantity) {
                conn.rollback();
                return false;
            }

            FlashSale flashSale = flashSaleDAO.findActiveByProductId(conn, productId);

            Integer flashSaleId = null;
            int flashQty = 0;
            int normalQty = quantity;
            double flashPrice = 0;
            double normalPrice = product.getPrice();

            if (flashSale != null) {
                int remaining = flashSale.getRemainingQuantity();
                flashQty = Math.min(quantity, remaining);
                normalQty = quantity - flashQty;
                flashSaleId = flashQty > 0 ? flashSale.getFlashSaleId() : null;
                flashPrice = product.getPrice() * (100 - flashSale.getDiscountPercent()) / 100.0;
            }

            if (!productDAO.decreaseStock(conn, productId, quantity)) {
                conn.rollback();
                return false;
            }

            if (flashQty > 0) {
                if (!flashSaleDAO.increaseSoldQuantity(conn, flashSaleId, flashQty)) {
                    conn.rollback();
                    return false;
                }
            }

            int cartId = cartDAO.findOrCreateCart(conn, userId);

            boolean ok = cartDAO.upsertCartItem(
                    conn,
                    cartId,
                    productId,
                    flashSaleId,
                    quantity,
                    flashQty,
                    normalQty,
                    flashPrice,
                    normalPrice);

            if (!ok) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception ignored) {
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public List<CartItem> getReservedCartItems(int userId) {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            return cartDAO.findCartItemsByUserId(conn, userId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public boolean removeCartItemAndRestore(int userId, int productId) {
        Connection conn = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            CartItem item = cartDAO.findCartItemByUserIdAndProductId(conn, userId, productId);
            if (item == null) {
                conn.rollback();
                return false;
            }

            if (!productDAO.increaseStock(conn, productId, item.getQuantity())) {
                conn.rollback();
                return false;
            }

            if (item.getFlashSaleId() != null && item.getReservedFlashQuantity() > 0) {
                if (!flashSaleDAO.decreaseSoldQuantity(conn, item.getFlashSaleId(), item.getReservedFlashQuantity())) {
                    conn.rollback();
                    return false;
                }
            }

            if (!cartDAO.deleteCartItemById(conn, item.getCartItemId())) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception ignored) {
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public boolean checkoutReservedCart(int userId, String couponCode) {
        Connection conn = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            List<CartItem> cartItems = cartDAO.findCartItemsByUserId(conn, userId);
            if (cartItems == null || cartItems.isEmpty()) {
                conn.rollback();
                return false;
            }

            double originalTotal = 0;
            double flashDiscountTotal = 0;

            for (CartItem item : cartItems) {
                double lineOriginal = item.getProduct().getPrice() * item.getQuantity();
                double lineActual = item.getSubtotal();

                originalTotal += lineOriginal;
                flashDiscountTotal += (lineOriginal - lineActual);
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
                double avgUnitPrice = item.getSubtotal() / item.getQuantity();

                if (!insertOrderDetail(
                        conn,
                        orderId,
                        item.getProduct().getProductId(),
                        item.getQuantity(),
                        avgUnitPrice,
                        item.getFlashSaleId(),
                        item.getReservedFlashQuantity(),
                        item.getReservedNormalQuantity())) {
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

            if (!cartDAO.clearCartItemsByUserId(conn, userId)) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception ignored) {
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public boolean checkout(int userId, List<CartItem> cartItems, String couponCode) {
        return checkoutReservedCart(userId, couponCode);
    }

    private int insertOrder(Connection conn, int userId, double totalAmount, String couponCode, double discountAmount)
            throws SQLException {
        String sql = """
                insert into orders(user_id, total_amount, status, created_at, coupon_code, discount_amount)
                values(?, ?, 'PENDING', now(), ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
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

    private boolean insertOrderDetail(Connection conn,
            int orderId,
            int productId,
            int quantity,
            double price,
            Integer flashSaleId,
            int flashSaleQuantity,
            int normalQuantity) throws SQLException {
        String sql = """
                insert into order_details(
                    order_id, product_id, quantity, price,
                    flash_sale_id, flash_sale_quantity, normal_quantity
                ) values(?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.setDouble(4, price);

            if (flashSaleId == null) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, flashSaleId);
            }

            ps.setInt(6, flashSaleQuantity);
            ps.setInt(7, normalQuantity);
            return ps.executeUpdate() > 0;
        }
    }
}