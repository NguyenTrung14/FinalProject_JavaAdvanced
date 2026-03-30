package service;

import model.Category;
import util.ValidationUtil;

import java.util.List;

import dao.impl.CategoryDAO;

public class CategoryService {
    private final CategoryDAO categoryDAO;

    public CategoryService() {
        categoryDAO = new CategoryDAO();
    }

    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    public Category getCategoryById(int id) {
        if (id <= 0) {
            return null;
        }
        return categoryDAO.findById(id);
    }

    public String addCategory(String categoryName, String description) {
        if (ValidationUtil.isEmpty(categoryName)) {
            return "Ten danh muc khong duoc de trong!";
        }

        if (categoryDAO.existsByName(categoryName)) {
            return "Ten danh muc da ton tai!";
        }

        Category category = new Category();
        category.setCategoryName(categoryName.trim());
        category.setDescription(description == null ? "" : description.trim());

        boolean result = categoryDAO.insert(category);
        return result ? "Them danh muc thanh cong!" : "Them danh muc that bai!";
    }

    public String updateCategory(int id, String newCategoryName, String newDescription) {
        if (id <= 0) {
            return "Id khong hop le!";
        }

        if (!categoryDAO.existsById(id)) {
            return "Id danh muc khong ton tai!";
        }

        if (ValidationUtil.isEmpty(newCategoryName)) {
            return "Ten danh muc khong duoc de trong!";
        }

        if (categoryDAO.existsByNameAndNotId(newCategoryName, id)) {
            return "Ten danh muc da ton tai!";
        }

        Category category = new Category();
        category.setCategoryId(id);
        category.setCategoryName(newCategoryName.trim());
        category.setDescription(newDescription == null ? "" : newDescription.trim());

        boolean result = categoryDAO.update(category);
        return result ? "Sua danh muc thanh cong!" : "Sua danh muc that bai!";
    }

    public String deleteCategory(int id) {
        if (id <= 0) {
            return "Id khong hop le!";
        }

        if (!categoryDAO.existsById(id)) {
            return "Id danh muc khong ton tai!";
        }

        boolean result = categoryDAO.softDelete(id);
        return result ? "Xoa danh muc thanh cong!" : "Xoa danh muc that bai!";
    }
}