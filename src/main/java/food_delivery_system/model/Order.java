package food_delivery_system.model;

//Encapsulation
public class Order {

    private String orderId;
    private String customerEmail;
    private String foodId;
    private int quantity;
    private String status;

    public Order() {}

    public Order(String orderId, String customerEmail, String foodId, int quantity, String status) {
        this.orderId = orderId;
        this.customerEmail = customerEmail;
        this.foodId = foodId;
        this.quantity = quantity;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getFoodId() { return foodId; }
    public void setFoodId(String foodId) { this.foodId = foodId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}