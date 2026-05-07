package food_delivery_system.service;

import food_delivery_system.model.Restaurant;
import food_delivery_system.repository.RestaurantRepository;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RestaurantService {

    private final RestaurantRepository repo =
            new RestaurantRepository();

    // CREATE
    public void addRestaurant(Restaurant r) {

        String line =
                r.getId() + "," +
                        r.getName() + "," +
                        r.getLocation() + "," +
                        r.getOwnerEmail();

        repo.save(line);
    }

    // READ All
    public List<Restaurant> getAllRestaurants() {

        List<String> lines = repo.findAll();

        List<Restaurant> list =
                new ArrayList<>();

        for (String line : lines) {

            if (line == null ||
                    line.trim().isEmpty()) {

                continue;
            }

            String[] data = line.split(",");

            if (data.length == 4) {

                list.add(
                        new Restaurant(
                                data[0],
                                data[1],
                                data[2],
                                data[3]
                        )
                );
            }
        }

        return list;
    }

    // OWNER RESTAURANTS ONLY
    public List<Restaurant> getRestaurantsByOwner(
            String ownerEmail) {

        List<Restaurant> allRestaurants =
                getAllRestaurants();

        List<Restaurant> result =
                new ArrayList<>();

        for (Restaurant r : allRestaurants) {

            if (r.getOwnerEmail()
                    .equals(ownerEmail)) {

                result.add(r);
            }
        }

        return result;
    }

    // UPDATE
    public void updateRestaurant(
            Restaurant updated) {

        List<String> lines =
                repo.findAll();

        List<String> newLines =
                new ArrayList<>();

        for (String line : lines) {

            String[] data = line.split(",");

            if (data[0]
                    .equals(updated.getId())) {

                String newLine =
                        updated.getId() + "," +
                                updated.getName() + "," +
                                updated.getLocation() + "," +
                                updated.getOwnerEmail();

                newLines.add(newLine);

            } else {

                newLines.add(line);
            }
        }

        repo.overwrite(newLines);
    }

    // DELETE
    public void deleteRestaurant(String id) {

        List<String> lines =
                repo.findAll();

        List<String> newLines =
                new ArrayList<>();

        for (String line : lines) {

            if (!line.startsWith(id + ",")) {

                newLines.add(line);
            }
        }

        repo.overwrite(newLines);
    }

    // FIND ONE
    public Restaurant getById(String id) {

        for (String line : repo.findAll()) {

            String[] data = line.split(",");

            if (data[0].equals(id)) {

                return new Restaurant(
                        data[0],
                        data[1],
                        data[2],
                        data[3]
                );
            }
        }

        return null;
    }
}