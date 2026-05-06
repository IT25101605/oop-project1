package food_delivery_system.controller;

import food_delivery_system.model.Customer;
import food_delivery_system.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ================= HOME =================
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // ================= LOGIN PAGE =================
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // ================= REGISTER PAGE =================
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // =====================================================
    // FIX: SUPPORT BOTH /register AND /customer/register
    // =====================================================

    @PostMapping({"/register", "/customer/register"})
    public String registerCustomer(@RequestParam String name,
                                   @RequestParam String email,
                                   @RequestParam String password,
                                   @RequestParam String address,
                                   @RequestParam String phone,
                                   Model model) {

        Customer customer = new Customer(
                "C" + System.currentTimeMillis(),
                name,
                email,
                password,
                address,
                phone
        );

        boolean success = authService.registerCustomer(customer);

        if (success) {
            return "redirect:/login";
        }

        model.addAttribute("error", "Email already exists!");
        return "register";
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        Customer user = authService.loginUser(email, password, "CUSTOMER");

        if (user != null) {
            session.setAttribute("email", user.getEmail());
            session.setAttribute("role", "CUSTOMER");
            return "redirect:/dashboard";
        }

        model.addAttribute("error", "Invalid login details");
        return "login";
    }

    // ================= DASHBOARD =================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        String email = (String) session.getAttribute("email");

        if (email == null) return "redirect:/login";

        model.addAttribute("userEmail", email);
        return "customer-dashboard";
    }

    // ================= PROFILE =================
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {

        String email = (String) session.getAttribute("email");

        if (email == null) return "redirect:/login";

        Customer customer = authService.getCustomerByEmail(email);

        model.addAttribute("customer", customer);
        return "customer-profile";
    }

    // ================= UPDATE =================
    @PostMapping("/update")
    public String updateCustomer(@RequestParam String name,
                                 @RequestParam String password,
                                 @RequestParam String address,
                                 @RequestParam String phone,
                                 HttpSession session) {

        String email = (String) session.getAttribute("email");

        if (email != null) {

            Customer customer = authService.getCustomerByEmail(email);

            if (customer != null) {
                customer.setName(name);
                customer.setPassword(password);
                customer.setAddress(address);
                customer.setPhone(phone);

                authService.updateCustomer(customer);
            }
        }

        return "redirect:/profile";
    }

    // ================= DELETE =================
    @PostMapping("/delete")
    public String deleteCustomer(HttpSession session) {

        String email = (String) session.getAttribute("email");

        if (email != null) {
            authService.deleteUserByEmail(email, "CUSTOMER");
        }

        session.invalidate();
        return "redirect:/";
    }

    // ================= LOGOUT =================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}