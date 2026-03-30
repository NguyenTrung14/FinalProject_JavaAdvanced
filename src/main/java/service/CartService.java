package service;

import model.CartItem;
import model.Product;

import java.util.ArrayList;
import java.util.List;

public class CartService {
    private final List<CartItem> cart = new ArrayList<>();

    public List<CartItem> getCart() {
        return cart;
    }

    public boolean addToCart(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            return false;
        }

        for (CartItem item : cart) {
            if (item.getProduct().getProductId() == product.getProductId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return true;
            }
        }

        cart.add(new CartItem(product, quantity));
        return true;
    }

    public boolean removeFromCart(int productId) {
        return cart.removeIf(item -> item.getProduct().getProductId() == productId);
    }

    public boolean updateQuantity(int productId, int quantity) {
        if (quantity <= 0) {
            return false;
        }

        for (CartItem item : cart) {
            if (item.getProduct().getProductId() == productId) {
                item.setQuantity(quantity);
                return true;
            }
        }

        return false;
    }

    public boolean isEmpty() {
        return cart.isEmpty();
    }

    public void clearCart() {
        cart.clear();
    }

    public double getTotalAmount() {
        double total = 0;
        for (CartItem item : cart) {
            total += item.getSubtotal();
        }
        return total;
    }
}