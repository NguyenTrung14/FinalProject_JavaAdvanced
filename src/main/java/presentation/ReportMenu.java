package presentation;

import model.TopProductStat;
import service.ReportService;

import java.util.List;
import java.util.Scanner;

public class ReportMenu {
    private final Scanner sc = new Scanner(System.in);
    private final ReportService reportService = new ReportService();

    public void displayMenu() {
        System.out.print("Nhap nam: ");
        int year = inputInt();

        System.out.print("Nhap thang: ");
        int month = inputInt();

        List<TopProductStat> list = reportService.getTop5BestSellingProductsOfMonth(year, month);

        if (list.isEmpty()) {
            System.out.println("Khong co du lieu thong ke.");
            return;
        }

        System.out.println("\n===== TOP 5 SAN PHAM BAN CHAY =====");
        int index = 1;
        for (TopProductStat stat : list) {
            System.out.println(index + ". Ma SP: " + stat.getProductId());
            System.out.println("Ten SP: " + stat.getProductName());
            System.out.println("Dung luong: " + stat.getStorage());
            System.out.println("Mau sac: " + stat.getColor());
            System.out.println("So luong da ban: " + stat.getTotalSold());
            System.out.println("Doanh thu: " + stat.getRevenue());
            System.out.println("----------------------------");
            index++;
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
}