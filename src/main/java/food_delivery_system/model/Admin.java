package food_delivery_system.model;

public class Admin extends User {

    private String phone;

    public Admin() {
    }

    public Admin(String id, String name, String email, String password, String role, String phone) {
        super(id, name, email, password, role);
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}