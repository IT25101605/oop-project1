package food_delivery_system.service;

import food_delivery_system.model.Order;
import food_delivery_system.repository.OrderRepository;

import java.util.*;

// SOLID- Business logic layer
public class OrderService {

    private final OrderRepository repo = new OrderRepository();

    // CREATE (place order)
    public void placeOrder(Order order) {

        String line = order.getOrderId() + "," +
                order.getCustomerEmail() + "," +
                order.getFoodId() + "," +
                order.getQuantity() + "," +
                order.getStatus();

        repo.save(line);
    }

    // READ
    public List<Order> getAllOrders() {

        List<Order> list = new ArrayList<>();

        for (String line : repo.findAll()) {
            String[] data = line.split(",");

            if (data.length == 5) {
                list.add(new Order(
                        data[0],
                        data[1],
                        data[2],
                        Integer.parseInt(data[3]),
                        data[4]
                ));
            }
        }
        return list;
    }

    // UPDATE (status)
    public void updateStatus(String orderId, String status) {

        List<String> updated = new ArrayList<>();

        for (String line : repo.findAll()) {
            String[] data = line.split(",");

            if (data[0].equals(orderId)) {
                data[4] = status;
                updated.add(String.join(",", data));
            } else {
                updated.add(line);
            }
        }

        repo.overwrite(updated);
    }

    // DELETE
    public void deleteOrder(String orderId) {

        List<String> updated = new ArrayList<>();

        for (String line : repo.findAll()) {
            if (!line.startsWith(orderId + ",")) {
                updated.add(line);
            }
        }

        repo.overwrite(updated);
    }
}