package dao;

import model.TopProductStat;
import java.util.List;

public interface IReportDAO {
    List<TopProductStat> getTop5BestSellingProducts(int month, int year);

    double getRevenueByMonth(int month, int year);
}