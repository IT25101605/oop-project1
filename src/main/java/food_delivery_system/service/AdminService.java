package food_delivery_system.service;

import food_delivery_system.util.FileUtil;
import java.util.*;

// Solid: SRP → Admin handles system-level operations only
public class AdminService {

    private final String USERS = "src/main/resources/data/users.txt";
    private final String RESTAURANTS = "src/main/resources/data/restaurants.txt";
    private final String ORDERS = "src/main/resources/data/orders.txt";

    //  Login (hardcoded for simplicity)
    public boolean login(String username, String password) {
        return username.equals("admin") && password.equals("123");
    }

    // Read all data
    public List<String> getAllUsers() {
        return FileUtil.readAllLines(USERS);
    }

    public List<String> getAllRestaurants() {
        return FileUtil.readAllLines(RESTAURANTS);
    }

    public List<String> getAllOrders() {
        return FileUtil.readAllLines(ORDERS);
    }

    //Delete user
    public void deleteUser(String email) {

        List<String> updated = new ArrayList<>();

        for (String line : FileUtil.readAllLines(USERS)) {
            if (!line.contains(email)) {
                updated.add(line);
            }
        }

        FileUtil.overwriteFile(USERS, updated);
    }

    // ❌ DELETE RESTAURANT
    public void deleteRestaurant(String id) {

        List<String> updated = new ArrayList<>();

        for (String line : FileUtil.readAllLines(RESTAURANTS)) {
            if (!line.startsWith(id + ",")) {
                updated.add(line);
            }
        }

        FileUtil.overwriteFile(RESTAURANTS, updated);
    }

    //Delete order
    public void deleteOrder(String id) {

        List<String> updated = new ArrayList<>();

        for (String line : FileUtil.readAllLines(ORDERS)) {
            if (!line.startsWith(id + ",")) {
                updated.add(line);
            }
        }

        FileUtil.overwriteFile(ORDERS, updated);
    }
}