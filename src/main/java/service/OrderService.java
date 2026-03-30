package service;

import dao.IOrderDAO;
import dao.impl.OrderDAO;
import model.Order;
import model.OrderDetail;

import java.util.Collections;
import java.util.List;

public class OrderService {
    private final IOrderDAO orderDAO = new OrderDAO();
    private String lastMessage = "";

    public List<Order> getAllOrders() {
        return orderDAO.findAllOrders();
    }

    public List<Order> getOrdersByUserId(int userId) {
        if (userId <= 0) {
            return Collections.emptyList();
        }
        return orderDAO.findOrdersByUserId(userId);
    }

    public Order findById(int orderId) {
        if (orderId <= 0) {
            return null;
        }
        return orderDAO.findById(orderId);
    }

    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        if (orderId <= 0) {
            return Collections.emptyList();
        }
        return orderDAO.findOrderDetailsByOrderId(orderId);
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public List<String> getAllowedNextStatuses(String currentStatus) {
        if (currentStatus == null || currentStatus.trim().isEmpty()) {
            return Collections.emptyList();
        }

        switch (currentStatus.trim().toUpperCase()) {
            case "PENDING":
                return List.of("SHIPPING", "CANCELLED");
            case "SHIPPING":
                return List.of("DELIVERED", "CANCELLED");
            case "DELIVERED":
            case "CANCELLED":
            default:
                return Collections.emptyList();
        }
    }

    public boolean updateOrderStatus(int orderId, String newStatus) {
        lastMessage = "";

        if (orderId <= 0) {
            lastMessage = "Ma don hang khong hop le.";
            return false;
        }

        if (newStatus == null || newStatus.trim().isEmpty()) {
            lastMessage = "Trang thai moi khong hop le.";
            return false;
        }

        Order order = orderDAO.findById(orderId);
        if (order == null) {
            lastMessage = "Khong tim thay don hang.";
            return false;
        }

        String currentStatus = order.getStatus();
        String normalizedNewStatus = newStatus.trim().toUpperCase();
        List<String> allowedStatuses = getAllowedNextStatuses(currentStatus);

        if (allowedStatuses.isEmpty()) {
            lastMessage = "Don hang o trang thai " + currentStatus + " va khong the cap nhat them.";
            return false;
        }

        if (!allowedStatuses.contains(normalizedNewStatus)) {
            lastMessage = "Khong the chuyen tu " + currentStatus + " sang " + normalizedNewStatus + ".";
            return false;
        }

        boolean result = orderDAO.updateOrderStatus(orderId, normalizedNewStatus);

        if (result) {
            lastMessage = "Cap nhat trang thai thanh cong.";
        } else {
            lastMessage = "Cap nhat that bai do loi du lieu.";
        }

        return result;
    }

    public String getOrderProgress(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "Khong xac dinh";
        }

        switch (status.trim().toUpperCase()) {
            case "PENDING":
                return "Don hang dang cho xu ly";
            case "SHIPPING":
                return "Don hang dang giao";
            case "DELIVERED":
                return "Don hang da giao thanh cong";
            case "CANCELLED":
                return "Don hang da bi huy";
            default:
                return "Khong xac dinh";
        }
    }
}