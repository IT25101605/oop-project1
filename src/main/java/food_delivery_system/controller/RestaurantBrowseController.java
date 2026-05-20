package food_delivery_system.controller;

import food_delivery_system.model.*;
import food_delivery_system.repository.UserRepository;
import food_delivery_system.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

// @Controller annotation marks this class as Spring MVC Controller
// MVC Architecture:
// Controller layer handles user requests and responses
@Controller
public class RestaurantBrowseController {

    // Dependency Injection using @Autowired
    // Spring automatically injects required objects

    // Service class for restaurant business logic
    @Autowired
    private RestaurantService restaurantService;

    // Service class for food operations
    @Autowired
    private FoodService foodService;

    // Service class for order management
    @Autowired
    private OrderService orderService;

    // Service class for review operations
    @Autowired
    private ReviewService reviewService;

    // Repository class for user data access
    // Repository Pattern used here
    @Autowired
    private UserRepository userRepository;

    // Handles GET request for restaurant list page
    @GetMapping("/restaurants")
    public String browseRestaurants(HttpSession session,
                                    Model model) {

        // Getting logged-in user from session
        // Session management used for authentication
        User currentUser =
                (User) session.getAttribute("user");

        // Ternary operator used for conditional value assignment
        String customerCity =
                currentUser != null
                        && "CUSTOMER".equalsIgnoreCase(currentUser.getRole())

                        ? (currentUser.getCity() == null
                        ? ""
                        : currentUser.getCity().trim())

                        : "";

        // Fetching all restaurants
        List<Restaurant> restaurants =
                restaurantService.all();

        // Filtering restaurants based on customer city
        if (!customerCity.isBlank()) {

            // Stream API used for filtering collection data
            restaurants = restaurants.stream()

                    // Lambda expression used here
                    .filter(r -> customerCity.equalsIgnoreCase(
                            r.getCity() == null
                                    ? ""
                                    : r.getCity().trim()
                    ))

                    // Converts stream back into List
                    .toList();
        }

        // Sending restaurant list to frontend view
        model.addAttribute("restaurants", restaurants);

        // Sending customer city to frontend
        model.addAttribute("customerCity", customerCity);

        // Sending review service object
        model.addAttribute("reviewService", reviewService);

        // Returning HTML/Thymeleaf page name
        return "restaurants";
    }

    // Handles request for viewing single restaurant profile
    @GetMapping("/restaurants/{id}")
    public String restaurantProfile(@PathVariable String id,
                                    HttpSession session,
                                    Model model) {

        // Fetching restaurant using ID
        Restaurant restaurant =
                restaurantService.byId(id);

        // Validation checking
        // Redirect if restaurant does not exist
        if (restaurant == null)
            return "redirect:/restaurants";

        // Getting current logged-in user
        User currentUser =
                (User) session.getAttribute("user");

        // Security validation:
        // Customers can only view restaurants in their city
        if (currentUser != null
                && "CUSTOMER".equalsIgnoreCase(currentUser.getRole())
                && currentUser.getCity() != null
                && !currentUser.getCity().isBlank()
                && !currentUser.getCity().trim().equalsIgnoreCase(
                restaurant.getCity() == null
                        ? ""
                        : restaurant.getCity().trim()
        )) {

            return "redirect:/restaurants";
        }

        // Fetching food items belonging to restaurant
        // Aggregation relationship:
        // Restaurant contains multiple Food items
        List<Food> foods =
                foodService.byRestaurant(id);

        // Fetching orders for restaurant
        List<Order> orders =
                orderService.byRestaurant(id);

        // Fetching restaurant owner details
        User owner =
                userRepository.findById(
                        restaurant.getOwnerId()
                );

        // Sending restaurant details to frontend
        model.addAttribute("restaurant", restaurant);

        // Sending food list
        model.addAttribute("foods", foods);

        // Sending owner details
        model.addAttribute("owner", owner);

        // Sending orders list
        model.addAttribute("orders", orders);

        // Stream API used for filtering matching reviews
        model.addAttribute("reviews",
                reviewService.all().stream()

                        // Matching reviews with restaurant orders
                        .filter(rv -> orders.stream()
                                .anyMatch(o ->
                                        o.getId().equals(rv.getOrderId())
                                ))

                        .toList());

        // Sending total order count
        model.addAttribute("totalOrders", orders.size());

        // Counting completed orders
        // Method reference used here
        model.addAttribute("completedOrders",
                orders.stream()
                        .filter(Order::isCompleted)
                        .count());

        // Counting active orders
        model.addAttribute("activeOrders",
                orders.stream()
                        .filter(Order::isActive)
                        .count());

        // Returning restaurant profile page
        return "restaurant-profile";
    }

    // Handles admin access for restaurant profiles
    @GetMapping("/admin/restaurants/{id}")
    public String adminRestaurantProfile(@PathVariable String id,
                                         HttpSession session,
                                         Model model) {

        // Getting logged-in user
        User currentUser =
                (User) session.getAttribute("user");

        // Role-based authorization
        // Only ADMIN users can access this page
        if (currentUser == null
                || !"ADMIN".equalsIgnoreCase(currentUser.getRole()))

            return "redirect:/admin-login";

        // Reusing existing method
        // Reusability is an important OOP concept
        return restaurantProfile(id, session, model);
    }
}
