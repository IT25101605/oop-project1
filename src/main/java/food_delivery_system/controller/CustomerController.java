package food_delivery_system.controller;

import food_delivery_system.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

// MVC Pattern: Controller handles HTTP requests only

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService service = new CustomerService();

    // CREATE (REGISTER)
    @PostMapping("/register")
    public String register(@RequestParam String data) {
        service.register(data);
        return "redirect:/login";
    }

    // READ
    @GetMapping("/all")
    @ResponseBody
    public List<String> getAll() {
        return service.getAll();
    }
}