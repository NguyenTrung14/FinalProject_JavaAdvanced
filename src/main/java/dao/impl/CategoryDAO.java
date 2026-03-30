package dao.impl;

import model.Category;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.ICategoryDAO;

public class CategoryDAO implements ICategoryDAO {

    private String normalizeName(String categoryName) {
        return categoryName == null ? "" : categoryName.trim();
    }

    private String normalizeDescription(String description) {
        return description == null ? "" : description.trim();
    }

    @Override
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        String sql = "select category_id, category_name, description, status " +
                "from categories " +
                "where status = 'ACTIVE' " +
                "order by category_id";

        try (
                Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category category = new Category();
                category.setCategoryId(rs.getInt("category_id"));
                category.setCategoryName(rs.getString("category_name"));
                category.setDescription(rs.getString("description"));
                category.setStatus(rs.getString("status"));
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    @Override
    public Category findById(int id) {
        if (id <= 0) {
            return null;
        }

        String sql = "select category_id, category_name, description, status " +
                "from categories " +
                "where category_id = ? and status = 'ACTIVE'";

        try (
                Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category category = new Category();
                    category.setCategoryId(rs.getInt("category_id"));
                    category.setCategoryName(rs.getString("category_name"));
                    category.setDescription(rs.getString("description"));
                    category.setStatus(rs.getString("status"));
                    return category;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean existsById(int id) {
        if (id <= 0) {
            return false;
        }

        String sql = "select 1 from categories where category_id = ? and status = 'ACTIVE'";

        try (
                Connection conn = DBConnection.getInstance().getConnection();
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
    public boolean existsByName(String categoryName) {
        String normalizedName = normalizeName(categoryName);

        if (normalizedName.isEmpty()) {
            return false;
        }

        String sql = "select 1 " +
                "from categories " +
                "where lower(trim(category_name)) = lower(?) " +
                "and status = 'ACTIVE'";

        try (
                Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalizedName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean insert(Category category) {
        if (category == null) {
            return false;
        }

        String categoryName = normalizeName(category.getCategoryName());
        String description = normalizeDescription(category.getDescription());

        if (categoryName.isEmpty()) {
            return false;
        }

        String sql = "insert into categories(category_name, description, status) values (?, ?, 'ACTIVE')";

        try (
                Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoryName);
            ps.setString(2, description);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean update(Category category) {
        if (category == null || category.getCategoryId() <= 0) {
            return false;
        }

        String categoryName = normalizeName(category.getCategoryName());
        String description = normalizeDescription(category.getDescription());

        if (categoryName.isEmpty()) {
            return false;
        }

        String sql = "update categories " +
                "set category_name = ?, description = ? " +
                "where category_id = ? and status = 'ACTIVE'";

        try (
                Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoryName);
            ps.setString(2, description);
            ps.setInt(3, category.getCategoryId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean softDelete(int id) {
        if (id <= 0) {
            return false;
        }

        String sql = "update categories " +
                "set status = 'DELETED' " +
                "where category_id = ? and status = 'ACTIVE'";

        try (
                Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean existsByNameAndNotId(String categoryName, int id) {
        String normalizedName = normalizeName(categoryName);

        if (id <= 0 || normalizedName.isEmpty()) {
            return false;
        }

        String sql = "select 1 " +
                "from categories " +
                "where lower(trim(category_name)) = lower(?) " +
                "and category_id <> ? " +
                "and status = 'ACTIVE'";

        try (
                Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalizedName);
            ps.setInt(2, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}