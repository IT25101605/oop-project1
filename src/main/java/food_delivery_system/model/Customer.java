package food_delivery_system.model;

public class Customer extends User {
    private String address;
    private String phone;

    public Customer() {
    }

    public Customer(String id, String name, String email, String password, String role, String address, String phone) {
        super(id, name, email, password, role);
        this.address = address;
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}