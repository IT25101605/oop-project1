package food_delivery_system.service;

import food_delivery_system.model.Settings;
import food_delivery_system.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Central pricing service. Wraps website commission logic so cart, checkout,
 * order placement, payment, and reports all use the same numbers.
 */
@Service
public class SettingsService {

    @Autowired private SettingsRepository repo;

    public Settings get() { return repo.load(); }

    public void update(double restaurantCommissionPct, double riderCommissionPct) {
        if (restaurantCommissionPct < 0) restaurantCommissionPct = 0;
        if (riderCommissionPct < 0) riderCommissionPct = 0;
        repo.save(new Settings(restaurantCommissionPct, riderCommissionPct));
    }

    /** Customer-facing food price = base price + restaurant commission. */
    public double customerPrice(double basePrice) {
        return round2(basePrice + commissionFromBase(basePrice));
    }

    /** Website commission added on top of base food price. */
    public double commissionFromBase(double basePrice) {
        return round2(basePrice * (get().getRestaurantCommissionPct() / 100.0));
    }

    /** Website deduction taken from rider's delivery fee. */
    public double riderWebsiteFee(double deliveryFee) {
        return round2(deliveryFee * (get().getRiderCommissionPct() / 100.0));
    }

    /** Net amount the rider receives after the website deduction. */
    public double riderEarning(double deliveryFee) {
        return round2(deliveryFee - riderWebsiteFee(deliveryFee));
    }

    public static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
