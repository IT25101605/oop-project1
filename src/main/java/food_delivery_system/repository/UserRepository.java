package food_delivery_system.repository;

import food_delivery_system.model.User;
import food_delivery_system.util.FileUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/*
UserRepository is used to handle data storage operations
this  manages user data using users.txt file
stores Customer, Owner, Rider, and Admin details
 */
@Repository
public class UserRepository {

    // File used for storing user data
    private static final String FILE = "users.txt";

    // FileUtil handles file reading/writing operations
    @Autowired
    private FileUtil fileUtil;

    // default admin account
    @PostConstruct
    public void seed() {

        if (findByEmail("admin@dinedrop.com") == null) {

            save(new User("U-ADMIN", "Admin", "admin@dinedrop.com", "admin123",
                    "0000000000", "ADMIN", "HQ", "", "", ""));
        }
    }

    // Returns all users from users.txt
    public List<User> findAll() {

        return fileUtil.readAllLines(FILE).stream()

                // Removes blank lines
                .filter(l -> !l.isBlank())

                // Converts text line into User object
                .map(this::parse)

                // Converts stream into List collection
                .collect(Collectors.toList());
    }

    // Finds user using ID
    public User findById(String id) {

        // Stream filtering operation
        return findAll().stream()

                // Lambda expression used
                .filter(u -> u.getId().equals(id))

                // Returns first matching user
                .findFirst()

                // Returns null if not found
                .orElse(null);
    }

    // Finds user using email
    public User findByEmail(String email) {

        return findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    // Returns users based on role
    public List<User> findByRole(String role) {

        return findAll().stream()
                .filter(u -> u.getRole().equalsIgnoreCase(role))
                .collect(Collectors.toList());
    }

    // Saves new user into file
    public User save(User u) {

        // Generates ID if ID is empty
        if (u.getId() == null || u.getId().isBlank()) {

            // Static utility method used
            u.setId("U-" + FileUtil.nextId());
        }

        // Appends user data into file
        fileUtil.appendLine(FILE, toLine(u));

        return u;
    }

    // Updates existing user data
    public void update(User u) {

        List<User> all = findAll();

        List<String> lines = new ArrayList<>();

        for (User x : all)

            // Ternary operator
            lines.add(toLine(x.getId().equals(u.getId()) ? u : x));

        fileUtil.writeAllLines(FILE, lines);
    }

    // Deletes user from file
    public void delete(String id) {

        List<String> lines = findAll().stream()

                .filter(u -> !u.getId().equals(id))

                .map(this::toLine)

                .collect(Collectors.toList());

        fileUtil.writeAllLines(FILE, lines);
    }

    // Converts User object into text line format
    private String toLine(User u) {

        return FileUtil.join(u.getId(), u.getName(), u.getEmail(), u.getPassword(),
                u.getPhone(), u.getRole(), u.getCity(), u.getVehicle(),

                u.getLicenseNumber() == null ? "" : u.getLicenseNumber(),
                u.getLicensePlate() == null ? "" : u.getLicensePlate());
    }

    private User parse(String line) {

        String[] p = FileUtil.split(line);

        return new User(get(p,0), get(p,1), get(p,2), get(p,3), get(p,4), get(p,5),
                get(p,6), get(p,7), get(p,8), get(p,9));
    }

    private static String get(String[] a, int i){

        return i<a.length ? a[i] : "";
    }

    // OOP Concepts Used:
    // 1. Encapsulation - User object uses private variables with getters/setters
    // 2. Abstraction - Repository hides file handling implementation
    // 3. Composition - UserRepository uses FileUtil object




}