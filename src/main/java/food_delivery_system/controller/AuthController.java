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

    @GetMapping("/customer/profile")
    public String showCustomerProfile(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        String email = (String) session.getAttribute("userEmail");

        if (role == null || !role.equals("customer") || email == null) {
            return "redirect:/login";
        }

        Customer customer = authService.getCustomerByEmail(email);
        model.addAttribute("customer", customer);
        return "customer-profile";
    }

    @PostMapping("/customer/profile/update")
    public String updateCustomerProfile(@RequestParam String name,
                                        @RequestParam String password,
                                        @RequestParam String address,
                                        @RequestParam String phone,
                                        HttpSession session,
                                        Model model) {

        String role = (String) session.getAttribute("role");
        String email = (String) session.getAttribute("userEmail");

        if (role == null || !role.equals("customer") || email == null) {
            return "redirect:/login";
        }

        Customer oldCustomer = authService.getCustomerByEmail(email);

        if (oldCustomer == null) {
            return "redirect:/login";
        }

        Customer updatedCustomer = new Customer(
                oldCustomer.getId(),
                name,
                oldCustomer.getEmail(),
                password,
                oldCustomer.getRole(),
                address,
                phone
        );

        boolean success = authService.updateCustomer(updatedCustomer);

        if (success) {
            session.setAttribute("userName", updatedCustomer.getName());
            model.addAttribute("success", "Profile updated successfully");
            model.addAttribute("customer", updatedCustomer);
        } else {
            model.addAttribute("error", "Profile update failed");
            model.addAttribute("customer", oldCustomer);
        }

        return "customer-profile";
    }

    @PostMapping("/customer/profile/delete")
    public String deleteCustomerProfile(HttpSession session) {
        String role = (String) session.getAttribute("role");
        String email = (String) session.getAttribute("userEmail");

        if (role == null || !role.equals("customer") || email == null) {
            return "redirect:/login";
        }

        authService.deleteUserByEmail(email, "customer");
        session.invalidate();
        return "redirect:/?deleted=1";
    }

    @GetMapping("/owner/profile")
    public String showOwnerProfile(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        String email = (String) session.getAttribute("userEmail");

        if (role == null || !role.equals("owner") || email == null) {
            return "redirect:/login";
        }

        RestaurantOwner owner = authService.getOwnerByEmail(email);
        model.addAttribute("owner", owner);
        return "owner-profile";
    }

    @PostMapping("/owner/profile/update")
    public String updateOwnerProfile(@RequestParam String name,
                                     @RequestParam String password,
                                     @RequestParam String restaurantName,
                                     @RequestParam String phone,
                                     HttpSession session,
                                     Model model) {

        String role = (String) session.getAttribute("role");
        String email = (String) session.getAttribute("userEmail");

        if (role == null || !role.equals("owner") || email == null) {
            return "redirect:/login";
        }

        RestaurantOwner oldOwner = authService.getOwnerByEmail(email);

        if (oldOwner == null) {
            return "redirect:/login";
        }

        RestaurantOwner updatedOwner = new RestaurantOwner(
                oldOwner.getId(),
                name,
                oldOwner.getEmail(),
                password,
                oldOwner.getRole(),
                restaurantName,
                phone
        );

        boolean success = authService.updateOwner(updatedOwner);

        if (success) {
            session.setAttribute("userName", updatedOwner.getName());
            model.addAttribute("success", "Profile updated successfully");
            model.addAttribute("owner", updatedOwner);
        } else {
            model.addAttribute("error", "Profile update failed");
            model.addAttribute("owner", oldOwner);
        }

        return "owner-profile";
    }

    @PostMapping("/owner/profile/delete")
    public String deleteOwnerProfile(HttpSession session) {
        String role = (String) session.getAttribute("role");
        String email = (String) session.getAttribute("userEmail");

        if (role == null || !role.equals("owner") || email == null) {
            return "redirect:/login";
        }

        authService.deleteUserByEmail(email, "owner");
        session.invalidate();
        return "redirect:/?deleted=1";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}