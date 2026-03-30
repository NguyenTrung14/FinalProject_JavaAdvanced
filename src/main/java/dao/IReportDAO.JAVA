package dao;

import model.ReportItem;
import java.util.List;

public interface IReportDAO {
    List<ReportItem> getTop5BestSellingProducts(int month, int year);
    double getRevenueByMonth(int month, int year);
}