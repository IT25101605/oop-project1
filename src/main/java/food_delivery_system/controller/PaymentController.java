package food_delivery_system.controller;

import food_delivery_system.model.Payment;
import food_delivery_system.service.PaymentService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService service = new PaymentService();

    // SHOW PAGE
    @GetMapping("/pay")
    public String paymentPage() {
        return "payment";
    }

    // CREATE
    @PostMapping("/pay")
    public String makePayment(@RequestParam String orderId,
                              @RequestParam double amount) {

        Payment payment = new Payment(
                UUID.randomUUID().toString(),
                orderId,
                amount,
                "Completed"
        );

        service.makePayment(payment);

        return "redirect:/payment/all";
    }

    // READ
    @GetMapping("/all")
    public String viewPayments(Model model) {

        model.addAttribute("payments", service.getAllPayments());
        return "view-payments";
    }
}