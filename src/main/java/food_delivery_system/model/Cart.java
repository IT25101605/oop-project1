package food_delivery_system.model;

// OOP: Encapsulation
public class Cart {

    private String cartId;
    private String customerEmail;
    private String foodId;
    private int quantity;

    public Cart() {}

    public Cart(String cartId, String customerEmail, String foodId, int quantity) {
        this.cartId = cartId;
        this.customerEmail = customerEmail;
        this.foodId = foodId;
        this.quantity = quantity;
    }

    public String getCartId() { return cartId; }
    public void setCartId(String cartId) { this.cartId = cartId; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getFoodId() { return foodId; }
    public void setFoodId(String foodId) { this.foodId = foodId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}