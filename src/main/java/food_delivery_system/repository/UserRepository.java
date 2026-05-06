package food_delivery_system.repository;

import food_delivery_system.util.FileUtil;
import java.util.*;

public class UserRepository {

    private static final String FILE = "src/main/resources/data/users.txt";

    // SRP: only file operations for users

    public void save(String line) {
        FileUtil.writeLine(FILE, line);
    }

    public List<String> findAll() {
        return FileUtil.readAllLines(FILE);
    }

    public void update(List<String> lines) {
        FileUtil.overwriteFile(FILE, lines);
    }
}