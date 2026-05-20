package food_delivery_system.service;

// Importing models
import food_delivery_system.model.Cart;
import food_delivery_system.model.Food;

// Importing repositories
import food_delivery_system.repository.CartRepository;
import food_delivery_system.repository.FoodRepository;

// Spring Service annotation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CartService {

    // Injecting Cart and Food repositories
    @Autowired private CartRepository cartRepo;
    @Autowired private FoodRepository foodRepo;


    //get all cart iterm from custommer


    public List<Cart> getCart(String customerId) {

        // Fetch cart items from repository by customer ID
        return cartRepo.findByCustomer(customerId);
    }





    public void addToCart(String customerId, String foodId, int qty) {

        // Find food item from database
        Food f = foodRepo.findById(foodId);

        // If food does not exist, stop process
        if (f == null) return;

        // Ensure minimum quantity is 1
        if (qty < 1) qty = 1;

        // Check if same food already exists in cart
        for (Cart c : cartRepo.findByCustomer(customerId)) {

            if (c.getFoodId().equals(foodId)) {

                // Increase quantity instead of adding duplicate row
                c.setQuantity(c.getQuantity() + qty);
                cartRepo.update(c);
                return;
            }
        }

        // If not found in cart, create new cart item
        Cart c = new Cart(
                null,
                customerId,
                foodId,
                f.getName(),
                f.getRestaurantId(),
                f.getPrice(),
                qty
        );

        // Save new cart item
        cartRepo.save(c);
    }


    // update quantity of cart iterm


    public void updateQuantity(String cartId, int qty) {

        // Find cart item
        Cart c = cartRepo.findById(cartId);

        if (c == null) return;

        // If quantity is invalid, remove item
        if (qty < 1) {
            cartRepo.delete(cartId);
            return;
        }

        // Update quantity
        c.setQuantity(qty);
        cartRepo.update(c);
    }


    // remove iterm cart


    public void remove(String cartId) {

        cartRepo.delete(cartId);
    }


    // clear entire cart


    public void clear(String customerId) {

        cartRepo.clearForCustomer(customerId);
    }


    // Cart subtotal calculate


    public double subtotal(String customerId) {

        // Sum of (price × quantity) for all items
        return getCart(customerId)
                .stream()
                .mapToDouble(Cart::getSubtotal)
                .sum();
    }
}