package food_delivery_system.service;

import food_delivery_system.model.Cart;
import food_delivery_system.repository.CartRepository;

import java.util.*;

// OOP: Abstraction
public class CartService {

    private final CartRepository repo = new CartRepository();

    // CREATE (Add to cart)
    public void addToCart(Cart cart) {

        String line = cart.getCartId() + "," +
                cart.getCustomerEmail() + "," +
                cart.getFoodId() + "," +
                cart.getQuantity();

        repo.save(line);
    }

    // READ
    public List<Cart> getCartItems() {

        List<Cart> list = new ArrayList<>();

        for (String line : repo.findAll()) {
            String[] data = line.split(",");

            if (data.length == 4) {
                list.add(new Cart(
                        data[0],
                        data[1],
                        data[2],
                        Integer.parseInt(data[3])
                ));
            }
        }
        return list;
    }

    // DELETE (remove item)
    public void removeItem(String cartId) {

        List<String> updated = new ArrayList<>();

        for (String line : repo.findAll()) {
            if (!line.startsWith(cartId + ",")) {
                updated.add(line);
            }
        }

        repo.overwrite(updated);
    }
}