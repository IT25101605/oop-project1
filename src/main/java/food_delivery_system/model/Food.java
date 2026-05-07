package food_delivery_system.model;

//Encapsulation (private fields and getters/setters)
public class Food {

    private String id;
    private String name;
    private double price;
    private String restaurantId;

    public Food() {}

    public Food(String id, String name, double price, String restaurantId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.restaurantId = restaurantId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
}