package food_delivery_system.controller;

import food_delivery_system.service.AdminService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// MVC Controller
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService service = new AdminService();

    // Login page
    @GetMapping("/login")
    public String loginPage() {
        return "admin-login";
    }

    // Login process
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password) {

        if (service.login(username, password)) {
            return "redirect:/admin/dashboard";
        }

        return "admin-login";
    }

    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin-dashboard";
    }

    // View users
    @GetMapping("/users")
    public String users(Model model) {

        model.addAttribute("users", service.getAllUsers());
        return "manage-users";
    }

    // View restaurants
    @GetMapping("/restaurants")
    public String restaurants(Model model) {

        model.addAttribute("restaurants", service.getAllRestaurants());
        return "manage-restaurants";
    }

    // View orders
    @GetMapping("/orders")
    public String orders(Model model) {

        model.addAttribute("orders", service.getAllOrders());
        return "manage-orders";
    }

    // Delete user
    @GetMapping("/delete-user")
    public String deleteUser(@RequestParam String email) {

        service.deleteUser(email);
        return "redirect:/admin/users";
    }

    // Delete Restaurant    @GetMapping("/delete-restaurant")
    public String deleteRestaurant(@RequestParam String id) {

        service.deleteRestaurant(id);
        return "redirect:/admin/restaurants";
    }

    // Delete order
    @GetMapping("/delete-order")
    public String deleteOrder(@RequestParam String id) {

        service.deleteOrder(id);
        return "redirect:/admin/orders";
    }
}