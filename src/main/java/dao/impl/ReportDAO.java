package dao.impl;

import dao.IReportDAO;
import model.TopProductStat;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO implements IReportDAO {

    @Override
    public List<TopProductStat> getTop5BestSellingProducts(int month, int year) {
        List<TopProductStat> list = new ArrayList<>();

        String sql = """
                select
                    p.product_id,
                    p.product_name,
                    p.storage,
                    p.color,
                    sum(od.quantity) as total_sold,
                    sum(od.quantity * od.price) as revenue
                from order_details od
                join orders o on od.order_id = o.order_id
                join products p on od.product_id = p.product_id
                where year(o.created_at) = ?
                  and month(o.created_at) = ?
                  and o.status in ('SHIPPING', 'DELIVERED')
                group by p.product_id, p.product_name, p.storage, p.color
                order by total_sold desc, revenue desc
                limit 5
                """;

        try (
                Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TopProductStat stat = new TopProductStat();
                    stat.setProductId(rs.getInt("product_id"));
                    stat.setProductName(rs.getString("product_name"));
                    stat.setStorage(rs.getString("storage"));
                    stat.setColor(rs.getString("color"));
                    stat.setTotalSold(rs.getInt("total_sold"));
                    stat.setRevenue(rs.getDouble("revenue"));
                    list.add(stat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public double getRevenueByMonth(int month, int year) {
        String sql = """
                select coalesce(sum(od.quantity * od.price), 0) as total_revenue
                from order_details od
                join orders o on od.order_id = o.order_id
                where year(o.created_at) = ?
                  and month(o.created_at) = ?
                  and o.status in ('SHIPPING', 'DELIVERED')
                """;

        try (
                Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_revenue");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}