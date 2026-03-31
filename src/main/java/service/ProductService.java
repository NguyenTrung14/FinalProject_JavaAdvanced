package service;

import dao.IProductDAO;
import dao.impl.FlashSaleDAO;
import dao.impl.ProductDAO;
import model.FlashSale;
import model.Product;
import util.DBConnection;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private final IProductDAO productDAO = new ProductDAO();
    private final FlashSaleDAO flashSaleDAO = new FlashSaleDAO();

    public List<Product> getAll(int page, int pageSize) {
        if (page <= 0 || pageSize <= 0) {
            return List.of();
        }
        return applyFlashSale(productDAO.findAll(page, pageSize));
    }

    public int getTotalPages(int pageSize) {
        if (pageSize <= 0) {
            return 0;
        }
        int total = productDAO.countActiveProducts();
        return (int) Math.ceil((double) total / pageSize);
    }

    public Product findById(int id) {
        Product product = productDAO.findById(id);
        return applyFlashSale(product);
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
        return applyFlashSale(productDAO.searchByName(keyword.trim()));
    }

    public List<Product> sortByPriceAsc() {
        return applyFlashSale(productDAO.sortByPriceAsc());
    }

    public List<Product> sortByPriceDesc() {
        return applyFlashSale(productDAO.sortByPriceDesc());
    }

    public List<Product> getAvailableProducts() {
        return applyFlashSale(productDAO.findAvailableProducts());
    }

    public List<Product> filterByCategory(int categoryId) {
        if (categoryId <= 0) {
            return List.of();
        }
        return applyFlashSale(productDAO.filterByCategory(categoryId));
    }

    public List<Product> filterByPrice(double minPrice, double maxPrice) {
        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            return List.of();
        }

        List<Product> products = applyFlashSale(productDAO.findAvailableProducts());
        List<Product> result = new ArrayList<>();

        for (Product product : products) {
            double finalPrice = product.getFinalPrice();
            if (finalPrice >= minPrice && finalPrice <= maxPrice) {
                result.add(product);
            }
        }

        return result;
    }

    private List<Product> applyFlashSale(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return List.of();
        }

        List<Product> result = new ArrayList<>();
        for (Product product : products) {
            result.add(applyFlashSale(product));
        }
        return result;
    }

    private Product applyFlashSale(Product product) {
        if (product == null) {
            return null;
        }

        product.setFlashSaleActive(false);
        product.setDiscountPercent(0);

        try (Connection conn = DBConnection.getInstance().getConnection()) {
            FlashSale flashSale = flashSaleDAO.findActiveByProductId(conn, product.getProductId());
            if (flashSale != null) {
                product.setFlashSaleActive(true);
                product.setDiscountPercent(flashSale.getDiscountPercent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return product;
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