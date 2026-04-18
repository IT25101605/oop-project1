package food_delivery_system.service;

import food_delivery_system.model.Customer;
import food_delivery_system.util.FileUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    private static final String USER_FILE = "src/main/resources/data/users.txt";

    public boolean registerCustomer(Customer customer) {
        List<String> users = FileUtil.readAllLines(USER_FILE);

        for (String user : users) {
            String[] data = user.split(",");
            if (data.length >= 3 && data[2].equalsIgnoreCase(customer.getEmail())) {
                return false;
            }
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

    public Customer loginCustomer(String email, String password) {
        List<String> users = FileUtil.readAllLines(USER_FILE);

        for (String user : users) {
            String[] data = user.split(",");
            if (data.length >= 7) {
                String savedEmail = data[2];
                String savedPassword = data[3];
                String role = data[4];

                if (savedEmail.equalsIgnoreCase(email)
                        && savedPassword.equals(password)
                        && role.equalsIgnoreCase("customer")) {

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
}