package presentation;

import model.Order;
import model.OrderDetail;
import service.OrderService;

import java.util.List;
import java.util.Scanner;

public class OrderMenu {
    private final Scanner sc = new Scanner(System.in);
    private final OrderService orderService = new OrderService();

    public void displayMenu() {
        while (true) {
            System.out.println("\n===== QUAN LY DON HANG =====");
            System.out.println("1. Hien thi tat ca don hang");
            System.out.println("2. Xem chi tiet don hang");
            System.out.println("3. Cap nhat trang thai don hang");
            System.out.println("0. Quay lai");
            System.out.print("Chon: ");

            int choice = inputInt();

            switch (choice) {
                case 1:
                    showAllOrders();
                    break;
                case 2:
                    showOrderDetails();
                    break;
                case 3:
                    updateOrderStatus();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lua chon khong hop le.");
            }
        }
    }

    private void showAllOrders() {
        List<Order> orders = orderService.getAllOrders();

        if (orders == null || orders.isEmpty()) {
            System.out.println("Khong co don hang nao.");
            return;
        }

        System.out.println("\n===== DANH SACH DON HANG =====");
        for (Order order : orders) {
            printOrder(order);
            System.out.println("Tien do xu ly: " + orderService.getOrderProgress(order.getStatus()));
            System.out.println("----------------------------");
        }
    }

    private void showOrderDetails() {
        System.out.print("Nhap ma don hang: ");
        int orderId = inputInt();

        Order order = orderService.findById(orderId);
        if (order == null) {
            System.out.println("Khong tim thay don hang.");
            return;
        }

        System.out.println("\n===== THONG TIN DON HANG =====");
        printOrder(order);
        System.out.println("Tien do xu ly: " + orderService.getOrderProgress(order.getStatus()));

        List<OrderDetail> details = orderService.getOrderDetailsByOrderId(orderId);
        if (details == null || details.isEmpty()) {
            System.out.println("Don hang chua co chi tiet.");
            return;
        }

        System.out.println("\n----- CHI TIET SAN PHAM -----");
        for (OrderDetail detail : details) {
            System.out.println("Ma SP: " + detail.getProductId());
            System.out.println("Ten SP: " + detail.getProductName());
            System.out.println("Dung luong: " + detail.getStorage());
            System.out.println("Mau sac: " + detail.getColor());
            System.out.println("Don gia: " + detail.getUnitPrice());
            System.out.println("So luong: " + detail.getQuantity());
            System.out.println("Thanh tien: " + detail.getSubtotal());
            System.out.println("----------------------------");
        }
    }

    private void updateOrderStatus() {
        System.out.print("Nhap ma don hang: ");
        int orderId = inputInt();

        Order order = orderService.findById(orderId);
        if (order == null) {
            System.out.println("Khong tim thay don hang.");
            return;
        }

        printOrder(order);

        List<String> allowedStatuses = orderService.getAllowedNextStatuses(order.getStatus());
        if (allowedStatuses.isEmpty()) {
            System.out.println("Don hang nay khong the cap nhat them trang thai.");
            return;
        }

        System.out.println("Trang thai co the chuyen:");
        for (int i = 0; i < allowedStatuses.size(); i++) {
            System.out.println((i + 1) + ". " + allowedStatuses.get(i));
        }

        System.out.print("Chon trang thai moi: ");
        int choice = inputInt();

        if (choice < 1 || choice > allowedStatuses.size()) {
            System.out.println("Lua chon khong hop le.");
            return;
        }

        String newStatus = allowedStatuses.get(choice - 1);
        boolean result = orderService.updateOrderStatus(orderId, newStatus);

        System.out.println(orderService.getLastMessage());

        if (result) {
            Order updatedOrder = orderService.findById(orderId);
            if (updatedOrder != null) {
                System.out.println("\nThong tin sau khi cap nhat:");
                printOrder(updatedOrder);
            }
        }
    }

    private void printOrder(Order order) {
        System.out.println("Ma don: " + order.getOrderId());
        System.out.println("Ma khach hang: " + order.getUserId());
        System.out.println("Tong tien: " + order.getTotalAmount());
        System.out.println("Trang thai: " + order.getStatus());
        System.out.println("Ngay tao: " + order.getCreatedAt());
    }

    private int inputInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.print("Vui long nhap so nguyen: ");
            }
        }
    }
}