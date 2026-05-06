package food_delivery_system.repository;

import food_delivery_system.util.FileUtil;
import java.util.*;

// SOLID: SRP → only handles payment file
public class PaymentRepository {

    private static final String FILE = "src/main/resources/data/payments.txt";

    public void save(String line) {
        FileUtil.writeLine(FILE, line);
    }

    public List<String> findAll() {
        return FileUtil.readAllLines(FILE);
    }

    public void overwrite(List<String> lines) {
        FileUtil.overwriteFile(FILE, lines);
    }
}