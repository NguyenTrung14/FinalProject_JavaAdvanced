package dao.impl;

import model.Coupon;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CouponDAO {

    public boolean insert(Coupon coupon) {
        String sql = """
                insert into coupons(code, discount_percent, start_time, end_time, quantity, used_count, min_order_amount, status)
                values(?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, coupon.getCode());
            ps.setDouble(2, coupon.getDiscountPercent());
            ps.setString(3, coupon.getStartTime());
            ps.setString(4, coupon.getEndTime());
            ps.setInt(5, coupon.getQuantity());
            ps.setInt(6, coupon.getUsedCount());
            ps.setDouble(7, coupon.getMinOrderAmount());
            ps.setString(8, coupon.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Coupon> findAll() {
        List<Coupon> list = new ArrayList<>();
        String sql = "select * from coupons order by coupon_id desc";

        try (
                Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapCoupon(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public Coupon findValidCoupon(Connection conn, String code) throws SQLException {
        String sql = """
                select * from coupons
                where upper(code) = upper(?)
                  and status = 'ACTIVE'
                  and now() between start_time and end_time
                  and used_count < quantity
                limit 1
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCoupon(rs);
                }
            }
        }

        return null;
    }

    public boolean increaseUsedCount(Connection conn, int couponId) throws SQLException {
        String sql = """
                update coupons
                set used_count = used_count + 1
                where coupon_id = ?
                  and used_count + 1 <= quantity
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, couponId);
            return ps.executeUpdate() > 0;
        }
    }

    private Coupon mapCoupon(ResultSet rs) throws SQLException {
        Coupon coupon = new Coupon();
        coupon.setCouponId(rs.getInt("coupon_id"));
        coupon.setCode(rs.getString("code"));
        coupon.setDiscountPercent(rs.getDouble("discount_percent"));
        coupon.setStartTime(String.valueOf(rs.getTimestamp("start_time")));
        coupon.setEndTime(String.valueOf(rs.getTimestamp("end_time")));
        coupon.setQuantity(rs.getInt("quantity"));
        coupon.setUsedCount(rs.getInt("used_count"));
        coupon.setMinOrderAmount(rs.getDouble("min_order_amount"));
        coupon.setStatus(rs.getString("status"));
        return coupon;
    }
}