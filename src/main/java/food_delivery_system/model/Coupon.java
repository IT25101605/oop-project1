package food_delivery_system.model;

/**
 * Model class representing Restaurant Coupons / Promo Codes
 * MVC Architecture: Model layer stores application data
 *
 * OOP Concept:
 * - Encapsulation used through private variables and public getters/setters
 * - Abstraction used by hiding internal data implementation
 *
 * Restaurant promo code. type = PERCENT | AMOUNT.
 * Discount applied to subtotal (food cost incl. commission).
 */
public class Coupon {

    // Private variables for data hiding
    // Encapsulation: variables are accessed using getter/setter methods
    private String id;

    // Aggregation relationship:
    // Coupon belongs to a Restaurant
    private String restaurantId;

    // Coupon code entered by customer
    private String code;

    // Discount type: PERCENT or AMOUNT
    private String type;

    // Discount value
    // Example:
    // 10 = 10% OR Rs. 10 flat discount
    private double value;

    // Minimum subtotal required to use coupon
    private double minOrder;

    // Expiry date for coupon
    // yyyy-MM-dd format
    private String expiryDate;

    // Used to enable or disable coupon
    private boolean enabled;

    // Additional coupon details
    private String description;

    // Default constructor
    // Required for frameworks like Spring / Jackson
    public Coupon() {}

    // Parameterized constructor
    // Used for object creation with values
    public Coupon(String id,
                  String restaurantId,
                  String code,
                  String type,
                  double value,
                  double minOrder,
                  String expiryDate,
                  boolean enabled,
                  String description) {

        // this keyword refers to current object variables
        this.id = id;

        this.restaurantId = restaurantId;

        // Validation and formatting
        // trim() removes extra spaces
        // toUpperCase() standardizes coupon codes
        this.code = code == null
                ? ""
                : code.trim().toUpperCase();

        // Default discount type is PERCENT
        this.type = type == null
                ? "PERCENT"
                : type.toUpperCase();

        this.value = value;

        this.minOrder = minOrder;

        // Null checking prevents NullPointerException
        this.expiryDate = expiryDate == null
                ? ""
                : expiryDate;

        this.enabled = enabled;

        this.description = description == null
                ? ""
                : description;
    }

    // Getter and Setter methods
    // Encapsulation:
    // Accessing private variables safely through methods

    public String getId(){return id;}

    public void setId(String v){this.id=v;}

    public String getRestaurantId(){return restaurantId;}

    public void setRestaurantId(String v){this.restaurantId=v;}

    public String getCode(){return code;}

    // Converts coupon code to uppercase before saving
    public void setCode(String v){
        this.code = v == null
                ? ""
                : v.trim().toUpperCase();
    }

    public String getType(){return type;}

    // Standardizes discount type
    public void setType(String v){
        this.type = v == null
                ? "PERCENT"
                : v.toUpperCase();
    }

    public double getValue(){return value;}

    public void setValue(double v){this.value=v;}

    public double getMinOrder(){return minOrder;}

    public void setMinOrder(double v){this.minOrder=v;}

    public String getExpiryDate(){return expiryDate;}

    public void setExpiryDate(String v){
        this.expiryDate = v == null
                ? ""
                : v;
    }

    public boolean isEnabled(){return enabled;}

    public void setEnabled(boolean v){this.enabled=v;}

    public String getDescription(){return description;}

    public void setDescription(String v){
        this.description = v == null
                ? ""
                : v;
    }

    // Method checks whether coupon is expired
    public boolean isExpired() {

        // If expiry date is empty, coupon never expires
        if (expiryDate == null || expiryDate.isBlank())
            return false;

        try {

            // LocalDate class used for date handling
            // File does not use inheritance directly
            // Java built-in classes are reused here

            return java.time.LocalDate
                    .parse(expiryDate)

                    // Comparing expiry date with current date
                    .isBefore(java.time.LocalDate.now());

        } catch (Exception e) {

            // Exception handling prevents application crash
            // Returns false if invalid date format exists
            return false;
        }
    }

    /**
     * Compute discount amount applied to a given subtotal.
     * Business logic method
     */
    public double computeDiscount(double subtotal) {

        // Flat amount discount calculation
        if ("AMOUNT".equalsIgnoreCase(type))

            // Math.min prevents discount exceeding subtotal
            return Math.min(value, subtotal);

        // Percentage discount calculation
        double d = subtotal * (value / 100.0);

        // Math.max and Math.min used for safe value range
        return Math.max(0, Math.min(d, subtotal));
    }
}

