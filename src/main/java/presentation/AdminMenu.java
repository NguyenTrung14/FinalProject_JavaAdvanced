package presentation;

import model.User;

import java.util.Scanner;

public class AdminMenu {
    private final Scanner sc = new Scanner(System.in);
    private final ProductMenu productMenu = new ProductMenu();
    private final CategoryMenu categoryMenu = new CategoryMenu();
    private final OrderMenu orderMenu = new OrderMenu();
    private final FlashSaleMenu flashSaleMenu = new FlashSaleMenu();
    private final CouponMenu couponMenu = new CouponMenu();
    private final ReportMenu reportMenu = new ReportMenu();
    private final UserMenu userMenu = new UserMenu();

    public void display(User currentUser) {
        int choice;
        do {
            System.out.println("\n========== MENU ADMIN ==========");
            System.out.println("Xin chao: " + currentUser.getFullName());
            System.out.println("1. Quan ly san pham");
            System.out.println("2. Quan ly danh muc");
            System.out.println("3. Quan ly don hang");
            System.out.println("4. Quan ly nguoi dung");
            System.out.println("5. Quan ly flash sale");
            System.out.println("6. Quan ly coupon");
            System.out.println("7. Top 5 san pham ban chay");
            System.out.println("0. Dang xuat");
            System.out.print("Chon: ");

            choice = inputInt();

            switch (choice) {
                case 1:
                    productMenu.displayMenu();
                    break;
                case 2:
                    categoryMenu.showCategoryMenu();
                    break;
                case 3:
                    orderMenu.displayMenu();
                    break;
                case 4:
                    userMenu.displayMenu();
                    break;
                case 5:
                    flashSaleMenu.displayMenu();
                    break;
                case 6:
                    couponMenu.displayMenu();
                    break;
                case 7:
                    reportMenu.displayMenu();
                    break;
                case 0:
                    System.out.println("Dang xuat thanh cong.");
                    break;
                default:
                    System.out.println("Lua chon khong hop le.");
            }
        } while (choice != 0);
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