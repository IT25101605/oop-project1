package food_delivery_system.repository;

import food_delivery_system.util.FileUtil;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
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

    // UPDATE / DELETE
    public void overwrite(List<String> lines) {
        FileUtil.overwriteFile(FILE, lines);
    }
}