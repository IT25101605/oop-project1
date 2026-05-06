package food_delivery_system.service;

import food_delivery_system.model.Payment;
import food_delivery_system.repository.PaymentRepository;

import java.util.*;

// Business logic layer
public class PaymentService {

    private final PaymentRepository repo = new PaymentRepository();

    // CREATE (simulate payment)
    public void makePayment(Payment payment) {

        String line = payment.getPaymentId() + "," +
                payment.getOrderId() + "," +
                payment.getAmount() + "," +
                payment.getStatus();

        repo.save(line);
    }

    // READ
    public List<Payment> getAllPayments() {

        List<Payment> list = new ArrayList<>();

        for (String line : repo.findAll()) {
            String[] data = line.split(",");

            if (data.length == 4) {
                list.add(new Payment(
                        data[0],
                        data[1],
                        Double.parseDouble(data[2]),
                        data[3]
                ));
            }
        }
        return list;
    }
}