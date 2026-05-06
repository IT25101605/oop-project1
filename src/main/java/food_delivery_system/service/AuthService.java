package food_delivery_system.service;

import food_delivery_system.util.FileUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
-------------------------------------------------------
SERVICE LAYER (BUSINESS LOGIC)
-------------------------------------------------------
✔ Handles authentication logic
✔ Handles Customer CRUD (via users.txt)
✔ Does NOT handle UI or file I/O directly (uses FileUtil)

OOP Principle:
- Encapsulation: hides file operations
- SRP: only authentication + user management
-------------------------------------------------------
*/

@Service
public class AuthService {

    private final String USER_FILE = "src/main/resources/data/users.txt";

    /*
    -------------------------------------------------------
    REGISTER CUSTOMER (CREATE)
    -------------------------------------------------------
    Format in file:
    id,name,email,password,address,phone,role
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
            String[] data = user.split(",");

            if (data.length >= 7) {
                String storedEmail = data[2];
                String storedPassword = data[3];
                String role = data[6];

                if (storedEmail.equals(email) && storedPassword.equals(password)) {
                    return role; // CUSTOMER / ADMIN / OWNER
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
            String[] data = user.split(",");

            if (data[2].equals(email) && data[6].equals("CUSTOMER")) {
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
            String[] data = user.split(",");

            if (data[2].equals(email) && data[6].equals("CUSTOMER")) {

                // rebuild updated record
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
            String[] data = user.split(",");

            if (data[2].equals(email) && data[6].equals("CUSTOMER")) {
                deleted = true;
                continue; // skip this user
            }

            updatedList.add(user);
        }

        FileUtil.overwriteFile(USER_FILE, updatedList);
        return deleted;
    }

    /*
    -------------------------------------------------------
    CHECK EMAIL EXISTS (helper method)
    -------------------------------------------------------
    */
    private boolean emailExists(String email) {

        List<String> users = FileUtil.readAllLines(USER_FILE);

        for (String user : users) {
            String[] data = user.split(",");

            if (data[2].equals(email)) {
                return true;
            }
        }

        return false;
    }
}