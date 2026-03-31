package dao.impl;

import model.FlashSale;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FlashSaleDAO {

    public boolean insert(FlashSale flashSale) {
        String sql = """
                insert into flash_sales(product_id, discount_percent, start_time, end_time, max_quantity, sold_quantity, status)
                values(?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flashSale.getProductId());
            ps.setDouble(2, flashSale.getDiscountPercent());
            ps.setString(3, flashSale.getStartTime());
            ps.setString(4, flashSale.getEndTime());
            ps.setInt(5, flashSale.getMaxQuantity());
            ps.setInt(6, flashSale.getSoldQuantity());
            ps.setString(7, flashSale.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<FlashSale> findAll() {
        List<FlashSale> list = new ArrayList<>();
        String sql = "select * from flash_sales order by flash_sale_id desc";

        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapFlashSale(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public FlashSale findActiveByProductId(Connection conn, int productId) throws SQLException {
        String sql = """
                select * from flash_sales
                where product_id = ?
                  and status = 'ACTIVE'
                  and now() between start_time and end_time
                  and sold_quantity < max_quantity
                order by discount_percent desc, flash_sale_id desc
                limit 1
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapFlashSale(rs);
                }
            }
        }

        return null;
    }

    public boolean increaseSoldQuantity(Connection conn, int flashSaleId, int quantity) throws SQLException {
        String sql = """
                update flash_sales
                set sold_quantity = sold_quantity + ?
                where flash_sale_id = ?
                  and sold_quantity + ? <= max_quantity
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, flashSaleId);
            ps.setInt(3, quantity);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean decreaseSoldQuantity(Connection conn, int flashSaleId, int quantity) throws SQLException {
        String sql = """
                update flash_sales
                set sold_quantity = sold_quantity - ?
                where flash_sale_id = ?
                  and sold_quantity >= ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, flashSaleId);
            ps.setInt(3, quantity);
            return ps.executeUpdate() > 0;
        }
    }

    private FlashSale mapFlashSale(ResultSet rs) throws SQLException {
        FlashSale flashSale = new FlashSale();
        flashSale.setFlashSaleId(rs.getInt("flash_sale_id"));
        flashSale.setProductId(rs.getInt("product_id"));
        flashSale.setDiscountPercent(rs.getDouble("discount_percent"));
        flashSale.setStartTime(String.valueOf(rs.getTimestamp("start_time")));
        flashSale.setEndTime(String.valueOf(rs.getTimestamp("end_time")));
        flashSale.setMaxQuantity(rs.getInt("max_quantity"));
        flashSale.setSoldQuantity(rs.getInt("sold_quantity"));
        flashSale.setStatus(rs.getString("status"));
        return flashSale;
    }
}