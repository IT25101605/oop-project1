package food_delivery_system.controller;

import food_delivery_system.model.Customer;
import food_delivery_system.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/customer/register")
    public String showRegisterPage() {
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

        boolean success = customerService.registerCustomer(customer);

        if (success) {
            return "redirect:/customer/login?registered=1";
        } else {
            model.addAttribute("error", "Email already exists");
            return "customer-register";
        }
    }

    @GetMapping("/customer/login")
    public String showLoginPage() {
        return "customer-login";
    }

    @PostMapping("/customer/login")
    public String loginCustomer(@RequestParam String email,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {

        Customer customer = customerService.loginCustomer(email, password);

        if (customer != null) {
            session.setAttribute("customerEmail", customer.getEmail());
            session.setAttribute("customerName", customer.getName());
            return "redirect:/customer/dashboard";
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "customer-login";
        }
    }

    @GetMapping("/customer/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        String customerName = (String) session.getAttribute("customerName");

        if (customerName == null) {
            return "redirect:/customer/login";
        }

        model.addAttribute("customerName", customerName);
        return "customer-dashboard";
    }

    @GetMapping("/customer/profile")
    public String showProfile(HttpSession session, Model model) {
        String email = (String) session.getAttribute("customerEmail");

        if (email == null) {
            return "redirect:/customer/login";
        }

        Customer customer = customerService.getCustomerByEmail(email);
        model.addAttribute("customer", customer);
        return "customer-profile";
    }

    @PostMapping("/customer/profile")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String password,
                                @RequestParam String address,
                                @RequestParam String phone,
                                HttpSession session,
                                Model model) {

        String email = (String) session.getAttribute("customerEmail");

        if (email == null) {
            return "redirect:/customer/login";
        }

        Customer oldCustomer = customerService.getCustomerByEmail(email);

        if (oldCustomer == null) {
            return "redirect:/customer/login";
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

        boolean success = customerService.updateCustomer(updatedCustomer);

        if (success) {
            session.setAttribute("customerName", updatedCustomer.getName());
            model.addAttribute("customer", updatedCustomer);
            model.addAttribute("success", "Profile updated successfully");
        } else {
            model.addAttribute("customer", oldCustomer);
            model.addAttribute("error", "Profile update failed");
        }

        return "customer-profile";
    }

    @GetMapping("/customer/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/customer/login";
    }
}