package dao;

import model.Order;
import model.OrderDetail;

import java.util.List;

public interface IOrderDAO {
    List<Order> findAllOrders();

    List<Order> findOrdersByUserId(int userId);

    Order findById(int orderId);

    boolean updateOrderStatus(int orderId, String newStatus);

    List<OrderDetail> findOrderDetailsByOrderId(int orderId);
}