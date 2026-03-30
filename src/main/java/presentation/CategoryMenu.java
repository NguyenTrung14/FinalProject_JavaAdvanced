package presentation;

import model.Category;
import service.CategoryService;
import util.InputUtil;

import java.util.List;

public class CategoryMenu {
    private final CategoryService categoryService;

    public CategoryMenu() {
        categoryService = new CategoryService();
    }

    public void showCategoryMenu() {
        while (true) {
            System.out.println("\n===== quan ly danh muc =====");
            System.out.println("1. Hien thi danh sach danh muc");
            System.out.println("2. Them danh muc");
            System.out.println("3. Sua danh muc");
            System.out.println("4. Xoa danh muc");
            System.out.println("0. Quay lai");

            int choice = InputUtil.inputInt("Chon chuc nang: ");

            switch (choice) {
                case 1:
                    showAllCategories();
                    break;
                case 2:
                    addCategory();
                    break;
                case 3:
                    updateCategory();
                    break;
                case 4:
                    deleteCategory();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lua chon khong hop le!");
            }
        }
    }

    private void showAllCategories() {
        List<Category> categories = categoryService.getAllCategories();

        if (categories == null || categories.isEmpty()) {
            System.out.println("Khong co danh muc nao!");
            return;
        }

        System.out.println("\n===== Danh sach danh muc =====");
        System.out.printf("%-10s %-25s %-30s %-15s%n", "Id", "Ten danh muc", "Mo ta", "Trang thai");

        for (Category category : categories) {
            System.out.printf(
                    "%-10d %-25s %-30s %-15s%n",
                    category.getCategoryId(),
                    category.getCategoryName(),
                    category.getDescription() == null ? "" : category.getDescription(),
                    category.getStatus());
        }
    }

    private void addCategory() {
        System.out.println("\n===== Them danh muc =====");

        String categoryName = InputUtil.inputString("Nhap ten danh muc: ");
        String description = InputUtil.inputString("Nhap mo ta: ");

        String message = categoryService.addCategory(categoryName, description);
        System.out.println(message);
    }

    private void updateCategory() {
        System.out.println("\n===== Sua danh muc =====");

        int id = InputUtil.inputInt("Nhap id danh muc can sua: ");
        Category oldCategory = categoryService.getCategoryById(id);

        if (oldCategory == null) {
            System.out.println("Khong tim thay danh muc!");
            return;
        }

        System.out.println("Thong tin cu:");
        System.out.println("Id: " + oldCategory.getCategoryId());
        System.out.println("Ten danh muc: " + oldCategory.getCategoryName());
        System.out.println("Mo ta: " + (oldCategory.getDescription() == null ? "" : oldCategory.getDescription()));

        String newCategoryName = InputUtil.inputString("Nhap ten moi: ");
        String newDescription = InputUtil.inputString("Nhap mo ta moi: ");

        String message = categoryService.updateCategory(id, newCategoryName, newDescription);
        System.out.println(message);
    }

    private void deleteCategory() {
        System.out.println("\n===== Xoa danh muc =====");

        int id = InputUtil.inputInt("Nhap id danh muc can xoa: ");
        Category category = categoryService.getCategoryById(id);

        if (category == null) {
            System.out.println("Khong tim thay danh muc!");
            return;
        }

        System.out.println("Danh muc sap xoa: " + category.getCategoryName());
        boolean confirm = InputUtil.inputYesNo("Ban co chac chan muon xoa khong");

        if (!confirm) {
            System.out.println("Da huy thao tac xoa!");
            return;
        }

        String message = categoryService.deleteCategory(id);
        System.out.println(message);
    }
}