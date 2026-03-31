package presentation;

import model.User;
import service.AuthService;
import util.ValidationUtil;

import java.util.Scanner;

public class AuthUI {
    private final Scanner sc = new Scanner(System.in);
    private final AuthService authService = new AuthService();

    public User login() {
        System.out.println("\n===== Dang nhap =====");
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Mat khau: ");
        String password = sc.nextLine().trim();

        User user = authService.login(email, password);
        if (user == null) {
            System.out.println(authService.getLastMessage());
        } else {
            System.out.println("Dang nhap thanh cong. Xin chao " + user.getFullName() + ".");
        }
        return user;
    }

    public void register() {
        System.out.println("\n===== Dang ky =====");

        while (true) {
            User user = new User();

            while (true) {
                System.out.print("Ho ten: ");
                String fullName = sc.nextLine().trim();

                if (ValidationUtil.isEmpty(fullName)) {
                    System.out.println("Ho ten khong duoc de trong.");
                    if (askExit()) return;
                } else {
                    user.setFullName(fullName);
                    break;
                }
            }

            while (true) {
                System.out.print("Email: ");
                String email = sc.nextLine().trim();

                if (ValidationUtil.isEmpty(email)) {
                    System.out.println("Email khong duoc de trong.");
                    if (askExit()) return;
                } else if (!ValidationUtil.isValidEmail(email)) {
                    System.out.println("Email khong hop le.");
                    if (askExit()) return;
                } else {
                    user.setEmail(email);
                    break;
                }
            }

            while (true) {
                System.out.print("So dien thoai: ");
                String phone = sc.nextLine().trim();

                if (ValidationUtil.isEmpty(phone)) {
                    System.out.println("So dien thoai khong duoc de trong.");
                    if (askExit()) return;
                } else if (!ValidationUtil.isValidPhone(phone)) {
                    System.out.println("So dien thoai phai la 10 chu so.");
                    if (askExit()) return;
                } else {
                    user.setPhone(phone);
                    break;
                }
            }

            while (true) {
                System.out.print("Mat khau: ");
                String password = sc.nextLine().trim();

                if (ValidationUtil.isEmpty(password)) {
                    System.out.println("Mat khau khong duoc de trong.");
                    if (askExit()) return;
                } else if (!ValidationUtil.isStrongPassword(password)) {
                    System.out.println("Mat khau phai toi thieu 6 ky tu, gom chu va so.");
                    if (askExit()) return;
                } else {
                    user.setPassword(password);
                    break;
                }
            }

            while (true) {
                System.out.print("Dia chi: ");
                String address = sc.nextLine().trim();

                if (ValidationUtil.isEmpty(address)) {
                    System.out.println("Dia chi khong duoc de trong.");
                    if (askExit()) return;
                } else {
                    user.setAddress(address);
                    break;
                }
            }

            user.setRole("CUSTOMER");

            boolean result = authService.register(user);
            System.out.println(authService.getLastMessage());

            if (result) {
                System.out.println("Dang ky thanh cong.");
                return;
            }

            System.out.print("Ban co muon nhap lai thong tin dang ky khong? (y/n): ");
            String choice = sc.nextLine().trim();
            if (!choice.equalsIgnoreCase("y")) {
                System.out.println("Da thoat dang ky.");
                return;
            }
        }
    }

    private boolean askExit() {
        System.out.print("Ban co muon thoat dang ky? (y/n): ");
        String choice = sc.nextLine().trim();
        return choice.equalsIgnoreCase("y");
    }
}