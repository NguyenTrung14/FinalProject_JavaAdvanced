package service;

import model.TopProductStat;

import java.util.List;

import dao.impl.ReportDAO;

public class ReportService {
    private final ReportDAO reportDAO = new ReportDAO();

    public List<TopProductStat> getTop5BestSellingProductsOfMonth(int year, int month) {
        if (year <= 0 || month < 1 || month > 12) {
            return List.of();
        }
        return reportDAO.getTop5BestSellingProductsOfMonth(year, month);
    }
}