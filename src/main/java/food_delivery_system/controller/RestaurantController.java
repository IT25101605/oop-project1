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

// @Controller annotation marks this class as Controller layer
// MVC Architecture:
// Controller handles HTTP requests and sends responses/views
@Controller

// Base URL mapping for all owner-related routes
@RequestMapping("/owner")
public class RestaurantController {

    // Dependency Injection using @Autowired
    // Spring automatically injects required service objects

    // Service layer for restaurant business logic
    @Autowired
    private RestaurantService restaurantService;

    // Service layer for food operations
    @Autowired
    private FoodService foodService;

    // Service layer for order operations
    @Autowired
    private OrderService orderService;

    // Service layer for coupon operations
    @Autowired
    private food_delivery_system.service.CouponService couponService;

    // Service layer for review operations
    @Autowired
    private food_delivery_system.service.ReviewService reviewService;

    // @Value injects value from application.properties
    // Default folder is "uploads"
    @Value("${foodiego.uploads.dir:uploads}")
    private String uploadsDir;

    // Helper method for validating OWNER access
    // Authorization logic
    private User requireOwner(HttpSession s) {

        // Retrieving logged-in user from session
        User u = (User) s.getAttribute("user");

        // Validation for owner role
        if (u == null || !"OWNER".equalsIgnoreCase(u.getRole()))
            return null;

        return u;
    }

    // Handles owner dashboard page
    @GetMapping
    public String dashboard(HttpSession session,
                            Model model) {

        // Checking owner authentication
        User u = requireOwner(session);

        // Redirect to login if unauthorized
        if (u == null)
            return "redirect:/login";

        // Fetching owner's restaurants
        List<Restaurant> mine =
                restaurantService.byOwner(u.getId());

        // Sending restaurants to frontend
        model.addAttribute("restaurants", mine);

        // Sending food service object to view
        model.addAttribute("foodService", foodService);

        // Fetching all orders related to owner's restaurants
        java.util.List<Order> myOrders =
                orderService.all().stream()

                        // Stream API filtering
                        .filter(o -> mine.stream()
                                .anyMatch(r ->
                                        r.getId().equals(o.getRestaurantId())
                                ))

                        .toList();

        // Sending all orders
        model.addAttribute("orders", myOrders);

        // Filtering active orders
        model.addAttribute(
                "activeOrders",

                // Method reference used here
                myOrders.stream()
                        .filter(Order::isActive)
                        .toList()
        );

        // Filtering completed orders
        model.addAttribute(
                "completedOrders",

                myOrders.stream()
                        .filter(Order::isCompleted)
                        .toList()
        );

        // Filtering pending orders
        model.addAttribute(
                "pendingOrders",

                myOrders.stream()
                        .filter(o ->
                                "PENDING".equalsIgnoreCase(o.getStatus())
                        )
                        .toList()
        );

        // Filtering cancelled orders
        model.addAttribute(
                "cancelledOrders",

                myOrders.stream()
                        .filter(o ->
                                "CANCELLED".equalsIgnoreCase(o.getStatus())
                        )
                        .toList()
        );

        // Fetching restaurant IDs
        java.util.List<String> rids =
                mine.stream()

                        // Mapping Restaurant objects into IDs
                        .map(Restaurant::getId)

                        .toList();

        // Income calculations
        // Business logic handled in Service layer
        model.addAttribute(
                "incomeToday",
                orderService.ownerIncomeToday(rids)
        );

        model.addAttribute(
                "incomeMonth",
                orderService.ownerIncomeMonth(rids)
        );

        model.addAttribute(
                "incomeTotal",
                orderService.ownerIncomeTotal(rids)
        );

        // HashMap collection used
        // Key = orderId, Value = Review object
        java.util.Map<String,
                food_delivery_system.model.Review>
                reviewByOrder =
                new java.util.HashMap<>();

        // Looping through all reviews
        for (food_delivery_system.model.Review rv :
                reviewService.all()) {

            // Null validation
            if (rv.getOrderId() != null
                    && !rv.getOrderId().isBlank()) {

                // Storing review using order ID
                reviewByOrder.put(
                        rv.getOrderId(),
                        rv
                );
            }
        }

        // Sending reviews map to frontend
        model.addAttribute(
                "reviewByOrder",
                reviewByOrder
        );

        // HashMap for restaurant coupons
        java.util.Map<String,
                food_delivery_system.model.Coupon>
                couponByRestaurant =
                new java.util.HashMap<>();

        // Loop through owner's restaurants
        for (Restaurant r : mine) {

            // Fetching restaurant coupons
            java.util.List<food_delivery_system.model.Coupon> cs =
                    couponService.byRestaurant(r.getId());

            // Checking whether coupons exist
            if (!cs.isEmpty())

                // Storing first coupon
                couponByRestaurant.put(
                        r.getId(),
                        cs.get(0)
                );
        }

        // Sending coupon map to frontend
        model.addAttribute(
                "couponByRestaurant",
                couponByRestaurant
        );

        // Counting total foods across restaurants
        int totalFoods = 0;

        // Enhanced for-loop used
        for (Restaurant r : mine) {

            totalFoods +=
                    foodService.byRestaurant(r.getId()).size();
        }

        // Sending total food count
        model.addAttribute("totalFoods", totalFoods);

        // Returning dashboard page
        return "owner-dashboard";
    }

    // Displays add restaurant form
    @GetMapping("/restaurant/add")
    public String addRestaurant(HttpSession session,
                                Model model) {

        // Owner validation
        User u = requireOwner(session);

        if (u == null)
            return "redirect:/login";

        // Business rule:
        // One owner can have only one restaurant
        if (restaurantService.ownerHasRestaurant(u.getId())) {

            return "redirect:/owner?restaurantLimit=1";
        }

        // Returning add restaurant page
        return "add-restaurant";
    }

    // Handles add restaurant form submission
    @PostMapping("/restaurant/add")
    public String addRestaurantPost(
            @RequestParam String name,

            @RequestParam String city,

            @RequestParam String address,

            @RequestParam String cuisine,

            @RequestParam String description,

            @RequestParam(required=false) String latitude,

            @RequestParam(required=false) String longitude,

            // MultipartFile used for image upload
            @RequestParam(required=false) MultipartFile image,

            HttpSession session) {

        // Owner validation
        User u = requireOwner(session);

        if (u == null)
            return "redirect:/login";

        // Restricting multiple restaurants
        if (restaurantService.ownerHasRestaurant(u.getId())) {

            return "redirect:/owner?restaurantLimit=1";
        }

        // Saving uploaded image
        String img = saveImage(image);

        // Creating Restaurant object
        // Encapsulation:
        // Object stores restaurant data
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

    // Displays edit restaurant form
    @GetMapping("/restaurant/edit/{id}")
    public String editRestaurant(@PathVariable String id,
                                 HttpSession session,
                                 Model model) {

        User u = requireOwner(session);

        if (u == null)
            return "redirect:/login";

        // Fetching restaurant by ID
        Restaurant r =
                restaurantService.byId(id);

        // Authorization check
        if (r == null
                || !u.getId().equals(r.getOwnerId()))

            return "redirect:/owner";

        // Sending restaurant object to frontend
        model.addAttribute("r", r);

        return "edit-restaurant";
    }

    // Handles edit restaurant form submission
    @PostMapping("/restaurant/edit/{id}")
    public String editRestaurantPost(
            @PathVariable String id,

            @RequestParam String name,

            @RequestParam String city,

            @RequestParam String address,

            @RequestParam String cuisine,

            @RequestParam String description,

            @RequestParam(required=false) String latitude,

            @RequestParam(required=false) String longitude,

            @RequestParam(required=false) MultipartFile image,

            HttpSession session) {

        User u = requireOwner(session);

        if (u == null)
            return "redirect:/login";

        // Fetching restaurant
        Restaurant r =
                restaurantService.byId(id);

        // Security validation
        if (r == null
                || !u.getId().equals(r.getOwnerId()))

            return "redirect:/owner";

        // Updating restaurant details
        r.setName(name);
        r.setCity(city);
        r.setAddress(address);

        r.setCuisine(cuisine);
        r.setDescription(description);

        r.setLatitude(latitude);
        r.setLongitude(longitude);

        // Saving uploaded image
        String img = saveImage(image);

        // Updating image only if uploaded
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

        User u = requireOwner(session);

        if (u == null)
            return "redirect:/login";

        // Fetching restaurant
        Restaurant r =
                restaurantService.byId(id);

        // Owner validation before delete
        if (r != null
                && u.getId().equals(r.getOwnerId()))

            restaurantService.delete(id);

        return "redirect:/owner";
    }

    // Handles image upload and saving
    // File handling concept used here
    private String saveImage(MultipartFile image) {

        // Validation for empty image
        if (image == null || image.isEmpty())
            return null;

        try {

            // Creating upload directory path
            Path dir = Paths.get(uploadsDir);

            // Creating folder if not exists
            if (!Files.exists(dir))

                Files.createDirectories(dir);

            // Generating unique file name
            String fn =
                    System.currentTimeMillis()
                            + "_"
                            + image.getOriginalFilename()
                            .replaceAll("\\s+","_");

            // Copying uploaded image into folder
            Files.copy(
                    image.getInputStream(),
                    dir.resolve(fn),
                    StandardCopyOption.REPLACE_EXISTING
            );

            return fn;

        } catch (IOException e) {

            // Exception handling
            // Prevents application crash
            return null;
        }
    }
}
