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

    @GetMapping("/customer/register")
    public String showCustomerRegister() {
        return "customer-register";
    }

    @PostMapping("/customer/register")
    public String registerCustomer(@RequestParam String name,
                                   @RequestParam String email,
                                   @RequestParam String password,
                                   @RequestParam String address,
                                   @RequestParam String phone,
                                   Model model) {

        Customer customer = new Customer(
                UUID.randomUUID().toString(),
                name,
                email,
                password,
                "customer",
                address,
                phone
        );

        boolean success = authService.registerCustomer(customer);

        if (success) {
            return "redirect:/customer/login?registered=1";
        }

        model.addAttribute("error", "Email already exists");
        return "customer-register";
    }

    @GetMapping("/customer/login")
    public String showCustomerLogin() {
        return "customer-login";
    }

    @PostMapping("/customer/login")
    public String customerLogin(@RequestParam String email,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {

        User user = authService.loginUser(email, password, "customer");

        if (user != null) {
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("role", user.getRole());

            return "redirect:/customer/dashboard";
        }

        model.addAttribute("error", "Invalid customer email or password");
        return "customer-login";
    }

    @GetMapping("/customer/dashboard")
    public String customerDashboard(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");

        if (role == null || !role.equals("customer")) {
            return "redirect:/customer/login";
        }

        model.addAttribute("userName", session.getAttribute("userName"));
        return "customer-dashboard";
    }

    @GetMapping("/owner/register")
    public String showOwnerRegister() {
        return "owner-register";
    }

    @PostMapping("/owner/register")
    public String registerOwner(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String password,
                                @RequestParam String restaurantName,
                                @RequestParam String phone,
                                Model model) {

        RestaurantOwner owner = new RestaurantOwner(
                UUID.randomUUID().toString(),
                name,
                email,
                password,
                "owner",
                restaurantName,
                phone
        );

        boolean success = authService.registerOwner(owner);

        if (success) {
            return "redirect:/owner/login?registered=1";
        }

        model.addAttribute("error", "Email already exists");
        return "owner-register";
    }

    @GetMapping("/owner/login")
    public String showOwnerLogin() {
        return "owner-login";
    }

    @PostMapping("/owner/login")
    public String ownerLogin(@RequestParam String email,
                             @RequestParam String password,
                             HttpSession session,
                             Model model) {

        User user = authService.loginUser(email, password, "owner");

        if (user != null) {
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("role", user.getRole());

            return "redirect:/owner/dashboard";
        }

        model.addAttribute("error", "Invalid owner email or password");
        return "owner-login";
    }

    @GetMapping("/owner/dashboard")
    public String ownerDashboard(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");

        if (role == null || !role.equals("owner")) {
            return "redirect:/owner/login";
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