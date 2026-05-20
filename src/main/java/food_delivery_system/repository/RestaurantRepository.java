package food_delivery_system.repository;

import food_delivery_system.model.Restaurant;
import food_delivery_system.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

// @Repository annotation marks this class as Repository layer
// Repository Pattern:
// Handles database/file CRUD operations
// MVC Architecture:
// Repository communicates with data source
@Repository
public class RestaurantRepository {

    // Text file used for storing restaurant data
    // File handling concept used here
    private static final String FILE = "restaurants.txt";

    // Dependency Injection using @Autowired
    // FileUtil object injected automatically by Spring
    @Autowired
    private FileUtil fileUtil;

    // Retrieves all restaurants from file
    public List<Restaurant> findAll() {

        return fileUtil.readAllLines(FILE)

                // Stream API processes collections efficiently
                .stream()

                // Filtering blank lines
                .filter(l -> !l.isBlank())

                // Converting text lines into Restaurant objects
                .map(this::parse)

                // Collecting results into List collection
                .collect(Collectors.toList());
    }

    // Finds restaurant using restaurant ID
    public Restaurant findById(String id) {

        return findAll().stream()

                // Lambda expression used for filtering
                .filter(r -> r.getId().equals(id))

                // Returns first matching object
                .findFirst()

                // Returns null if no match found
                .orElse(null);
    }

    // Finds restaurants belonging to a specific owner
    // Aggregation relationship:
    // One owner can manage multiple restaurants
    public List<Restaurant> findByOwner(String ownerId) {

        return findAll().stream()

                // Filtering restaurants by owner ID
                .filter(r -> ownerId.equals(r.getOwnerId()))

                .collect(Collectors.toList());
    }

    // Saves new restaurant into file
    public Restaurant save(Restaurant r) {

        // Auto-generating restaurant ID if missing
        if (r.getId() == null || r.getId().isBlank())

            // Static utility method used
            r.setId("R-" + FileUtil.nextId());

        // Appending restaurant data into text file
        fileUtil.appendLine(FILE, toLine(r));

        return r;
    }

    // Updates existing restaurant details
    public void update(Restaurant r) {

        // Creating updated list of file lines
        List<String> lines = findAll().stream()

                // Replacing matching restaurant object
                .map(x -> toLine(
                        x.getId().equals(r.getId()) ? r : x))

                .collect(Collectors.toList());

        // Writing updated data back into file
        fileUtil.writeAllLines(FILE, lines);
    }

    // Deletes restaurant using ID
    public void delete(String id) {

        // Filtering restaurants except deleted one
        List<String> lines = findAll().stream()

                .filter(r -> !r.getId().equals(id))

                // Converting Restaurant objects into file lines
                .map(this::toLine)

                .collect(Collectors.toList());

        // Saving updated file content
        fileUtil.writeAllLines(FILE, lines);
    }

    // Converts Restaurant object into text line format
    // Serialization logic used for file storage
    private String toLine(Restaurant r) {

        return FileUtil.join(
                r.getId(),
                r.getOwnerId(),
                r.getName(),
                r.getCity(),
                r.getAddress(),
                r.getCuisine(),
                r.getImage(),
                r.getDescription(),
                r.getLatitude(),
                r.getLongitude()
        );
    }

    // Converts text line into Restaurant object
    // Deserialization logic
    private Restaurant parse(String l) {

        // Splitting file line into array values
        String[] p = FileUtil.split(l);

        // Creating Restaurant object from file data
        return new Restaurant(
                g(p,0),
                g(p,1),
                g(p,2),
                g(p,3),
                g(p,4),
                g(p,5),
                g(p,6),
                g(p,7),
                g(p,8),
                g(p,9)
        );
    }

    // Helper method for safely accessing array values
    // Prevents ArrayIndexOutOfBoundsException
    private static String g(String[] a, int i){

        return i < a.length ? a[i] : "";
    }
}

