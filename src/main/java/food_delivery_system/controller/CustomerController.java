/*package food_delivery_system.controller;

import food_delivery_system.model.Customer;
import food_delivery_system.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final AuthService authService;

    public CustomerController(AuthService authService) {
        this.authService = authService;
    }

    // DASHBOARD
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        String email = (String) session.getAttribute("email");

        if (email == null) return "redirect:/login";

        model.addAttribute("userEmail", email);
        return "customer-dashboard";
    }

    // PROFILE
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {

        String email = (String) session.getAttribute("email");

        if (email == null) return "redirect:/login";

        Customer customer = authService.getCustomerByEmail(email);

        model.addAttribute("customer", customer);
        return "customer-profile";
    }

    // UPDATE
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

        return "redirect:/customer/profile";
    }

    // DELETE
    @PostMapping("/delete")
    public String deleteCustomer(HttpSession session) {

        String email = (String) session.getAttribute("email");

        if (email != null) {
            authService.deleteUserByEmail(email, "customer");
        }

        session.invalidate();
        return "redirect:/";
    }
}*/