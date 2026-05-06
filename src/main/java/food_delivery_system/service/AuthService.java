package food_delivery_system.service;

import food_delivery_system.model.Customer;
import food_delivery_system.util.FileUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final String USER_FILE = "src/main/resources/data/users.txt";

    // ================= REGISTER =================
    public boolean registerCustomer(Customer customer) {

        if (emailExists(customer.getEmail())) {
            return false;
        }

        String id = UUID.randomUUID().toString();

        String line = id + "," +
                customer.getName() + "," +
                customer.getEmail() + "," +
                customer.getPassword() + "," +
                customer.getAddress() + "," +
                customer.getPhone() + ",CUSTOMER";

        FileUtil.writeLine(USER_FILE, line);
        return true;
    }

    // ================= LOGIN =================
    public Customer loginUser(String email, String password, String role) {

        List<String> users = FileUtil.readAllLines(USER_FILE);

        for (String user : users) {

            if (user == null || user.trim().isEmpty()) continue;

            String[] data = user.split(",");

            if (data.length >= 7) {

                String storedEmail = data[2];
                String storedPassword = data[3];
                String storedRole = data[6];

                if (storedEmail.equals(email)
                        && storedPassword.equals(password)
                        && storedRole.equalsIgnoreCase(role)) {

                    return new Customer(
                            data[0],
                            data[1],
                            data[2],
                            data[3],
                            data[4],
                            data[5]
                    );
                }
            }
        }

        return null;
    }

    // ================= GET CUSTOMER =================
    public Customer getCustomerByEmail(String email) {

        List<String> users = FileUtil.readAllLines(USER_FILE);

        for (String user : users) {

            if (user == null || user.trim().isEmpty()) continue;

            String[] data = user.split(",");

            if (data.length >= 7 &&
                    data[2].equals(email) &&
                    data[6].equals("CUSTOMER")) {

                return new Customer(
                        data[0],
                        data[1],
                        data[2],
                        data[3],
                        data[4],
                        data[5]
                );
            }
        }

        return null;
    }

    // ================= UPDATE =================
    public boolean updateCustomer(Customer updatedCustomer) {

        List<String> users = FileUtil.readAllLines(USER_FILE);
        List<String> updatedList = new ArrayList<>();

        boolean updated = false;

        for (String user : users) {

            if (user == null || user.trim().isEmpty()) continue;

            String[] data = user.split(",");

            if (data.length >= 7 &&
                    data[2].equals(updatedCustomer.getEmail()) &&
                    data[6].equals("CUSTOMER")) {

                String newLine = data[0] + "," +
                        updatedCustomer.getName() + "," +
                        updatedCustomer.getEmail() + "," +
                        updatedCustomer.getPassword() + "," +
                        updatedCustomer.getAddress() + "," +
                        updatedCustomer.getPhone() + ",CUSTOMER";

                updatedList.add(newLine);
                updated = true;

            } else {
                updatedList.add(user);
            }
        }

        FileUtil.overwriteFile(USER_FILE, updatedList);
        return updated;
    }

    // ================= DELETE =================
    public boolean deleteUserByEmail(String email, String role) {

        List<String> users = FileUtil.readAllLines(USER_FILE);
        List<String> updatedList = new ArrayList<>();

        boolean deleted = false;

        for (String user : users) {

            if (user == null || user.trim().isEmpty()) continue;

            String[] data = user.split(",");

            if (data.length >= 7 &&
                    data[2].equals(email) &&
                    data[6].equalsIgnoreCase(role)) {

                deleted = true;
                continue;
            }

            updatedList.add(user);
        }

        FileUtil.overwriteFile(USER_FILE, updatedList);
        return deleted;
    }

    // ================= EMAIL EXISTS =================
    private boolean emailExists(String email) {

        List<String> users = FileUtil.readAllLines(USER_FILE);

        for (String user : users) {

            if (user == null || user.trim().isEmpty()) continue;

            String[] data = user.split(",");

            if (data.length >= 3 && data[2].equals(email)) {
                return true;
            }
        }

        return false;
    }
}