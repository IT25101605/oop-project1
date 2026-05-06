package food_delivery_system.controller;

import food_delivery_system.model.Cart;
import food_delivery_system.service.CartService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService service = new CartService();

    // ADD
    @PostMapping("/add")
    public String addToCart(@RequestParam String email,
                            @RequestParam String foodId,
                            @RequestParam int quantity) {

        Cart cart = new Cart(
                UUID.randomUUID().toString(),
                email,
                foodId,
                quantity
        );

        service.addToCart(cart);

        return "redirect:/cart/view";
    }

    // VIEW
    @GetMapping("/view")
    public String viewCart(Model model) {

        model.addAttribute("cartItems", service.getCartItems());
        return "cart";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable String id) {

        service.removeItem(id);
        return "redirect:/cart/view";
    }
}