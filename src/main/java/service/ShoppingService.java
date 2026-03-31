package service;

import dao.impl.ShoppingDAO;
import model.CartItem;

import java.util.List;

public class ShoppingService {
    private final ShoppingDAO shoppingDAO = new ShoppingDAO();

    public boolean addToCartAndReserve(int userId, int productId, int quantity) {
        if (userId <= 0 || productId <= 0 || quantity <= 0) {
            return false;
        }
        return shoppingDAO.addToCartAndReserve(userId, productId, quantity);
    }

    public List<CartItem> getReservedCartItems(int userId) {
        if (userId <= 0) {
            return List.of();
        }
        return shoppingDAO.getReservedCartItems(userId);
    }

    public boolean removeCartItemAndRestore(int userId, int productId) {
        if (userId <= 0 || productId <= 0) {
            return false;
        }
        return shoppingDAO.removeCartItemAndRestore(userId, productId);
    }

    public boolean checkoutReservedCart(int userId, String couponCode) {
        if (userId <= 0) {
            return false;
        }
        return shoppingDAO.checkoutReservedCart(userId, couponCode);
    }

    public boolean checkout(int userId, List<CartItem> cartItems, String couponCode) {
        if (userId <= 0) {
            return false;
        }
        return shoppingDAO.checkoutReservedCart(userId, couponCode);
    }
}