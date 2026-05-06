package food_delivery_system.controller;

import food_delivery_system.model.Order;
import food_delivery_system.service.OrderService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService service = new OrderService();

    // PLACE ORDER
    @PostMapping("/place")
    public String placeOrder(@RequestParam String email,
                             @RequestParam String foodId,
                             @RequestParam int quantity) {

        Order order = new Order(
                UUID.randomUUID().toString(),
                email,
                foodId,
                quantity,
                "Pending"
        );

        service.placeOrder(order);

        return "redirect:/order/view";
    }

    // VIEW
    @GetMapping("/view")
    public String viewOrders(Model model) {

        model.addAttribute("orders", service.getAllOrders());
        return "view-orders";
    }

    // UPDATE STATUS
    @PostMapping("/update")
    public String updateStatus(@RequestParam String orderId,
                               @RequestParam String status) {

        service.updateStatus(orderId, status);
        return "redirect:/order/view";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable String id) {

        service.deleteOrder(id);
        return "redirect:/order/view";
    }
}