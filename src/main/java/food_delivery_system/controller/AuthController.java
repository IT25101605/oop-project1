package food_delivery_system.controller;

import food_delivery_system.model.Customer;
import food_delivery_system.model.RestaurantOwner;
import food_delivery_system.model.User;
import food_delivery_system.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String role,
                               @RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam(required = false) String address,
                               @RequestParam String phone,
                               @RequestParam(required = false) String restaurantName,
                               Model model) {

        boolean success = false;

        if ("customer".equalsIgnoreCase(role)) {
            Customer customer = new Customer(
                    UUID.randomUUID().toString(),
                    name,
                    email,
                    password,
                    "customer",
                    address,
                    phone
            );
            success = authService.registerCustomer(customer);

        } else if ("owner".equalsIgnoreCase(role)) {
            RestaurantOwner owner = new RestaurantOwner(
                    UUID.randomUUID().toString(),
                    name,
                    email,
                    password,
                    "owner",
                    restaurantName,
                    phone
            );
            success = authService.registerOwner(owner);
        }

        if (success) {
            return "redirect:/login?registered=1";
        }

        model.addAttribute("error", "Email already exists");
        return "register";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String role,
                            @RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {

        User user = authService.loginUser(email, password, role);

        if (user != null) {
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("role", user.getRole());

            if ("customer".equalsIgnoreCase(role)) {
                return "redirect:/customer/dashboard";
            } else if ("owner".equalsIgnoreCase(role)) {
                return "redirect:/owner/dashboard";
            }
        }

        model.addAttribute("error", "Invalid login details");
        return "login";
    }

    @GetMapping("/customer/dashboard")
    public String customerDashboard(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");

        if (role == null || !role.equals("customer")) {
            return "redirect:/login";
        }

        model.addAttribute("userName", session.getAttribute("userName"));
        return "customer-dashboard";
    }

    @GetMapping("/owner/dashboard")
    public String ownerDashboard(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");

        if (role == null || !role.equals("owner")) {
            return "redirect:/login";
        }

        model.addAttribute("userName", session.getAttribute("userName"));
        return "owner-dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}