package service;

import model.CartItem;
import model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartService {
    private final Map<Integer, List<CartItem>> userCarts = new HashMap<>();

    private List<CartItem> getOrCreateCart(int userId) {
        return userCarts.computeIfAbsent(userId, k -> new ArrayList<>());
    }

    public List<CartItem> getCart(int userId) {
        return getOrCreateCart(userId);
    }

    public boolean addToCart(int userId, Product product, int quantity) {
        if (product == null || quantity <= 0) {
            return false;
        }

        List<CartItem> cart = getOrCreateCart(userId);

        for (CartItem item : cart) {
            if (item.getProduct().getProductId() == product.getProductId()) {
                int newQuantity = item.getQuantity() + quantity;

                if (newQuantity > product.getStock()) {
                    return false;
                }

                item.setQuantity(newQuantity);
                return true;
            }
        }

        if (quantity > product.getStock()) {
            return false;
        }

        cart.add(new CartItem(product, quantity));
        return true;
    }

    public boolean removeFromCart(int userId, int productId) {
        return getOrCreateCart(userId)
                .removeIf(item -> item.getProduct().getProductId() == productId);
    }

    public boolean isEmpty(int userId) {
        return getOrCreateCart(userId).isEmpty();
    }

    public void clearCart(int userId) {
        getOrCreateCart(userId).clear();
    }

    public double getTotalAmount(int userId) {
        double total = 0;
        for (CartItem item : getOrCreateCart(userId)) {
            total += item.getSubtotal();
        }
        return total;
    }
}