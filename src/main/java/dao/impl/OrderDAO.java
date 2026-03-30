package dao.impl;

import dao.IOrderDAO;
import model.Order;
import model.OrderDetail;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO implements IOrderDAO {

    @Override
    public List<Order> findAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "select * from orders order by order_id desc";

        try (
                Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List<Order> findOrdersByUserId(int userId) {
        List<Order> list = new ArrayList<>();
        String sql = "select * from orders where user_id = ? order by order_id desc";

        try (
                Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapOrder(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public Order findById(int orderId) {
        String sql = "select * from orders where order_id = ?";

        try (
                Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapOrder(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean updateOrderStatus(int orderId, String newStatus) {
        Connection conn = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            Order currentOrder = findByIdForUpdate(conn, orderId);
            if (currentOrder == null) {
                conn.rollback();
                return false;
            }

            String oldStatus = currentOrder.getStatus();

            if ("CANCELLED".equalsIgnoreCase(oldStatus) || "DELIVERED".equalsIgnoreCase(oldStatus)) {
                conn.rollback();
                return false;
            }

            if ("CANCELLED".equalsIgnoreCase(newStatus)) {
                List<OrderDetail> details = findOrderDetailsByOrderId(conn, orderId);

                for (OrderDetail detail : details) {
                    boolean ok = increaseStock(conn, detail.getProductId(), detail.getQuantity());
                    if (!ok) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            String sql = "update orders set status = ? where order_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, newStatus);
                ps.setInt(2, orderId);

                int rows = ps.executeUpdate();
                if (rows <= 0) {
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

    @Override
    public List<OrderDetail> findOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = """
                select
                    od.order_detail_id,
                    od.order_id,
                    od.product_id,
                    od.quantity,
                    od.price as unit_price,
                    p.product_name,
                    p.storage,
                    p.color
                from order_details od
                join products p on od.product_id = p.product_id
                where od.order_id = ?
                order by od.order_detail_id asc
                """;

        try (
                Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetail detail = new OrderDetail();
                    detail.setOrderDetailId(rs.getInt("order_detail_id"));
                    detail.setOrderId(rs.getInt("order_id"));
                    detail.setProductId(rs.getInt("product_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setUnitPrice(rs.getDouble("unit_price"));
                    detail.setProductName(rs.getString("product_name"));
                    detail.setStorage(rs.getString("storage"));
                    detail.setColor(rs.getString("color"));
                    list.add(detail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private Order findByIdForUpdate(Connection conn, int orderId) throws SQLException {
        String sql = "select * from orders where order_id = ? for update";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapOrder(rs);
                }
            }
        }

        return null;
    }

    private List<OrderDetail> findOrderDetailsByOrderId(Connection conn, int orderId) throws SQLException {
        List<OrderDetail> list = new ArrayList<>();
        String sql = """
                select
                    od.order_detail_id,
                    od.order_id,
                    od.product_id,
                    od.quantity,
                    od.price as unit_price,
                    p.product_name,
                    p.storage,
                    p.color
                from order_details od
                join products p on od.product_id = p.product_id
                where od.order_id = ?
                order by od.order_detail_id asc
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetail detail = new OrderDetail();
                    detail.setOrderDetailId(rs.getInt("order_detail_id"));
                    detail.setOrderId(rs.getInt("order_id"));
                    detail.setProductId(rs.getInt("product_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setUnitPrice(rs.getDouble("unit_price"));
                    detail.setProductName(rs.getString("product_name"));
                    detail.setStorage(rs.getString("storage"));
                    detail.setColor(rs.getString("color"));
                    list.add(detail);
                }
            }
        }

        return list;
    }

    private boolean increaseStock(Connection conn, int productId, int quantity) throws SQLException {
        String sql = "update products set stock = stock + ? where product_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        }
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setUserId(rs.getInt("user_id"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        order.setStatus(rs.getString("status"));
        order.setCreatedAt(rs.getString("created_at"));
        return order;
    }
}