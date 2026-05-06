package food_delivery_system.util;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileUtil {

    // SOLID: Single Responsibility → ONLY handles file operations

    public static List<String> readAllLines(String path) {
        try {
            File file = new File(path);

            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            return Files.readAllLines(Paths.get(path));
        } catch (Exception e) {
            System.out.println("File Read Error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void writeLine(String path, String line) {
        try (FileWriter fw = new FileWriter(path, true)) {
            fw.write(line + "\n");
        } catch (Exception e) {
            System.out.println("Write Error: " + e.getMessage());
        }
    }

    public static void overwriteFile(String path, List<String> lines) {
        try {
            Files.write(Paths.get(path), lines);
        } catch (Exception e) {
            System.out.println("Overwrite Error: " + e.getMessage());
        }
    }
}