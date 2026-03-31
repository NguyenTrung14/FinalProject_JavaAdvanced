package dao.impl;

import dao.IProductDAO;
import model.Product;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO implements IProductDAO {

    private static final String PRODUCT_SELECT_WITH_FLASH_SALE = """
            select
                p.product_id,
                p.product_name,
                p.storage,
                p.color,
                p.price,
                p.stock,
                p.description,
                p.category_id,
                p.status,
                p.created_at,
                fs.flash_sale_id,
                fs.discount_percent,
                (fs.max_quantity - fs.sold_quantity) as remaining_quantity
            from products p
            left join flash_sales fs
                on fs.product_id = p.product_id
                and fs.status = 'ACTIVE'
                and now() between fs.start_time and fs.end_time
                and fs.sold_quantity < fs.max_quantity
            """;

    @Override
    public List<Product> findAll(int page, int pageSize) {
        List<Product> list = new ArrayList<>();
        String sql = PRODUCT_SELECT_WITH_FLASH_SALE +
                " where p.status = 'ACTIVE' order by p.product_id limit ? offset ?";

        int offset = (page - 1) * pageSize;

        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pageSize);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public int countActiveProducts() {
        String sql = "select count(*) from products where status = 'ACTIVE'";
        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Product findById(int id) {
        String sql = PRODUCT_SELECT_WITH_FLASH_SALE +
                " where p.product_id = ? and p.status = 'ACTIVE'";

        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean existsById(int id) {
        String sql = "select 1 from products where product_id = ? and status = 'ACTIVE'";
        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean insert(Product p) {
        String sql = """
                insert into products(product_name, storage, color, price, stock, description, category_id, status)
                values (?, ?, ?, ?, ?, ?, ?, 'ACTIVE')
                """;

        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getProductName().trim());
            ps.setString(2, p.getStorage().trim());
            ps.setString(3, p.getColor().trim());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getStock());
            ps.setString(6, p.getDescription().trim());
            ps.setInt(7, p.getCategoryId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean update(Product p) {
        String sql = """
                update products
                set product_name = ?, storage = ?, color = ?, price = ?, stock = ?, description = ?, category_id = ?
                where product_id = ? and status = 'ACTIVE'
                """;

        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getProductName().trim());
            ps.setString(2, p.getStorage().trim());
            ps.setString(3, p.getColor().trim());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getStock());
            ps.setString(6, p.getDescription().trim());
            ps.setInt(7, p.getCategoryId());
            ps.setInt(8, p.getProductId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean softDelete(int id) {
        String sql = "update products set status = 'DELETED' where product_id = ? and status = 'ACTIVE'";
        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<Product> searchByName(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = PRODUCT_SELECT_WITH_FLASH_SALE +
                " where p.product_name like ? and p.status = 'ACTIVE' order by p.product_id";

        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List<Product> sortByPriceAsc() {
        return getSortedProducts("asc");
    }

    @Override
    public List<Product> sortByPriceDesc() {
        return getSortedProducts("desc");
    }

    private List<Product> getSortedProducts(String order) {
        List<Product> list = new ArrayList<>();
        String sql = PRODUCT_SELECT_WITH_FLASH_SALE +
                " where p.status = 'ACTIVE' order by p.price " + order;

        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List<Product> findAvailableProducts() {
        List<Product> list = new ArrayList<>();
        String sql = PRODUCT_SELECT_WITH_FLASH_SALE +
                " where p.status = 'ACTIVE' and p.stock > 0 order by p.product_id";

        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List<Product> filterByCategory(int categoryId) {
        List<Product> list = new ArrayList<>();
        String sql = PRODUCT_SELECT_WITH_FLASH_SALE +
                " where p.status = 'ACTIVE' and p.stock > 0 and p.category_id = ? order by p.product_id";

        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List<Product> filterByPrice(double minPrice, double maxPrice) {
        List<Product> list = new ArrayList<>();
        String sql = PRODUCT_SELECT_WITH_FLASH_SALE +
                " where p.status = 'ACTIVE' and p.stock > 0 and p.price between ? and ? order by p.price asc";

        try (Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, minPrice);
            ps.setDouble(2, maxPrice);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private Product mapResultSet(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setProductName(rs.getString("product_name"));
        p.setStorage(rs.getString("storage"));
        p.setColor(rs.getString("color"));
        p.setPrice(rs.getDouble("price"));
        p.setStock(rs.getInt("stock"));
        p.setDescription(rs.getString("description"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setStatus(rs.getString("status"));
        p.setCreatedAt(rs.getString("created_at"));

        int flashSaleId = rs.getInt("flash_sale_id");
        if (!rs.wasNull()) {
            p.setFlashSaleActive(true);
            p.setDiscountPercent(rs.getDouble("discount_percent"));
            p.setFlashSaleRemainingQuantity(rs.getInt("remaining_quantity"));
        } else {
            p.setFlashSaleActive(false);
            p.setDiscountPercent(0);
            p.setFlashSaleRemainingQuantity(0);
        }

        return p;
    }
}