package food_delivery_system.service;

import food_delivery_system.util.FileUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final String USER_FILE = "src/main/resources/data/users.txt";

    /*
    -------------------------------------------------------
    REGISTER CUSTOMER (CREATE)
    -------------------------------------------------------
    */
    public boolean registerCustomer(String name, String email,
                                    String password, String address,
                                    String phone) {

        if (emailExists(email)) {
            return false; // prevent duplicate users
        }

        String id = UUID.randomUUID().toString();

        String line = id + "," + name + "," + email + "," +
                password + "," + address + "," + phone + ",CUSTOMER";

        FileUtil.writeLine(USER_FILE, line);
        return true;
    }

    /*
    -------------------------------------------------------
    LOGIN (READ + VALIDATION)
    -------------------------------------------------------
    */
    public String login(String email, String password) {

        List<String> users = FileUtil.readAllLines(USER_FILE);

        for (String user : users) {
            String[] data = safeSplit(user);

            if (data != null) {
                String storedEmail = data[2];
                String storedPassword = data[3];
                String role = data[6];

                if (storedEmail.equals(email) && storedPassword.equals(password)) {
                    return role;
                }
            }
        }

        return null;
    }

    /*
    -------------------------------------------------------
    GET CUSTOMER PROFILE (READ)
    -------------------------------------------------------
    */
    public String[] getCustomerByEmail(String email) {

        List<String> users = FileUtil.readAllLines(USER_FILE);

        for (String user : users) {
            String[] data = safeSplit(user);

            if (data != null &&
                    data[2].equals(email) &&
                    data[6].equals("CUSTOMER")) {

                return data;
            }
        }

        return null;
    }

    /*
    -------------------------------------------------------
    UPDATE CUSTOMER (UPDATE)
    -------------------------------------------------------
    */
    public boolean updateCustomer(String email, String name,
                                  String password, String address,
                                  String phone) {

        List<String> users = FileUtil.readAllLines(USER_FILE);
        List<String> updatedList = new ArrayList<>();

        boolean updated = false;

        for (String user : users) {
            String[] data = safeSplit(user);

            if (data != null &&
                    data[2].equals(email) &&
                    data[6].equals("CUSTOMER")) {

                String newLine = data[0] + "," + name + "," + email + "," +
                        password + "," + address + "," + phone + ",CUSTOMER";

                updatedList.add(newLine);
                updated = true;

            } else {
                updatedList.add(user);
            }
        }

        FileUtil.overwriteFile(USER_FILE, updatedList);
        return updated;
    }

    /*
    -------------------------------------------------------
    DELETE CUSTOMER (DELETE)
    -------------------------------------------------------
    */
    public boolean deleteCustomer(String email) {

        List<String> users = FileUtil.readAllLines(USER_FILE);
        List<String> updatedList = new ArrayList<>();

        boolean deleted = false;

        for (String user : users) {
            String[] data = safeSplit(user);

            if (data != null &&
                    data[2].equals(email) &&
                    data[6].equals("CUSTOMER")) {

                deleted = true;
                continue;
            }

            updatedList.add(user);
        }

        FileUtil.overwriteFile(USER_FILE, updatedList);
        return deleted;
    }

    /*
    -------------------------------------------------------
    CHECK EMAIL EXISTS
    -------------------------------------------------------
    */
    private boolean emailExists(String email) {

        List<String> users = FileUtil.readAllLines(USER_FILE);

        for (String user : users) {
            String[] data = safeSplit(user);

            if (data != null && data[2].equals(email)) {
                return true;
            }
        }

        return false;
    }

    /*
    -------------------------------------------------------
    SAFE SPLIT METHOD (IMPORTANT FIX)
    -------------------------------------------------------
    */
    private String[] safeSplit(String user) {

        if (user == null || user.trim().isEmpty()) {
            return null;
        }

        String[] data = user.split(",");

        // expect at least 7 fields
        if (data.length < 7) {
            System.out.println("Skipping invalid line: " + user);
            return null;
        }

        return data;
    }
}