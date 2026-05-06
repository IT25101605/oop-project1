package food_delivery_system.controller;

import food_delivery_system.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final AuthService authService = new AuthService();

    // HOME
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // LOGIN PAGE
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // REGISTER PAGE
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // REGISTER CUSTOMER ✅ (ONLY HERE)
    @PostMapping("/customer/register")
    public String registerCustomer(@RequestParam String name,
                                   @RequestParam String email,
                                   @RequestParam String password,
                                   @RequestParam String address,
                                   @RequestParam String phone,
                                   Model model) {

        boolean success = authService.registerCustomer(
                name, email, password, address, phone
        );

        if (success) {
            return "redirect:/login";
        } else {
            model.addAttribute("error", "Email already exists!");
            return "register";
        }
    }

    // LOGIN
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        String role = authService.login(email, password);

        if (role != null) {

            session.setAttribute("email", email);
            session.setAttribute("role", role);

            if (role.equals("CUSTOMER")) {
                return "redirect:/customer/dashboard";
            } else if (role.equals("ADMIN")) {
                return "redirect:/admin/dashboard";
            } else if (role.equals("OWNER")) {
                return "redirect:/owner/dashboard";
            }
        }

        model.addAttribute("error", "Invalid login details");
        return "login";
    }

    // CUSTOMER DASHBOARD
    @GetMapping("/customer/dashboard")
    public String customerDashboard(HttpSession session, Model model) {

        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "redirect:/login";
        }

        model.addAttribute("userEmail", email);
        return "customer-dashboard";
    }

    // PROFILE
    @GetMapping("/customer/profile")
    public String profile(HttpSession session, Model model) {

        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "redirect:/login";
        }

        String[] customer = authService.getCustomerByEmail(email);

        model.addAttribute("customer", customer);
        return "customer-profile";
    }

    // UPDATE
    @PostMapping("/customer/update")
    public String updateCustomer(@RequestParam String name,
                                 @RequestParam String password,
                                 @RequestParam String address,
                                 @RequestParam String phone,
                                 HttpSession session) {

        String email = (String) session.getAttribute("email");

        if (email != null) {
            authService.updateCustomer(email, name, password, address, phone);
        }

        return "redirect:/customer/profile";
    }

    // DELETE
    @PostMapping("/customer/delete")
    public String deleteCustomer(HttpSession session) {

        String email = (String) session.getAttribute("email");

        if (email != null) {
            authService.deleteCustomer(email);
        }

        session.invalidate();
        return "redirect:/";
    }

    // LOGOUT
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}