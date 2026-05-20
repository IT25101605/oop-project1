package food_delivery_system.model;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Restaurant model class
 * MVC Architecture: Model layer stores restaurant data
 *
 * OOP Concepts Used:
 * - Encapsulation using private variables and getter/setter methods
 * - Abstraction by hiding internal implementation details
 * - Aggregation relationship:
 *   Restaurant belongs to an Owner and contains Foods/Orders
 */
public class Restaurant {

    // Unique restaurant ID
    private String id;

    // Owner ID linked with User model
    // Aggregation relationship between User and Restaurant
    private String ownerId;

    // Restaurant name
    private String name;

    // Restaurant city/location
    private String city;

    // Full address
    private String address;

    // Restaurant cuisine type
    // Example: Italian, Chinese, Sri Lankan
    private String cuisine;

    // Restaurant image file name
    // Stored inside uploads folder or static images folder
    private String image;

    // Restaurant description/details
    private String description;

    // GPS latitude coordinate
    private String latitude;

    // GPS longitude coordinate
    private String longitude;

    // Default constructor
    // Required for frameworks like Spring and Jackson
    public Restaurant() {}

    // Constructor overloading
    // Polymorphism concept:
    // Same constructor name with different parameters
    public Restaurant(String id,
                      String ownerId,
                      String name,
                      String city,
                      String address,
                      String cuisine,
                      String image,
                      String description) {

        // Calling another constructor using this()
        // Constructor chaining
        this(id, ownerId, name, city, address,
                cuisine, image, description, "", "");
    }

    // Parameterized constructor
    // Used for object creation with full data
    public Restaurant(String id,
                      String ownerId,
                      String name,
                      String city,
                      String address,
                      String cuisine,
                      String image,
                      String description,
                      String latitude,
                      String longitude) {

        // this keyword refers to current object variables
        this.id=id;
        this.ownerId=ownerId;
        this.name=name;
        this.city=city;
        this.address=address;
        this.cuisine=cuisine;
        this.image=image;
        this.description=description;

        // Null checking prevents NullPointerException
        this.latitude = latitude == null ? "" : latitude;
        this.longitude = longitude == null ? "" : longitude;
    }

    // Getter and Setter methods
    // Encapsulation:
    // Private variables accessed through public methods

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String o) {
        this.ownerId = o;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        this.name = n;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String c) {
        this.city = c;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String a) {
        this.address = a;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String c) {
        this.cuisine = c;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String i) {
        this.image = i;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String d) {
        this.description = d;
    }

    public String getLatitude() {
        return latitude;
    }

    // Validation inside setter
    public void setLatitude(String latitude) {
        this.latitude = latitude == null ? "" : latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    // Validation inside setter
    public void setLongitude(String longitude) {
        this.longitude = longitude == null ? "" : longitude;
    }

    // Checks whether restaurant contains valid GPS coordinates
    public boolean hasCoordinates() {

        // isBlank() checks for empty strings
        return latitude != null &&
                !latitude.isBlank() &&
                longitude != null &&
                !longitude.isBlank();
    }

    // Generates location query for Google Maps
    public String getMapQuery() {

        // If coordinates exist, use them directly
        if (hasCoordinates())
            return latitude + "," + longitude;

        // Creating location query using address and city
        String q = (
                (address == null ? "" : address)
                        + ", "
                        + (city == null ? "" : city)
        ).trim();

        // Default fallback location
        return q.isBlank() || q.equals(",")
                ? "Sri Lanka"
                : q;
    }

    // Private helper method for URL encoding
    // Encapsulation: helper method hidden inside class
    private String encodedMapQuery() {

        // URLEncoder converts special characters into URL-safe format
        return URLEncoder.encode(
                getMapQuery(),
                StandardCharsets.UTF_8
        );
    }

    // Generates embeddable Google Maps URL
    public String getMapEmbedUrl() {

        return "https://maps.google.com/maps?q="
                + encodedMapQuery()
                + "&output=embed";
    }

    // Generates clickable Google Maps search URL
    public String getGoogleMapsUrl() {

        return "https://www.google.com/maps/search/?api=1&query="
                + encodedMapQuery();
    }
}

