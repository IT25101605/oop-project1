package food_delivery_system.model;

// OOP: Encapsulation → private fields
public class Restaurant {

    private String id;
    private String name;
    private String location;
    private String ownerEmail;

    public Restaurant() {}

    public Restaurant(String id, String name, String location, String ownerEmail) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.ownerEmail = ownerEmail;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
}