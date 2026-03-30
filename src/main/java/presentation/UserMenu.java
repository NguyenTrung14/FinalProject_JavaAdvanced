package presentation;

import model.User;
import service.UserService;

import java.util.List;
import java.util.Scanner;

public class UserMenu {
    private final Scanner sc = new Scanner(System.in);
    private final UserService userService = new UserService();

    public void displayMenu() {
        int choice;
        do {
            System.out.println("\n========== Quan ly nguoi dung ==========");
            System.out.println("1. Hien thi danh sach nguoi dung");
            System.out.println("2. Tim nguoi dung theo id");
            System.out.println("3. Khoa tai khoan nguoi dung");
            System.out.println("0. Quay lai");
            System.out.print("Chon: ");

            choice = inputInt();

            switch (choice) {
                case 1:
                    showAllUsers();
                    break;
                case 2:
                    findUserById();
                    break;
                case 3:
                    softDeleteUser();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Lua chon khong hop le.");
            }
        } while (choice != 0);
    }

    private void showAllUsers() {
        List<User> users = userService.getAllUsers();

        if (users == null || users.isEmpty()) {
            System.out.println("Khong co nguoi dung nao.");
            return;
        }

        System.out.println("\n========== Danh sach nguoi dung ==========");
        for (User user : users) {
            printUser(user);
            System.out.println("--------------------------------");
        }
    }

    private void findUserById() {
        System.out.print("Nhap id nguoi dung: ");
        int id = inputInt();

        User user = userService.findById(id);
        if (user == null) {
            System.out.println("Khong tim thay nguoi dung.");
            return;
        }

        printUser(user);
    }

    private void softDeleteUser() {
        System.out.print("Nhap id nguoi dung can khoa: ");
        int id = inputInt();

        User user = userService.findById(id);
        if (user == null) {
            System.out.println("Khong tim thay nguoi dung.");
            return;
        }

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            System.out.println("Khong duoc khoa tai khoan ADMIN.");
            return;
        }

        printUser(user);
        System.out.print("Xac nhan khoa tai khoan (Y/N): ");
        String confirm = sc.nextLine().trim();

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Da huy thao tac.");
            return;
        }

        boolean result = userService.softDelete(id);
        System.out.println(result ? "Khoa tai khoan thanh cong." : "Khoa tai khoan that bai.");
    }

    private void printUser(User user) {
        System.out.println("Id: " + user.getUserId());
        System.out.println("Ho ten: " + user.getFullName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("So dien thoai: " + user.getPhone());
        System.out.println("Dia chi: " + user.getAddress());
        System.out.println("Role: " + user.getRole());
        System.out.println("Trang thai: " + user.getStatus());
        System.out.println("Ngay tao: " + user.getCreatedAt());
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