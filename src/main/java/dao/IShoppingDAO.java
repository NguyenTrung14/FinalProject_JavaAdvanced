package dao;

import model.CartItem;
import model.Order;

import java.util.List;

public interface IShoppingDAO {
    boolean placeOrder(int userId, List<CartItem> cartItems);

    List<Order> findOrdersByUserId(int userId);
}