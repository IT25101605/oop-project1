package food_delivery_system.model;

/**
 * Platform settings — global website commission configuration.
 * Single-row entity persisted in settings.txt.
 *
 * restaurantCommissionPct = % added on top of restaurant food price (paid by customer).
 * riderCommissionPct      = % deducted from delivery fee (paid by rider).
 */
public class Settings {
    private double restaurantCommissionPct;
    private double riderCommissionPct;

    public Settings() {
        this.restaurantCommissionPct = 5.0;
        this.riderCommissionPct = 10.0;
    }

    public Settings(double restaurantCommissionPct, double riderCommissionPct) {
        this.restaurantCommissionPct = restaurantCommissionPct;
        this.riderCommissionPct = riderCommissionPct;
    }

    public double getRestaurantCommissionPct() { return restaurantCommissionPct; }
    public void setRestaurantCommissionPct(double v) { this.restaurantCommissionPct = v; }
    public double getRiderCommissionPct() { return riderCommissionPct; }
    public void setRiderCommissionPct(double v) { this.riderCommissionPct = v; }
}
