package food_delivery_system.service;

import food_delivery_system.model.User;
import food_delivery_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
Handles authentication and registration business logic
Service layer acts between AuthController and UserRepository layers
 */
@Service
public class AuthService {

    // UserRepository used for file operations
    @Autowired
    private UserRepository userRepo;

    /*
     Expression for NIC validation
     new NIC format = 12 digits
     old NIC format = 9 digits followed by V or X
     */
    public static final String NIC_NUMBER_REGEX = "^(\\d{12}|\\d{9}[VX])$";

    /*
    Expression for vehicle number validation
    (ABC-1234 / WP-CAB-1234)
     */
    public static final String VEHICLE_NUMBER_REGEX = "^([A-Z]{2,3}-)?[A-Z]{2,3}-\\d{4}$";

    // Login method for user authentication
    public User login(String email, String password) {

        User u = userRepo.findByEmail(email);

        if (u != null && u.getPassword().equals(password))

            return u;

        return null;
    }

    // Handles user registration
    public String register(User u) {

        // Validation for empty email
        if (u.getEmail()==null || u.getEmail().isBlank())
            return "Email required";

        // Password validation
        if (u.getPassword()==null || u.getPassword().length() < 4)
            return "Password must be at least 4 chars";

        // Checks duplicate email registration
        if (userRepo.findByEmail(u.getEmail()) != null)
            return "Email already registered";

        // Default role
        if (u.getRole() == null || u.getRole().isBlank())
            u.setRole("CUSTOMER");

        // Rider-specific validation
        if ("RIDER".equalsIgnoreCase(u.getRole())) {

            // Calls separate validation method
            String err = validateRiderFields(u);

            if (err != null)
                return err;
        }

        userRepo.save(u);

        // null = registration successful
        return null;
    }

    //Validates rider registration fields
    public String validateRiderFields(User u) {

        //trim removes unwanted spaces
        String city = u.getCity() == null ? "" : u.getCity().trim();

        String vehicle = u.getVehicle() == null ? "" : u.getVehicle().trim();

        // Converts NIC to uppercase
        String nic = u.getLicenseNumber() == null ? "" :
                u.getLicenseNumber().trim().toUpperCase();

        // Converts vehicle number to uppercase
        String vehicleNumber = u.getLicensePlate() == null ? "" :
                u.getLicensePlate().trim().toUpperCase();

        if (city.isBlank())
            return "Service city is required for rider registration";

        if (vehicle.isBlank())
            return "Vehicle type is required for rider registration";

        if (!nic.matches(NIC_NUMBER_REGEX))

            return "ID / NIC number must be 12 digits or old NIC format: 9 digits followed by V or X";

        if (!vehicleNumber.matches(VEHICLE_NUMBER_REGEX))

            return "Vehicle number must use ABC-1234 or WP-CAB-1234 format";

        // Encapsulation using setter methods
        u.setCity(city);
        u.setVehicle(vehicle);
        u.setLicenseNumber(nic);
        u.setLicensePlate(vehicleNumber);

        return null;
    }

    // Encapsulation - User object fields accessed through getters/setters
    // Abstraction - Service layer hides business logic from controller
    // Composition - AuthService uses UserRepository object

    // SOLID Principles ;
    // Single Responsibility Principle:
    // This class only handles authentication and registration logic

    // Dependency Inversion Principle;
    // Service depends on repository layer instead of direct file handling


}