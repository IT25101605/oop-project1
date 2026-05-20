package food_delivery_system.model;

public class Admin extends User {

    // Default Constructor: Used for creating an empty Admin object.
    public Admin() {
        super(); // Calls the parent (User) class constructor.
        setRole("ADMIN"); // Encapsulation: Modifying internal state via a setter.
    }

    // Parameterized Constructor: Used for object initialization with data
    // Polymorphism: Constructor overloading (if multiple constructors exist in User)
    public Admin(String id, String name, String email, String password) {
        //pass arguments to the parent class constructor.
        super(id, name, email, password, "", "ADMIN", "", "");
    }
}

