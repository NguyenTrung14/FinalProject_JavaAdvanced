package dao;

import model.User;
import java.util.List;

public interface IUserDAO {
    boolean register(User user);

    User login(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByEmailExceptUserId(String email, int userId);

    boolean existsByPhoneExceptUserId(String phone, int userId);

    List<User> findAll();

    User findById(int id);

    boolean softDelete(int id);

    boolean updateProfile(User user);
}