package food_delivery_system.service;

import food_delivery_system.model.Food;
import food_delivery_system.repository.FoodRepository;

import java.util.*;

// OOP: Abstraction → hides file operations from controller
public class FoodService {

    private final FoodRepository repo = new FoodRepository();

    // CREATE
    public void addFood(Food f) {

        String line = f.getId() + "," +
                f.getName() + "," +
                f.getPrice() + "," +
                f.getRestaurantId();

        repo.save(line);
    }

    // READ ALL
    public List<Food> getAllFoods() {

        List<String> lines = repo.findAll();
        List<Food> foods = new ArrayList<>();

        for (String line : lines) {
            String[] data = line.split(",");

            if (data.length == 4) {
                foods.add(new Food(
                        data[0],
                        data[1],
                        Double.parseDouble(data[2]),
                        data[3]
                ));
            }
        }

        return foods;
    }

    // UPDATE
    public void updateFood(Food updated) {

        List<String> lines = repo.findAll();
        List<String> newLines = new ArrayList<>();

        for (String line : lines) {
            String[] data = line.split(",");

            if (data[0].equals(updated.getId())) {

                String newLine = updated.getId() + "," +
                        updated.getName() + "," +
                        updated.getPrice() + "," +
                        updated.getRestaurantId();

                newLines.add(newLine);

            } else {
                newLines.add(line);
            }
        }

        repo.overwrite(newLines);
    }

    // DELETE
    public void deleteFood(String id) {

        List<String> lines = repo.findAll();
        List<String> newLines = new ArrayList<>();

        for (String line : lines) {
            if (!line.startsWith(id + ",")) {
                newLines.add(line);
            }
        }

        repo.overwrite(newLines);
    }

    // FIND ONE
    public Food getById(String id) {

        for (String line : repo.findAll()) {
            String[] data = line.split(",");

            if (data[0].equals(id)) {
                return new Food(
                        data[0],
                        data[1],
                        Double.parseDouble(data[2]),
                        data[3]
                );
            }
        }
        return null;
    }
}