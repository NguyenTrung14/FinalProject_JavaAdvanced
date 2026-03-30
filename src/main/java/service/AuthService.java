package service;

import dao.IUserDAO;
import dao.impl.UserDAO;
import model.User;
import util.PasswordUtil;
import util.ValidationUtil;

public class AuthService {
    private final IUserDAO userDAO = new UserDAO();
    private String lastMessage = "";

    public boolean register(User user) {
        lastMessage = "";

        if (user == null) {
            lastMessage = "Du lieu dang ky khong hop le.";
            return false;
        }

        user.setFullName(safeTrim(user.getFullName()));
        user.setEmail(safeTrim(user.getEmail()).toLowerCase());
        user.setPhone(safeTrim(user.getPhone()));
        user.setPassword(safeTrim(user.getPassword()));
        user.setAddress(safeTrim(user.getAddress()));

        if (isBlank(user.getFullName()) || isBlank(user.getEmail())
                || isBlank(user.getPhone()) || isBlank(user.getPassword())
                || isBlank(user.getAddress())) {
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

        if (!ValidationUtil.isStrongPassword(user.getPassword())) {
            lastMessage = "Mat khau toi thieu 6 ky tu, gom chu va so.";
            return false;
        }

        if (userDAO.existsByEmail(user.getEmail())) {
            lastMessage = "Email da ton tai.";
            return false;
        }

        if (userDAO.existsByPhone(user.getPhone())) {
            lastMessage = "So dien thoai da ton tai.";
            return false;
        }

        user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
        boolean result = userDAO.register(user);
        lastMessage = result ? "Dang ky thanh cong." : "Dang ky that bai.";
        return result;
    }

    public User login(String email, String password) {
        email = safeTrim(email).toLowerCase();
        password = safeTrim(password);

        if (isBlank(email) || isBlank(password)) {
            lastMessage = "Email va mat khau khong duoc de trong.";
            return null;
        }

        User user = userDAO.login(email);
        if (user == null) {
            lastMessage = "Tai khoan khong ton tai hoac da bi khoa.";
            return null;
        }

        if (PasswordUtil.checkPassword(password, user.getPassword())) {
            lastMessage = "Dang nhap thanh cong.";
            return user;
        }

        lastMessage = "Sai mat khau.";
        return null;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}