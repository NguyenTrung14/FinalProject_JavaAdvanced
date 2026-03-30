package presentation;

import model.User;
import service.AuthService;

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
        User user = new User();

        System.out.print("Ho ten: ");
        user.setFullName(sc.nextLine().trim());
        System.out.print("Email: ");
        user.setEmail(sc.nextLine().trim());
        System.out.print("So dien thoai: ");
        user.setPhone(sc.nextLine().trim());
        System.out.print("Mat khau: ");
        user.setPassword(sc.nextLine().trim());
        System.out.print("Dia chi: ");
        user.setAddress(sc.nextLine().trim());
        user.setRole("CUSTOMER");

        boolean result = authService.register(user);
        System.out.println(authService.getLastMessage());

        if (result) {
            System.out.println("Tao tai khoan thanh cong.");
        }
    }
}