package service;

import dao.IUserDAO;
import dao.impl.UserDAO;
import model.User;
import util.ValidationUtil;

import java.util.List;

public class UserService {
    private final IUserDAO userDAO = new UserDAO();
    private String lastMessage = "";

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public User findById(int id) {
        if (id <= 0) {
            return null;
        }
        return userDAO.findById(id);
    }

    public boolean softDelete(int id) {
        if (id <= 0) {
            lastMessage = "Id khong hop le.";
            return false;
        }

        User user = userDAO.findById(id);
        if (user == null) {
            lastMessage = "Khong tim thay nguoi dung.";
            return false;
        }

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            lastMessage = "Khong duoc khoa tai khoan ADMIN.";
            return false;
        }

        boolean result = userDAO.softDelete(id);
        lastMessage = result ? "Khoa tai khoan thanh cong." : "Khoa tai khoan that bai.";
        return result;
    }

    public boolean updateProfile(User user) {
        if (user == null || user.getUserId() <= 0) {
            lastMessage = "Du lieu nguoi dung khong hop le.";
            return false;
        }

        user.setFullName(safeTrim(user.getFullName()));
        user.setEmail(safeTrim(user.getEmail()).toLowerCase());
        user.setPhone(safeTrim(user.getPhone()));
        user.setAddress(safeTrim(user.getAddress()));

        if (ValidationUtil.isEmpty(user.getFullName())
                || ValidationUtil.isEmpty(user.getEmail())
                || ValidationUtil.isEmpty(user.getPhone())
                || ValidationUtil.isEmpty(user.getAddress())) {
            lastMessage = "Khong duoc de trong thong tin.";
            return false;
        }

        if (!ValidationUtil.isValidEmail(user.getEmail())) {
            lastMessage = "Email khong dung dinh dang.";
            return false;
        }

        if (!ValidationUtil.isValidPhone(user.getPhone())) {
            lastMessage = "So dien thoai phai gom 10 chu so.";
            return false;
        }

        if (userDAO.existsByEmailExceptUserId(user.getEmail(), user.getUserId())) {
            lastMessage = "Email da ton tai.";
            return false;
        }

        if (userDAO.existsByPhoneExceptUserId(user.getPhone(), user.getUserId())) {
            lastMessage = "So dien thoai da ton tai.";
            return false;
        }

        boolean result = userDAO.updateProfile(user);
        lastMessage = result ? "Cap nhat thong tin thanh cong." : "Cap nhat thong tin that bai.";
        return result;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}