package food_delivery_system.service;

import food_delivery_system.model.Admin;
import food_delivery_system.util.FileUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    private static final String ADMIN_FILE = "src/main/resources/data/admins.txt";
    private static final String USER_FILE = "src/main/resources/data/users.txt";

    public boolean registerAdmin(Admin admin) {

        List<String> admins = FileUtil.readAllLines(ADMIN_FILE);

        for (String a : admins) {
            String[] data = a.split(",");
            if (data[2].equalsIgnoreCase(admin.getEmail())) {
                return false;
            }
        }

        String line = admin.getId() + "," +
                admin.getName() + "," +
                admin.getEmail() + "," +
                admin.getPassword() + "," +
                admin.getRole() + "," +
                admin.getPhone();

        FileUtil.writeLine(ADMIN_FILE, line);
        return true;
    }

    public Admin loginAdmin(String email, String password) {

        List<String> admins = FileUtil.readAllLines(ADMIN_FILE);

        for (String a : admins) {
            String[] data = a.split(",");

            if (data[2].equalsIgnoreCase(email) &&
                    data[3].equals(password)) {

                return new Admin(
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

    public List<String[]> getAllUsers() {
        List<String> users = FileUtil.readAllLines(USER_FILE);
        List<String[]> list = new ArrayList<>();

        for (String u : users) {
            list.add(u.split(","));
        }

        return list;
    }

    public boolean deleteUser(String email) {

        List<String> users = FileUtil.readAllLines(USER_FILE);
        List<String> updated = new ArrayList<>();

        boolean deleted = false;

        for (String u : users) {
            String[] data = u.split(",");

            if (!data[2].equalsIgnoreCase(email)) {
                updated.add(u);
            } else {
                deleted = true;
            }
        }

        if (deleted) {
            FileUtil.overwriteFile(USER_FILE, updated);
        }

        return deleted;
    }
}