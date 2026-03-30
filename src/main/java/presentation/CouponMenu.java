package presentation;

import model.Coupon;
import service.CouponService;

import java.util.List;
import java.util.Scanner;

public class CouponMenu {
    private final Scanner sc = new Scanner(System.in);
    private final CouponService couponService = new CouponService();

    public void displayMenu() {
        while (true) {
            System.out.println("\n===== QUAN LY COUPON =====");
            System.out.println("1. Xem danh sach coupon");
            System.out.println("2. Them coupon");
            System.out.println("0. Quay lai");
            System.out.print("Chon: ");

            int choice = inputInt();

            switch (choice) {
                case 1:
                    showAll();
                    break;
                case 2:
                    addCoupon();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lua chon khong hop le.");
            }
        }
    }

    private void showAll() {
        List<Coupon> list = couponService.getAllCoupons();

        if (list.isEmpty()) {
            System.out.println("Chua co coupon nao.");
            return;
        }

        for (Coupon c : list) {
            System.out.println("Ma coupon: " + c.getCouponId());
            System.out.println("Code: " + c.getCode());
            System.out.println("Giam %: " + c.getDiscountPercent());
            System.out.println("Bat dau: " + c.getStartTime());
            System.out.println("Ket thuc: " + c.getEndTime());
            System.out.println("So luong: " + c.getQuantity());
            System.out.println("Da dung: " + c.getUsedCount());
            System.out.println("Don toi thieu: " + c.getMinOrderAmount());
            System.out.println("Trang thai: " + c.getStatus());
            System.out.println("----------------------------");
        }
    }

    private void addCoupon() {
        Coupon coupon = new Coupon();

        System.out.print("Nhap ma coupon: ");
        coupon.setCode(sc.nextLine().trim().toUpperCase());

        System.out.print("Nhap phan tram giam: ");
        coupon.setDiscountPercent(inputDouble());

        System.out.print("Nhap thoi gian bat dau (yyyy-MM-dd HH:mm:ss): ");
        coupon.setStartTime(sc.nextLine().trim());

        System.out.print("Nhap thoi gian ket thuc (yyyy-MM-dd HH:mm:ss): ");
        coupon.setEndTime(sc.nextLine().trim());

        System.out.print("Nhap tong so luot dung: ");
        coupon.setQuantity(inputInt());

        coupon.setUsedCount(0);

        System.out.print("Nhap gia tri don toi thieu: ");
        coupon.setMinOrderAmount(inputDouble());

        coupon.setStatus("ACTIVE");

        boolean result = couponService.addCoupon(coupon);
        System.out.println(result ? "Them coupon thanh cong." : "Them coupon that bai.");
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