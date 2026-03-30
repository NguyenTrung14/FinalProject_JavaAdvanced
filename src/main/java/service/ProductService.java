package service;

import dao.IProductDAO;
import dao.impl.ProductDAO;
import model.Product;

import java.util.List;

public class ProductService {
    private final IProductDAO productDAO = new ProductDAO();

    public List<Product> getAll(int page, int pageSize) {
        if (page <= 0 || pageSize <= 0) {
            return List.of();
        }
        return productDAO.findAll(page, pageSize);
    }

    public int getTotalPages(int pageSize) {
        if (pageSize <= 0) {
            return 0;
        }
        int total = productDAO.countActiveProducts();
        return (int) Math.ceil((double) total / pageSize);
    }

    public Product findById(int id) {
        return productDAO.findById(id);
    }

    public boolean addProduct(Product p) {
        if (!isValidProduct(p)) {
            return false;
        }
        return productDAO.insert(p);
    }

    public boolean updateProduct(Product p) {
        if (p == null || p.getProductId() <= 0) {
            return false;
        }
        if (!isValidProduct(p)) {
            return false;
        }
        return productDAO.update(p);
    }

    public boolean deleteProduct(int id) {
        if (id <= 0) {
            return false;
        }
        return productDAO.softDelete(id);
    }

    public List<Product> searchByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        return productDAO.searchByName(keyword.trim());
    }

    public List<Product> sortByPriceAsc() {
        return productDAO.sortByPriceAsc();
    }

    public List<Product> sortByPriceDesc() {
        return productDAO.sortByPriceDesc();
    }

    public List<Product> getAvailableProducts() {
        return productDAO.findAvailableProducts();
    }

    public List<Product> filterByCategory(int categoryId) {
        if (categoryId <= 0) {
            return List.of();
        }
        return productDAO.filterByCategory(categoryId);
    }

    public List<Product> filterByPrice(double minPrice, double maxPrice) {
        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            return List.of();
        }
        return productDAO.filterByPrice(minPrice, maxPrice);
    }

    private boolean isValidProduct(Product p) {
        if (p == null)
            return false;
        if (p.getProductName() == null || p.getProductName().trim().isEmpty())
            return false;
        if (p.getStorage() == null || p.getStorage().trim().isEmpty())
            return false;
        if (p.getColor() == null || p.getColor().trim().isEmpty())
            return false;
        if (p.getDescription() == null || p.getDescription().trim().isEmpty())
            return false;
        if (p.getPrice() <= 0)
            return false;
        if (p.getStock() < 0)
            return false;
        return p.getCategoryId() > 0;
    }
}