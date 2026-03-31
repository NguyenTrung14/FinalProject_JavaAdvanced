package presentation;

import model.CartItem;
import model.Order;
import model.OrderDetail;
import model.Product;
import model.User;
import service.OrderService;
import service.ProductService;
import service.ShoppingService;
import service.UserService;

import java.util.List;
import java.util.Scanner;

public class CustomerMenu {
    private final Scanner sc = new Scanner(System.in);
    private final ProductService productService = new ProductService();
    private final ShoppingService shoppingService = new ShoppingService();
    private final OrderService orderService = new OrderService();
    private final UserService userService = new UserService();

    public CustomerMenu() {
    }

    public void display(User currentUser) {
        int choice;
        do {
            System.out.println("\n========== MENU KHACH HANG ==========");
            System.out.println("Xin chao: " + currentUser.getFullName());
            System.out.println("1. Hien thi san pham con hang");
            System.out.println("2. Loc san pham theo hang");
            System.out.println("3. Loc san pham theo gia");
            System.out.println("4. Tim kiem san pham theo ten");
            System.out.println("5. Sap xep gia tang dan");
            System.out.println("6. Sap xep gia giam dan");
            System.out.println("7. Them san pham vao gio hang");
            System.out.println("8. Xem gio hang");
            System.out.println("9. Xoa san pham khoi gio hang");
            System.out.println("10. Dat hang");
            System.out.println("11. Xem lich su don hang");
            System.out.println("12. Xem chi tiet don hang");
            System.out.println("13. Cap nhat thong tin ca nhan");
            System.out.println("0. Dang xuat");
            System.out.print("Chon: ");

            choice = inputInt();

            switch (choice) {
                case 1:
                    showProducts(productService.getAvailableProducts());
                    break;
                case 2:
                    filterByCategory();
                    break;
                case 3:
                    filterByPrice();
                    break;
                case 4:
                    searchProductByName();
                    break;
                case 5:
                    showProducts(productService.sortByPriceAsc());
                    break;
                case 6:
                    showProducts(productService.sortByPriceDesc());
                    break;
                case 7:
                    addToCart(currentUser);
                    break;
                case 8:
                    showCart(currentUser);
                    break;
                case 9:
                    removeFromCart(currentUser);
                    break;
                case 10:
                    checkout(currentUser);
                    break;
                case 11:
                    showOrders(currentUser);
                    break;
                case 12:
                    showOrderDetailsOfCurrentUser(currentUser);
                    break;
                case 13:
                    updateProfile(currentUser);
                    break;
                case 0:
                    System.out.println("Dang xuat thanh cong.");
                    break;
                default:
                    System.out.println("Lua chon khong hop le.");
            }
        } while (choice != 0);
    }

    private void updateProfile(User currentUser) {
        System.out.println("\n===== CAP NHAT THONG TIN CA NHAN =====");
        System.out.println("Thong tin hien tai:");
        System.out.println("Ho ten: " + currentUser.getFullName());
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println("So dien thoai: " + currentUser.getPhone());
        System.out.println("Dia chi: " + currentUser.getAddress());

        User updatedUser = new User();
        updatedUser.setUserId(currentUser.getUserId());

        System.out.print("Nhap ho ten moi: ");
        updatedUser.setFullName(sc.nextLine().trim());

        System.out.print("Nhap email moi: ");
        updatedUser.setEmail(sc.nextLine().trim());

        System.out.print("Nhap so dien thoai moi: ");
        updatedUser.setPhone(sc.nextLine().trim());

        System.out.print("Nhap dia chi moi: ");
        updatedUser.setAddress(sc.nextLine().trim());

        boolean result = userService.updateProfile(updatedUser);
        System.out.println(userService.getLastMessage());

        if (result) {
            currentUser.setFullName(updatedUser.getFullName());
            currentUser.setEmail(updatedUser.getEmail());
            currentUser.setPhone(updatedUser.getPhone());
            currentUser.setAddress(updatedUser.getAddress());
        }
    }

    private void filterByCategory() {
        System.out.print("Nhap ma hang / category id: ");
        int categoryId = inputInt();
        List<Product> products = productService.filterByCategory(categoryId);
        showProducts(products);
    }

    private void filterByPrice() {
        System.out.print("Nhap gia min: ");
        double min = inputDouble();

        System.out.print("Nhap gia max: ");
        double max = inputDouble();

        if (min > max) {
            System.out.println("Gia min khong duoc lon hon gia max.");
            return;
        }

        List<Product> products = productService.filterByPrice(min, max);
        showProducts(products);
    }

    private void searchProductByName() {
        System.out.print("Nhap ten san pham can tim: ");
        String keyword = sc.nextLine().trim();
        List<Product> products = productService.searchByName(keyword);
        showProducts(products);
    }

    private void addToCart(User currentUser) {
        System.out.print("Nhap ma san pham: ");
        int productId = inputInt();

        Product product = productService.findById(productId);
        if (product == null) {
            System.out.println("Khong tim thay san pham.");
            return;
        }

        if (!"ACTIVE".equalsIgnoreCase(product.getStatus())) {
            System.out.println("San pham khong hoat dong.");
            return;
        }

        if (product.getStock() <= 0) {
            System.out.println("San pham da het hang.");
            return;
        }

        System.out.print("Nhap so luong muon mua: ");
        int quantity = inputInt();

        if (quantity <= 0) {
            System.out.println("So luong phai lon hon 0.");
            return;
        }

        if (quantity > product.getStock()) {
            System.out.println("So luong vuot qua ton kho.");
            return;
        }

        boolean result = shoppingService.addToCartAndReserve(currentUser.getUserId(), productId, quantity);
        System.out.println(result ? "Them vao gio hang thanh cong." : "Them vao gio hang that bai.");
    }

    private void showCart(User currentUser) {
        List<CartItem> cartItems = shoppingService.getReservedCartItems(currentUser.getUserId());
        if (cartItems == null || cartItems.isEmpty()) {
            System.out.println("Gio hang dang trong.");
            return;
        }

        System.out.println("\n========== GIO HANG ==========");
        double total = 0;

        for (CartItem item : cartItems) {
            Product p = item.getProduct();

            System.out.println("Ma SP: " + p.getProductId());
            System.out.println("Ten SP: " + p.getProductName());
            System.out.println("Dung luong: " + p.getStorage());
            System.out.println("Mau sac: " + p.getColor());
            System.out.println("Tong so luong: " + item.getQuantity());

            if (item.getReservedFlashQuantity() > 0) {
                System.out.println("So luong flash sale: " + item.getReservedFlashQuantity());
                System.out.println("Gia flash sale: " + item.getFlashUnitPrice());
                System.out.println("Tien flash sale: " + (item.getReservedFlashQuantity() * item.getFlashUnitPrice()));
            }

            if (item.getReservedNormalQuantity() > 0) {
                System.out.println("So luong gia goc: " + item.getReservedNormalQuantity());
                System.out.println("Gia goc: " + item.getNormalUnitPrice());
                System.out.println("Tien gia goc: " + (item.getReservedNormalQuantity() * item.getNormalUnitPrice()));
            }

            System.out.println("Thanh tien: " + item.getSubtotal());
            System.out.println("--------------------------------");
            total += item.getSubtotal();
        }

        System.out.println("Tong tien gio hang: " + total);
    }

    private void removeFromCart(User currentUser) {
        List<CartItem> cartItems = shoppingService.getReservedCartItems(currentUser.getUserId());
        if (cartItems == null || cartItems.isEmpty()) {
            System.out.println("Gio hang dang trong.");
            return;
        }

        showCart(currentUser);
        System.out.print("Nhap ma san pham can xoa khoi gio: ");
        int productId = inputInt();

        boolean result = shoppingService.removeCartItemAndRestore(currentUser.getUserId(), productId);
        System.out.println(result
                ? "Xoa san pham khoi gio hang thanh cong."
                : "Khong tim thay san pham trong gio hang.");
    }

    private void checkout(User currentUser) {
        List<CartItem> cartItems = shoppingService.getReservedCartItems(currentUser.getUserId());
        if (cartItems == null || cartItems.isEmpty()) {
            System.out.println("Gio hang dang trong.");
            return;
        }

        showCart(currentUser);

        System.out.print("Nhap coupon (bo trong neu khong co): ");
        String couponCode = sc.nextLine().trim();

        System.out.print("Xac nhan dat hang (Y/N): ");
        String confirm = sc.nextLine().trim();

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Da huy dat hang.");
            return;
        }

        boolean result = shoppingService.checkoutReservedCart(
                currentUser.getUserId(),
                couponCode.isEmpty() ? null : couponCode);

        if (result) {
            System.out.println("Dat hang thanh cong.");
        } else {
            System.out.println("Dat hang that bai hoac coupon khong hop le.");
        }
    }

    private void showOrders(User currentUser) {
        List<Order> orders = orderService.getOrdersByUserId(currentUser.getUserId());
        if (orders == null || orders.isEmpty()) {
            System.out.println("Chua co don hang nao.");
            return;
        }

        System.out.println("\n========== LICH SU DON HANG ==========");
        for (Order order : orders) {
            System.out.println("Ma don hang: " + order.getOrderId());
            System.out.println("Tong tien: " + order.getTotalAmount());
            System.out.println("Trang thai: " + order.getStatus());
            System.out.println("Tien do xu ly: " + orderService.getOrderProgress(order.getStatus()));
            System.out.println("Ngay tao: " + order.getCreatedAt());
            System.out.println("--------------------------------");
        }
    }

    private void showOrderDetailsOfCurrentUser(User currentUser) {
        System.out.print("Nhap ma don hang can xem chi tiet: ");
        int orderId = inputInt();

        Order order = orderService.findById(orderId);
        if (order == null) {
            System.out.println("Khong tim thay don hang.");
            return;
        }

        if (order.getUserId() != currentUser.getUserId()) {
            System.out.println("Ban khong duoc xem don hang cua nguoi khac.");
            return;
        }

        System.out.println("\n========== CHI TIET DON HANG ==========");
        System.out.println("Ma don hang: " + order.getOrderId());
        System.out.println("Tong tien: " + order.getTotalAmount());
        System.out.println("Trang thai: " + order.getStatus());
        System.out.println("Tien do xu ly: " + orderService.getOrderProgress(order.getStatus()));
        System.out.println("Ngay tao: " + order.getCreatedAt());
        System.out.println("--------------------------------");

        List<OrderDetail> details = orderService.getOrderDetailsByOrderId(orderId);
        if (details == null || details.isEmpty()) {
            System.out.println("Khong co chi tiet don hang.");
            return;
        }

        for (OrderDetail detail : details) {
            System.out.println("Ma SP: " + detail.getProductId());
            System.out.println("Ten SP: " + detail.getProductName());
            System.out.println("Dung luong: " + detail.getStorage());
            System.out.println("Mau sac: " + detail.getColor());
            System.out.println("Don gia trung binh: " + detail.getUnitPrice());
            System.out.println("So luong flash sale: " + detail.getFlashSaleQuantity());
            System.out.println("So luong gia goc: " + detail.getNormalQuantity());
            System.out.println("Tong so luong: " + detail.getQuantity());
            System.out.println("Thanh tien: " + detail.getSubtotal());
            System.out.println("--------------------------------");
        }
    }

    private void showProducts(List<Product> products) {
        if (products == null || products.isEmpty()) {
            System.out.println("Khong co san pham nao.");
            return;
        }

        System.out.println("\n========== DANH SACH SAN PHAM ==========");
        for (Product p : products) {
            System.out.println("Ma SP: " + p.getProductId());
            System.out.println("Ten SP: " + p.getProductName());
            System.out.println("Dung luong: " + p.getStorage());
            System.out.println("Mau sac: " + p.getColor());

            if (p.isFlashSaleActive()) {
                System.out.println("Gia goc: " + p.getPrice());
                System.out.println("Flash sale: -" + p.getDiscountPercent() + "%");
                System.out.println("Gia sau giam: " + p.getFinalPrice());
                System.out.println("So luong uu dai con lai: " + p.getFlashSaleRemainingQuantity());
            } else {
                System.out.println("Gia: " + p.getPrice());
            }

            System.out.println("Ton kho: " + p.getStock());
            System.out.println("Mo ta: " + p.getDescription());
            System.out.println("Ma hang: " + p.getCategoryId());
            System.out.println("--------------------------------");
        }
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

    private double inputDouble() {
        while (true) {
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.print("Vui long nhap so: ");
            }
        }
    }
}