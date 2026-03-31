package service;

import dao.IReportDAO;
import dao.impl.ReportDAO;
import model.TopProductStat;

import java.util.List;

public class ReportService {
    private final IReportDAO reportDAO = new ReportDAO();

    public List<TopProductStat> getTop5BestSellingProducts(int month, int year) {
        if (year <= 0 || month < 1 || month > 12) {
            return List.of();
        }
        return reportDAO.getTop5BestSellingProducts(month, year);
    }

    public double getRevenueByMonth(int month, int year) {
        if (year <= 0 || month < 1 || month > 12) {
            return 0;
        }
        return reportDAO.getRevenueByMonth(month, year);
    }
}