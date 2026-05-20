package food_delivery_system.model;

/**
 Platform settings — global website commission configuration.
 This is a Model class in MVC architecture.
 represents system-wide configuration data.
 restaurantCommissionPct = % added on top of restaurant food price (paid by customer).
 riderCommissionPct      = % deducted from delivery fee (paid by rider).
 */
public class Settings {

    // Encapsulation protect global configuration data
    private double restaurantCommissionPct;
    private double riderCommissionPct;

    // Used when system loads default configuration values
    public Settings() {
        // Default commission values (business logic defaults)
        this.restaurantCommissionPct = 5.0;
        this.riderCommissionPct = 10.0;
    }

    // Used when loading settings from file or admin panel
    public Settings(double restaurantCommissionPct, double riderCommissionPct) {
        this.restaurantCommissionPct = restaurantCommissionPct;
        this.riderCommissionPct = riderCommissionPct;
    }

    public double getRestaurantCommissionPct() {
        return restaurantCommissionPct;
    }

    public void setRestaurantCommissionPct(double v) {
        this.restaurantCommissionPct = v;
    }

    public double getRiderCommissionPct() {
        return riderCommissionPct;
    }

    public void setRiderCommissionPct(double v) {
        this.riderCommissionPct = v;
    }

}