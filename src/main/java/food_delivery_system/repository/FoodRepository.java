package food_delivery_system.repository;

import food_delivery_system.util.FileUtil;
import java.util.*;

// SOLID: SRP → only handles file operations
public class FoodRepository {

    private static final String FILE = "src/main/resources/data/foods.txt";

    // CREATE
    public void save(String line) {
        FileUtil.writeLine(FILE, line);
    }

    // READ
    public List<String> findAll() {
        return FileUtil.readAllLines(FILE);
    }

    // UPDATE / DELETE (overwrite file)
    public void overwrite(List<String> lines) {
        FileUtil.overwriteFile(FILE, lines);
    }
}
