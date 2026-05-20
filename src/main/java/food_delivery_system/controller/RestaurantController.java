package food_delivery_system.controller;

import food_delivery_system.model.*;
import food_delivery_system.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

// @Controller marks this class as a Spring MVC Controller
// MVC Architecture: Controller handles HTTP requests and responses
@Controller

// Base URL mapping for all methods inside this controller
@RequestMapping("/owner")
public class RestaurantController {

    // Dependency Injection using @Autowired
    // Service layer objects are automatically injected by Spring Framework

    // Handles restaurant-related business logic
    @Autowired private RestaurantService restaurantService;

    // Handles food-related operations
    @Autowired private FoodService foodService;

    // Handles order-related operations
    @Autowired private OrderService orderService;

    // Coupon service for coupon management
    @Autowired private food_delivery_system.service.CouponService couponService;

    // Review service for customer feedback management
    @Autowired private food_delivery_system.service.ReviewService reviewService;

    // Reads uploads directory path from application.properties
    // @Value annotation injects configuration values
    @Value("${foodiego.uploads.dir:uploads}")
    private String uploadsDir;

    // Private helper method for owner authorization checking
    // Encapsulation: private method accessible only inside this class
    private User requireOwner(HttpSession s) {

        // Getting logged-in user from session
        User u = (User) s.getAttribute("user");

        // Role-based authentication and authorization
        if (u == null || !"OWNER".equalsIgnoreCase(u.getRole()))
            return null;

        return u;
    }

    // Handles owner dashboard page request
    @GetMapping
    public String dashboard(HttpSession session, Model model) {

        // Checking whether current user is an OWNER
        User u = requireOwner(session);

        // Redirecting to login page if unauthorized
        if (u == null) return "redirect:/login";

        // Fetching restaurants owned by current user
        List<Restaurant> mine = restaurantService.byOwner(u.getId());

        // Sending restaurants list to frontend
        model.addAttribute("restaurants", mine);

        // Sending food service object to frontend
        model.addAttribute("foodService", foodService);

        // Stream API used for filtering orders
        java.util.List<Order> myOrders = orderService.all().stream()

                // Lambda expression used for filtering
                .filter(o -> mine.stream()
                        .anyMatch(r -> r.getId().equals(o.getRestaurantId())))

                .toList();

        // Sending orders list to frontend
        model.addAttribute("orders", myOrders);

        // Filtering active orders
        // Method references used here
        model.addAttribute("activeOrders",
                myOrders.stream().filter(Order::isActive).toList());

        // Filtering completed orders
        model.addAttribute("completedOrders",
                myOrders.stream().filter(Order::isCompleted).toList());

        // Filtering pending orders
        model.addAttribute("pendingOrders",
                myOrders.stream()
                        .filter(o -> "PENDING".equalsIgnoreCase(o.getStatus()))
                        .toList());

        // Filtering cancelled orders
        model.addAttribute("cancelledOrders",
                myOrders.stream()
                        .filter(o -> "CANCELLED".equalsIgnoreCase(o.getStatus()))
                        .toList());

        // Aggregation relationship:
        // One owner can manage multiple restaurants and orders

        // Collecting restaurant IDs
        java.util.List<String> rids = mine.stream()
                .map(Restaurant::getId)
                .toList();

        // Calculating owner's income
        model.addAttribute("incomeToday",
                orderService.ownerIncomeToday(rids));

        model.addAttribute("incomeMonth",
                orderService.ownerIncomeMonth(rids));

        model.addAttribute("incomeTotal",
                orderService.ownerIncomeTotal(rids));

        // HashMap collection used to store reviews by order ID
        java.util.Map<String,
                food_delivery_system.model.Review> reviewByOrder =
                new java.util.HashMap<>();

        // Looping through all reviews
        for (food_delivery_system.model.Review rv : reviewService.all()) {

            // Validation checking
            if (rv.getOrderId() != null &&
                    !rv.getOrderId().isBlank()) {

                // Adding review to map
                reviewByOrder.put(rv.getOrderId(), rv);
            }
        }

        // Sending review map to frontend
        model.addAttribute("reviewByOrder", reviewByOrder);

        // HashMap used for coupons by restaurant
        java.util.Map<String,
                food_delivery_system.model.Coupon> couponByRestaurant =
                new java.util.HashMap<>();

        // Looping through owner restaurants
        for (Restaurant r : mine) {

            // Fetching coupons for each restaurant
            java.util.List<food_delivery_system.model.Coupon> cs =
                    couponService.byRestaurant(r.getId());

            // Adding first coupon to map if available
            if (!cs.isEmpty())
                couponByRestaurant.put(r.getId(), cs.get(0));
        }

        // Sending coupon data to frontend
        model.addAttribute("couponByRestaurant", couponByRestaurant);
 imesh

        // Variable used for storing total food count
        int totalFoods = 0;

        // Looping through restaurants
        for (Restaurant r : mine) {

            // Counting foods in each restaurant
            totalFoods += foodService.byRestaurant(r.getId()).size();
        }

        // Sending total food count to frontend
        model.addAttribute("totalFoods", totalFoods);

        // Returning dashboard page

        int totalFoods = 0;
        for (Restaurant r : mine) {
            totalFoods += foodService.byRestaurant(r.getId()).size();
        }
        model.addAttribute("totalFoods", totalFoods);
 main
        return "owner-dashboard";
    }

    // Displays add restaurant form page
    @GetMapping("/restaurant/add")
    public String addRestaurant(HttpSession session, Model model) {

        // Checking owner authorization
        User u = requireOwner(session);

        if (u == null) return "redirect:/login";

        // Business validation:
        // One owner can have only one restaurant
        if (restaurantService.ownerHasRestaurant(u.getId())) {

            return "redirect:/owner?restaurantLimit=1";
        }

        return "add-restaurant";
    }

    // Handles restaurant creation form submission
    @PostMapping("/restaurant/add")
    public String addRestaurantPost(@RequestParam String name,
                                    @RequestParam String city,
                                    @RequestParam String address,
                                    @RequestParam String cuisine,
                                    @RequestParam String description,
                                    @RequestParam(required=false) String latitude,
                                    @RequestParam(required=false) String longitude,
                                    @RequestParam(required=false) MultipartFile image,
                                    HttpSession session) {

        // Authorization checking
        User u = requireOwner(session);

        if (u == null) return "redirect:/login";

        // Restricting restaurant creation
        if (restaurantService.ownerHasRestaurant(u.getId())) {

            return "redirect:/owner?restaurantLimit=1";
        }

        // Saving uploaded image
        String img = saveImage(image);

        // Creating Restaurant object
        // Abstraction using model class
        restaurantService.add(
                new Restaurant(
                        null,
                        u.getId(),
                        name,
                        city,
                        address,
                        cuisine,
                        img,
                        description,
                        latitude,
                        longitude
                )
        );

        return "redirect:/owner";
    }

    // Displays edit restaurant page
    @GetMapping("/restaurant/edit/{id}")
    public String editRestaurant(@PathVariable String id,
                                 HttpSession session,
                                 Model model) {

        // Authorization checking
        User u = requireOwner(session);

        if (u == null) return "redirect:/login";

        // Fetching restaurant details
        Restaurant r = restaurantService.byId(id);

        // Validation checking
        if (r == null || !u.getId().equals(r.getOwnerId()))
            return "redirect:/owner";

        // Sending restaurant object to frontend
        model.addAttribute("r", r);

        return "edit-restaurant";
    }

    // Handles restaurant update request
    @PostMapping("/restaurant/edit/{id}")
    public String editRestaurantPost(@PathVariable String id,
                                     @RequestParam String name,
                                     @RequestParam String city,
                                     @RequestParam String address,
                                     @RequestParam String cuisine,
                                     @RequestParam String description,
                                     @RequestParam(required=false) String latitude,
                                     @RequestParam(required=false) String longitude,
                                     @RequestParam(required=false) MultipartFile image,
                                     HttpSession session) {

        // Authorization checking
        User u = requireOwner(session);

        if (u == null) return "redirect:/login";

        // Fetching restaurant object
        Restaurant r = restaurantService.byId(id);

        // Security validation
        if (r == null || !u.getId().equals(r.getOwnerId()))
            return "redirect:/owner";

        // Encapsulation:
        // Updating private variables using setter methods
        r.setName(name);
        r.setCity(city);
        r.setAddress(address);
        r.setCuisine(cuisine);
        r.setDescription(description);
        r.setLatitude(latitude);
        r.setLongitude(longitude);

        // Saving new image if uploaded
        String img = saveImage(image);

        if (img != null)
            r.setImage(img);

        // Updating restaurant data
        restaurantService.update(r);

        return "redirect:/owner";
    }

    // Handles restaurant deletion
    @PostMapping("/restaurant/delete/{id}")
    public String deleteRestaurant(@PathVariable String id,
                                   HttpSession session) {

        // Authorization checking
        User u = requireOwner(session);

        if (u == null) return "redirect:/login";

        // Fetching restaurant object
        Restaurant r = restaurantService.byId(id);

        // Owner validation before deleting
        if (r != null && u.getId().equals(r.getOwnerId()))
            restaurantService.delete(id);

        return "redirect:/owner";
    }

    // Private helper method for file upload handling
    // File handling concept used here
    private String saveImage(MultipartFile image) {

        // Checking whether image exists
        if (image == null || image.isEmpty())
            return null;

        try {

            // Creating upload directory path
            Path dir = Paths.get(uploadsDir);

            // Creating directory if not exists
            if (!Files.exists(dir))
                Files.createDirectories(dir);

            // Generating unique file name using current time
            String fn = System.currentTimeMillis()
                    + "_"
                    + image.getOriginalFilename()
                    .replaceAll("\\s+","_");

            // Copying uploaded file into uploads directory
            Files.copy(
                    image.getInputStream(),
                    dir.resolve(fn),
                    StandardCopyOption.REPLACE_EXISTING
            );

            return fn;

        } catch (IOException e) {

            // Exception handling used for file upload errors
            // IOException occurs during file operations
            return null;
        }
    }
 imesh
}


}
 main
