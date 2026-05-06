package food_delivery_system.repository;

import food_delivery_system.util.FileUtil;
import java.util.*;

// SOLID: SRP → Only handles file operations
public class RestaurantRepository {

    private static final String FILE = "src/main/resources/data/restaurants.txt";

    // CREATE
    public void save(String line) {
        FileUtil.writeLine(FILE, line);
    }

    // READ
    public List<String> findAll() {
        return FileUtil.readAllLines(FILE);
    }

    // UPDATE + DELETE (overwrite)
    public void overwrite(List<String> lines) {
        FileUtil.overwriteFile(FILE, lines);
    }
}