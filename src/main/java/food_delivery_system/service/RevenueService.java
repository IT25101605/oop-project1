package food_delivery_system.service;

import food_delivery_system.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/** Aggregates website revenue from completed orders. */
@Service
public class RevenueService {

    @Autowired private OrderService orderService;

    public static class Stats {
        public double restaurantCommission;
        public double riderCommission;
        public double total;
        public int completedOrders;
        public Stats(double r, double rd, int n) {
            this.restaurantCommission = SettingsService.round2(r);
            this.riderCommission = SettingsService.round2(rd);
            this.total = SettingsService.round2(r + rd);
            this.completedOrders = n;
        }
    }

    private boolean isCompleted(Order o) {
        return "DELIVERED".equalsIgnoreCase(o.getStatus());
    }

    public Stats overall() {
        return computeFor(orderService.all().stream().filter(this::isCompleted).toList());
    }

    public Stats today() {
        String today = LocalDate.now().toString();
        return computeFor(orderService.all().stream()
                .filter(this::isCompleted)
                .filter(o -> o.getCreatedAt() != null && o.getCreatedAt().startsWith(today))
                .toList());
    }

    public Stats thisMonth() {
        String month = YearMonth.now().toString(); // yyyy-MM
        return computeFor(orderService.all().stream()
                .filter(this::isCompleted)
                .filter(o -> o.getCreatedAt() != null && o.getCreatedAt().startsWith(month))
                .toList());
    }

    private Stats computeFor(List<Order> orders) {
        double r = orders.stream().mapToDouble(Order::getWebsiteCommission).sum();
        double rd = orders.stream().mapToDouble(Order::getRiderWebsiteFee).sum();
        return new Stats(r, rd, orders.size());
    }
}
