package dao.impl;

import model.CartItem;
import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    public int findOrCreateCart(Connection conn, int userId) throws SQLException {
        String selectSql = "select cart_id from carts where user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cart_id");
                }
            }
        }

        String insertSql = "insert into carts(user_id) values(?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Khong tao duoc cart.");
    }

    public boolean upsertCartItem(Connection conn,
            int cartId,
            int productId,
            Integer flashSaleId,
            int quantity,
            int flashQty,
            int normalQty,
            double flashPrice,
            double normalPrice) throws SQLException {

        String checkSql = "select cart_item_id from cart_items where cart_id = ? and product_id = ?";
        try (PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setInt(1, cartId);
            check.setInt(2, productId);

            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) {
                    String updateSql = """
                            update cart_items
                            set quantity = quantity + ?,
                                flash_sale_id = ?,
                                reserved_flash_quantity = reserved_flash_quantity + ?,
                                reserved_normal_quantity = reserved_normal_quantity + ?,
                                flash_unit_price = ?,
                                normal_unit_price = ?
                            where cart_id = ? and product_id = ?
                            """;
                    try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                        ps.setInt(1, quantity);
                        if (flashSaleId == null) {
                            ps.setNull(2, Types.INTEGER);
                        } else {
                            ps.setInt(2, flashSaleId);
                        }
                        ps.setInt(3, flashQty);
                        ps.setInt(4, normalQty);
                        ps.setDouble(5, flashPrice);
                        ps.setDouble(6, normalPrice);
                        ps.setInt(7, cartId);
                        ps.setInt(8, productId);
                        return ps.executeUpdate() > 0;
                    }
                }
            }
        }

        String insertSql = """
                insert into cart_items(
                    cart_id, product_id, flash_sale_id, quantity,
                    reserved_flash_quantity, reserved_normal_quantity,
                    flash_unit_price, normal_unit_price
                ) values (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, cartId);
            ps.setInt(2, productId);
            if (flashSaleId == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, flashSaleId);
            }
            ps.setInt(4, quantity);
            ps.setInt(5, flashQty);
            ps.setInt(6, normalQty);
            ps.setDouble(7, flashPrice);
            ps.setDouble(8, normalPrice);
            return ps.executeUpdate() > 0;
        }
    }

    public List<CartItem> findCartItemsByUserId(Connection conn, int userId) throws SQLException {
        List<CartItem> list = new ArrayList<>();

        String sql = """
                select
                    ci.cart_item_id,
                    ci.product_id,
                    ci.flash_sale_id,
                    ci.quantity,
                    ci.reserved_flash_quantity,
                    ci.reserved_normal_quantity,
                    ci.flash_unit_price,
                    ci.normal_unit_price,
                    p.product_name,
                    p.storage,
                    p.color,
                    p.price,
                    p.stock,
                    p.description,
                    p.category_id,
                    p.status,
                    p.created_at
                from cart_items ci
                join carts c on ci.cart_id = c.cart_id
                join products p on ci.product_id = p.product_id
                where c.user_id = ?
                order by ci.cart_item_id asc
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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
                    p.setCreatedAt(rs.getString("created_at"));

                    CartItem item = new CartItem();
                    item.setCartItemId(rs.getInt("cart_item_id"));
                    item.setProduct(p);
                    item.setQuantity(rs.getInt("quantity"));

                    int flashSaleId = rs.getInt("flash_sale_id");
                    if (rs.wasNull()) {
                        item.setFlashSaleId(null);
                    } else {
                        item.setFlashSaleId(flashSaleId);
                    }

                    item.setReservedFlashQuantity(rs.getInt("reserved_flash_quantity"));
                    item.setReservedNormalQuantity(rs.getInt("reserved_normal_quantity"));
                    item.setFlashUnitPrice(rs.getDouble("flash_unit_price"));
                    item.setNormalUnitPrice(rs.getDouble("normal_unit_price"));
                    list.add(item);
                }
            }
        }

        return list;
    }

    public CartItem findCartItemByUserIdAndProductId(Connection conn, int userId, int productId) throws SQLException {
        String sql = """
                select
                    ci.cart_item_id,
                    ci.product_id,
                    ci.flash_sale_id,
                    ci.quantity,
                    ci.reserved_flash_quantity,
                    ci.reserved_normal_quantity,
                    ci.flash_unit_price,
                    ci.normal_unit_price,
                    p.product_name,
                    p.storage,
                    p.color,
                    p.price,
                    p.stock,
                    p.description,
                    p.category_id,
                    p.status,
                    p.created_at
                from cart_items ci
                join carts c on ci.cart_id = c.cart_id
                join products p on ci.product_id = p.product_id
                where c.user_id = ? and ci.product_id = ?
                limit 1
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);

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
                    p.setCreatedAt(rs.getString("created_at"));

                    CartItem item = new CartItem();
                    item.setCartItemId(rs.getInt("cart_item_id"));
                    item.setProduct(p);
                    item.setQuantity(rs.getInt("quantity"));

                    int flashSaleId = rs.getInt("flash_sale_id");
                    if (rs.wasNull()) {
                        item.setFlashSaleId(null);
                    } else {
                        item.setFlashSaleId(flashSaleId);
                    }

                    item.setReservedFlashQuantity(rs.getInt("reserved_flash_quantity"));
                    item.setReservedNormalQuantity(rs.getInt("reserved_normal_quantity"));
                    item.setFlashUnitPrice(rs.getDouble("flash_unit_price"));
                    item.setNormalUnitPrice(rs.getDouble("normal_unit_price"));
                    return item;
                }
            }
        }

        return null;
    }

    public boolean deleteCartItemById(Connection conn, int cartItemId) throws SQLException {
        String sql = "delete from cart_items where cart_item_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartItemId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean clearCartItemsByUserId(Connection conn, int userId) throws SQLException {
        String sql = """
                delete ci
                from cart_items ci
                join carts c on ci.cart_id = c.cart_id
                where c.user_id = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            return true;
        }
    }
}