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

    @GetMapping("/")
    public String home() {
        return "index";
    }

    //LOGIN
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    //REGISTER
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // REGISTER ROLE
    @PostMapping({"/register", "/customer/register"})
    public String registerCustomer(@RequestParam String name,
                                   @RequestParam String email,
                                   @RequestParam String password,
                                   @RequestParam String address,
                                   @RequestParam String phone,
                                   @RequestParam(required = false) String role,
                                   Model model) {

        if (role == null) {
            role = "CUSTOMER";
        }

        Customer customer = new Customer(
                "C" + System.currentTimeMillis(),
                name,
                email,
                password,
                address,
                phone
        );

        customer.setRole(role);

        boolean success = authService.registerCustomer(customer);

        if (success) {
            return "redirect:/login";
        }

        model.addAttribute("error", "Email already exists!");
        return "register";
    }

    // LOGIN ROLE
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        @RequestParam String role,
                        HttpSession session,
                        Model model) {

        Customer user = authService.loginUser(email, password, role);

        if (user != null) {

            session.setAttribute("email", user.getEmail());
            session.setAttribute("role", role);

            if ("CUSTOMER".equals(role)) {
                return "redirect:/dashboard";
            } else if ("OWNER".equals(role)) {
                return "redirect:/owner/dashboard";
            }
        }

        model.addAttribute("error", "Invalid login details");
        return "login";
    }

    //CUSTOMER DASHBOARD
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        String email = (String) session.getAttribute("email");
        String role = (String) session.getAttribute("role");

        if (email == null) return "redirect:/login";

        if (!"CUSTOMER".equals(role)) {
            return "redirect:/owner/dashboard";
        }

        model.addAttribute("userEmail", email);
        return "customer-dashboard";
    }

    //OWNER DASHBOARD
    @GetMapping("/owner/dashboard")
    public String ownerDashboard(HttpSession session, Model model) {

        String email = (String) session.getAttribute("email");
        String role = (String) session.getAttribute("role");

        if (email == null) return "redirect:/login";

        if (!"OWNER".equals(role)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("ownerEmail", email);
        return "owner-dashboard";
    }

    // OWNER PROFILE
    @GetMapping("/owner/profile")
    public String ownerProfile(HttpSession session, Model model) {

        String email = (String) session.getAttribute("email");
        String role = (String) session.getAttribute("role");

        if (email == null) return "redirect:/login";

        if (!"OWNER".equals(role)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("ownerEmail", email);
        return "owner-profile";
    }

    // OWNER UPDATE PROFILE
    @PostMapping("/owner/update")
    public String updateOwner(@RequestParam String name,
                              @RequestParam String password,
                              @RequestParam String address,
                              @RequestParam String phone,
                              HttpSession session) {

        String email = (String) session.getAttribute("email");
        String role = (String) session.getAttribute("role");

        if (email != null && "OWNER".equals(role)) {

            Customer owner = authService.getCustomerByEmail(email);

            if (owner != null) {
                owner.setName(name);
                owner.setPassword(password);
                owner.setAddress(address);
                owner.setPhone(phone);

                authService.updateCustomer(owner);
            }
        }

        return "redirect:/owner/profile";
    }

    // OWNER DELETE PROFILE
    @PostMapping("/owner/delete")
    public String deleteOwner(HttpSession session) {

        String email = (String) session.getAttribute("email");
        String role = (String) session.getAttribute("role");

        if (email != null && "OWNER".equals(role)) {
            authService.deleteUserByEmail(email, "OWNER");
        }

        session.invalidate();
        return "redirect:/";
    }

    //PROFILE
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {

        String email = (String) session.getAttribute("email");

        if (email == null) return "redirect:/login";

        Customer customer = authService.getCustomerByEmail(email);

        model.addAttribute("customer", customer);
        return "customer-profile";
    }

    //UPDATE
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

    //DELETE
    @PostMapping("/delete")
    public String deleteCustomer(HttpSession session) {

        String email = (String) session.getAttribute("email");

        if (email != null) {
            authService.deleteUserByEmail(email, "CUSTOMER");
        }

        session.invalidate();
        return "redirect:/";
    }

    //LOGOUT
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/restaurants")
    public String restaurants(HttpSession session) {

        String role = (String) session.getAttribute("role");

        if (role == null) {
            return "redirect:/login";
        }

        return "restaurants";
    }

    @GetMapping("/owner")
    public String ownerRootRedirect() {
        return "redirect:/owner/dashboard";
    }

    @GetMapping("/customer")
    public String customerRootRedirect() {
        return "redirect:/dashboard";
    }
}