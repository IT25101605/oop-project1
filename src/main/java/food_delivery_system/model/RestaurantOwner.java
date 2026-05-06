package food_delivery_system.model;

public class RestaurantOwner extends User {
    private String restaurantName;
    private String phone;

    public RestaurantOwner() {
    }

    public RestaurantOwner(String id, String name, String email, String password, String role, String restaurantName, String phone) {
        super(id, name, email, password, role);
        this.restaurantName = restaurantName;
        this.phone = phone;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}