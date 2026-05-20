package food_delivery_system.repository;

import food_delivery_system.model.Coupon;
import food_delivery_system.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

// @Repository annotation marks this class as Repository layer
// Repository Pattern:
// Handles data storage and retrieval operations
// MVC Architecture:
// Repository works between Service layer and data source
@Repository
public class CouponRepository {

    // File name used for storing coupon data
    // File handling concept used here
    private static final String FILE = "coupons.txt";

    // Dependency Injection using @Autowired
    // Utility class injected automatically by Spring
    @Autowired
    private FileUtil fileUtil;

    // Fetches all coupons from file
    public List<Coupon> findAll() {

        // Reading all lines from text file
        return fileUtil.readAllLines(FILE)

                // Stream API used for collection processing
                .stream()

                // Filtering empty lines
                .filter(l -> !l.isBlank())

                // Converting text lines into Coupon objects
                // Method reference used here
                .map(this::parse)

                // Collecting results into List
                .collect(Collectors.toList());
    }

    // Finds coupon by ID
    public Coupon findById(String id) {

        return findAll().stream()

                // Lambda expression used for filtering
                .filter(c -> c.getId().equals(id))

                // Returns first matching object
                .findFirst()

                // Returns null if not found
                .orElse(null);
    }

    // Finds all coupons belonging to a restaurant
    // Aggregation relationship:
    // One restaurant can have multiple coupons
    public List<Coupon> findByRestaurant(String rid) {

        return findAll().stream()

                // Filtering by restaurant ID
                .filter(c -> rid.equals(c.getRestaurantId()))

                .collect(Collectors.toList());
    }

    // Finds coupon using coupon code and restaurant ID
    public Coupon findByCodeAndRestaurant(String code,
                                          String restaurantId) {

        // Null validation
        if (code == null) return null;

        // Formatting coupon code
        String c = code.trim().toUpperCase();

        return findAll().stream()

                // Matching coupon code and restaurant
                .filter(x ->
                        x.getCode().equals(c)
                                && x.getRestaurantId().equals(restaurantId))

                .findFirst()

                .orElse(null);
    }

    // Saves new coupon into file
    public Coupon save(Coupon c) {

        // Auto-generating coupon ID
        if (c.getId() == null || c.getId().isBlank())

            // Static method call
            c.setId("CP-" + FileUtil.nextId());

        // Writing coupon data into text file
        fileUtil.appendLine(FILE, toLine(c));

        return c;
    }

    // Updates existing coupon
    public void update(Coupon c) {

        // Creating updated list of coupon lines
        List<String> lines = findAll().stream()

                // Replacing matching coupon object
                .map(x -> toLine(
                        x.getId().equals(c.getId()) ? c : x))

                .collect(Collectors.toList());

        // Overwriting file with updated data
        fileUtil.writeAllLines(FILE, lines);
    }

    // Deletes coupon by ID
    public void delete(String id) {

        // Filtering out deleted coupon
        List<String> lines = findAll().stream()

                .filter(c -> !c.getId().equals(id))

                // Converting objects into file lines
                .map(this::toLine)

                .collect(Collectors.toList());

        // Writing remaining data back into file
        fileUtil.writeAllLines(FILE, lines);
    }

    // Converts Coupon object into single text line
    // File serialization logic
    private String toLine(Coupon c) {

        // Utility method joins values into file format
        return FileUtil.join(
                c.getId(),
                c.getRestaurantId(),
                c.getCode(),
                c.getType(),
                c.getValue(),
                c.getMinOrder(),
                c.getExpiryDate(),

                // Boolean converted into text format
                c.isEnabled() ? "1" : "0",

                c.getDescription()
        );
    }

    // Converts file line into Coupon object
    // File deserialization logic
    private Coupon parse(String l) {

        // Splitting line into array values
        String[] p = FileUtil.split(l);

        // Default values
        double v = 0, m = 0;

        try {

            // Parsing discount value
            v = Double.parseDouble(g(p,4));

        } catch(Exception ignored) {

            // Exception ignored to prevent application crash
        }

        try {

            // Parsing minimum order value
            m = Double.parseDouble(g(p,5));

        } catch(Exception ignored) {

            // Exception ignored safely
        }

        // Boolean conversion
        boolean enabled =
                "1".equals(g(p,7))
                        || "true".equalsIgnoreCase(g(p,7));

        // Creating Coupon object
        return new Coupon(
                g(p,0),
                g(p,1),
                g(p,2),
                g(p,3),
                v,
                m,
                g(p,6),
                enabled,
                g(p,8)
        );
    }

    // Helper method for safely reading array values
    // Prevents ArrayIndexOutOfBoundsException
    private static String g(String[] a, int i){

        return i < a.length ? a[i] : "";
    }
}

