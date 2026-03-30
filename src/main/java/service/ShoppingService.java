package service;

import model.CartItem;

import java.util.List;

import dao.impl.ShoppingDAO;

public class ShoppingService {
    private final ShoppingDAO shoppingDAO = new ShoppingDAO();

    public boolean checkout(int userId, List<CartItem> cartItems, String couponCode) {
        if (userId <= 0 || cartItems == null || cartItems.isEmpty()) {
            return false;
        }
        return shoppingDAO.checkout(userId, cartItems, couponCode);
    }
}