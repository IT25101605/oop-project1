package food_delivery_system.service;

import food_delivery_system.model.Customer;
import food_delivery_system.model.RestaurantOwner;
import food_delivery_system.model.User;
import food_delivery_system.util.FileUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {

    private static final String USER_FILE = "src/main/resources/data/users.txt";

    public boolean emailExists(String email) {
        List<String> users = FileUtil.readAllLines(USER_FILE);

        for (String user : users) {
            String[] data = user.split(",");
            if (data.length >= 3 && data[2].equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    public boolean registerCustomer(Customer customer) {
        if (emailExists(customer.getEmail())) {
            return false;
        }

        String line = customer.getId() + "," +
                customer.getName() + "," +
                customer.getEmail() + "," +
                customer.getPassword() + "," +
                customer.getRole() + "," +
                customer.getAddress() + "," +
                customer.getPhone();

        FileUtil.writeLine(USER_FILE, line);
        return true;
    }

    public boolean registerOwner(RestaurantOwner owner) {
        if (emailExists(owner.getEmail())) {
            return false;
        }

        String line = owner.getId() + "," +
                owner.getName() + "," +
                owner.getEmail() + "," +
                owner.getPassword() + "," +
                owner.getRole() + "," +
                owner.getRestaurantName() + "," +
                owner.getPhone();

        FileUtil.writeLine(USER_FILE, line);
        return true;
    }

    public User loginUser(String email, String password, String role) {
        List<String> users = FileUtil.readAllLines(USER_FILE);

        for (String user : users) {
            String[] data = user.split(",");

            if (data.length >= 7) {
                String savedEmail = data[2];
                String savedPassword = data[3];
                String savedRole = data[4];

                if (savedEmail.equalsIgnoreCase(email)
                        && savedPassword.equals(password)
                        && savedRole.equalsIgnoreCase(role)) {

                    if ("customer".equalsIgnoreCase(role)) {
                        return new Customer(
                                data[0],
                                data[1],
                                data[2],
                                data[3],
                                data[4],
                                data[5],
                                data[6]
                        );
                    } else if ("owner".equalsIgnoreCase(role)) {
                        return new RestaurantOwner(
                                data[0],
                                data[1],
                                data[2],
                                data[3],
                                data[4],
                                data[5],
                                data[6]
                        );
                    }
                }
            }
        }

        return null;
    }

    public Customer getCustomerByEmail(String email) {
        List<String> users = FileUtil.readAllLines(USER_FILE);

        for (String user : users) {
            String[] data = user.split(",");

            if (data.length >= 7 &&
                    data[2].equalsIgnoreCase(email) &&
                    data[4].equalsIgnoreCase("customer")) {

                return new Customer(
                        data[0],
                        data[1],
                        data[2],
                        data[3],
                        data[4],
                        data[5],
                        data[6]
                );
            }
        }
        return null;
    }

    public RestaurantOwner getOwnerByEmail(String email) {
        List<String> users = FileUtil.readAllLines(USER_FILE);

        for (String user : users) {
            String[] data = user.split(",");

            if (data.length >= 7 &&
                    data[2].equalsIgnoreCase(email) &&
                    data[4].equalsIgnoreCase("owner")) {

                return new RestaurantOwner(
                        data[0],
                        data[1],
                        data[2],
                        data[3],
                        data[4],
                        data[5],
                        data[6]
                );
            }
        }
        return null;
    }

    public boolean updateCustomer(Customer updatedCustomer) {
        List<String> users = FileUtil.readAllLines(USER_FILE);
        List<String> updatedLines = new ArrayList<>();
        boolean updated = false;

        for (String user : users) {
            String[] data = user.split(",");

            if (data.length >= 7 &&
                    data[2].equalsIgnoreCase(updatedCustomer.getEmail()) &&
                    data[4].equalsIgnoreCase("customer")) {

                String newLine = updatedCustomer.getId() + "," +
                        updatedCustomer.getName() + "," +
                        updatedCustomer.getEmail() + "," +
                        updatedCustomer.getPassword() + "," +
                        updatedCustomer.getRole() + "," +
                        updatedCustomer.getAddress() + "," +
                        updatedCustomer.getPhone();

                updatedLines.add(newLine);
                updated = true;
            } else {
                updatedLines.add(user);
            }
        }

        if (updated) {
            FileUtil.overwriteFile(USER_FILE, updatedLines);
        }

        return updated;
    }

    public boolean updateOwner(RestaurantOwner updatedOwner) {
        List<String> users = FileUtil.readAllLines(USER_FILE);
        List<String> updatedLines = new ArrayList<>();
        boolean updated = false;

        for (String user : users) {
            String[] data = user.split(",");

            if (data.length >= 7 &&
                    data[2].equalsIgnoreCase(updatedOwner.getEmail()) &&
                    data[4].equalsIgnoreCase("owner")) {

                String newLine = updatedOwner.getId() + "," +
                        updatedOwner.getName() + "," +
                        updatedOwner.getEmail() + "," +
                        updatedOwner.getPassword() + "," +
                        updatedOwner.getRole() + "," +
                        updatedOwner.getRestaurantName() + "," +
                        updatedOwner.getPhone();

                updatedLines.add(newLine);
                updated = true;
            } else {
                updatedLines.add(user);
            }
        }

        if (updated) {
            FileUtil.overwriteFile(USER_FILE, updatedLines);
        }

        return updated;
    }

    public boolean deleteUserByEmail(String email, String role) {
        List<String> users = FileUtil.readAllLines(USER_FILE);
        List<String> updatedLines = new ArrayList<>();
        boolean deleted = false;

        for (String user : users) {
            String[] data = user.split(",");

            if (data.length >= 7 &&
                    data[2].equalsIgnoreCase(email) &&
                    data[4].equalsIgnoreCase(role)) {
                deleted = true;
            } else {
                updatedLines.add(user);
            }
        }

        if (deleted) {
            FileUtil.overwriteFile(USER_FILE, updatedLines);
        }

        return deleted;
    }
}