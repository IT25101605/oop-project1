package food_delivery_system.controller;

import food_delivery_system.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService service = new CustomerService();

    // READ ALL CUSTOMERS
    @GetMapping("/all")
    @ResponseBody
    public List<String> getAll() {
        return service.getAll();
    }
}