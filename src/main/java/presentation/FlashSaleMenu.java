package presentation;

import model.FlashSale;
import service.FlashSaleService;

import java.util.List;
import java.util.Scanner;

public class FlashSaleMenu {
    private final Scanner sc = new Scanner(System.in);
    private final FlashSaleService flashSaleService = new FlashSaleService();

    public void displayMenu() {
        while (true) {
            System.out.println("\n===== QUAN LY FLASH SALE =====");
            System.out.println("1. Xem danh sach flash sale");
            System.out.println("2. Them flash sale");
            System.out.println("0. Quay lai");
            System.out.print("Chon: ");

            int choice = inputInt();

            switch (choice) {
                case 1:
                    showAll();
                    break;
                case 2:
                    addFlashSale();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lua chon khong hop le.");
            }
        }
    }

    private void showAll() {
        List<FlashSale> list = flashSaleService.getAllFlashSales();

        if (list.isEmpty()) {
            System.out.println("Chua co flash sale nao.");
            return;
        }

        for (FlashSale f : list) {
            System.out.println("Ma FS: " + f.getFlashSaleId());
            System.out.println("Ma SP: " + f.getProductId());
            System.out.println("Giam %: " + f.getDiscountPercent());
            System.out.println("Bat dau: " + f.getStartTime());
            System.out.println("Ket thuc: " + f.getEndTime());
            System.out.println("So luong toi da: " + f.getMaxQuantity());
            System.out.println("Da ban: " + f.getSoldQuantity());
            System.out.println("Trang thai: " + f.getStatus());
            System.out.println("----------------------------");
        }
    }

    private void addFlashSale() {
        FlashSale fs = new FlashSale();

        System.out.print("Nhap ma san pham: ");
        fs.setProductId(inputInt());

        System.out.print("Nhap phan tram giam: ");
        fs.setDiscountPercent(inputDouble());

        System.out.print("Nhap thoi gian bat dau (yyyy-MM-dd HH:mm:ss): ");
        fs.setStartTime(sc.nextLine().trim());

        System.out.print("Nhap thoi gian ket thuc (yyyy-MM-dd HH:mm:ss): ");
        fs.setEndTime(sc.nextLine().trim());

        System.out.print("Nhap so luong toi da: ");
        fs.setMaxQuantity(inputInt());

        fs.setSoldQuantity(0);
        fs.setStatus("ACTIVE");

        boolean result = flashSaleService.addFlashSale(fs);
        System.out.println(result ? "Them flash sale thanh cong." : "Them flash sale that bai.");
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