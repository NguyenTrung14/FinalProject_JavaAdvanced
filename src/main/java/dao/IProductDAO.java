package dao;

import model.Product;
import java.util.List;

public interface IProductDAO {
    List<Product> findAll(int page, int pageSize);

    int countActiveProducts();

    Product findById(int id);

    boolean existsById(int id);

    boolean insert(Product product);

    boolean update(Product product);

    boolean softDelete(int id);

    List<Product> searchByName(String keyword);

    List<Product> sortByPriceAsc();

    List<Product> sortByPriceDesc();

    List<Product> findAvailableProducts();

    List<Product> filterByCategory(int categoryId);

    List<Product> filterByPrice(double minPrice, double maxPrice);
}