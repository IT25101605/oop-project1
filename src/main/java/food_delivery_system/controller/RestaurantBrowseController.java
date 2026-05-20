package food_delivery_system.controller;

import food_delivery_system.model.Food;
import food_delivery_system.model.Order;
import food_delivery_system.model.Restaurant;
import food_delivery_system.model.User;
import food_delivery_system.repository.UserRepository;
import food_delivery_system.service.FoodService;
import food_delivery_system.service.OrderService;
import food_delivery_system.service.RestaurantService;
import food_delivery_system.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public class RestaurantBrowseController {
}
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

// @Controller marks this class as a Spring MVC Controller
// Controller layer handles client requests and communicates with Service layer
// MVC Architecture: Controller connects View and Business Logic
@Controller
public class RestaurantBrowseController {

    // Dependency Injection using @Autowired
    // Spring automatically injects service objects

    // Service class handles restaurant business logic
    @Autowired
    private RestaurantService restaurantService;

    // Food service used for food-related operations
    @Autowired private FoodService foodService;

    // Order service used to retrieve restaurant orders
    @Autowired private OrderService orderService;

    // Review service used for managing reviews
    @Autowired private ReviewService reviewService;

    // Repository pattern used for database/data access operations
    // Repository directly interacts with data source
    @Autowired private UserRepository userRepository;

    // Handles GET request for viewing restaurant list
    @GetMapping("/restaurants")
    public String browseRestaurants(HttpSession session, Model model) {

        // Getting logged-in user from session
        // Session management used for authentication
        User currentUser = (User) session.getAttribute("user");

        // Ternary operator used for checking customer city
        // Encapsulation: accessing private variables through getter methods
        String customerCity =
                currentUser != null &&
                        "CUSTOMER".equalsIgnoreCase(currentUser.getRole())
                        ? (currentUser.getCity() == null
                        ? ""
                        : currentUser.getCity().trim())
                        : "";

        // Fetching all restaurants using service layer
        List<Restaurant> restaurants = restaurantService.all();

        // Filtering restaurants based on customer city
        if (!customerCity.isBlank()) {

            // Java Stream API used for filtering collections
            // Functional programming concept used here
            restaurants = restaurants.stream()

                    // Lambda expression filters matching cities
                    .filter(r -> customerCity.equalsIgnoreCase(
                            r.getCity() == null
                                    ? ""
                                    : r.getCity().trim()
                    ))

                    // Converts stream back to list
                    .toList();
        }

        // Sending data from controller to frontend view
        model.addAttribute("restaurants", restaurants);
        model.addAttribute("customerCity", customerCity);
        model.addAttribute("reviewService", reviewService);

        // Returning frontend page name
        return "restaurants";
    }

    // Handles GET request for viewing single restaurant profile
    @GetMapping("/restaurants/{id}")
    public String restaurantProfile(@PathVariable String id,
                                    HttpSession session,
                                    Model model) {

        // Fetching restaurant using restaurant ID
        Restaurant restaurant = restaurantService.byId(id);

        // Null checking prevents errors
        if (restaurant == null) return "redirect:/restaurants";

        // Getting current logged-in user
        User currentUser = (User) session.getAttribute("user");

        // Validation to ensure customers can only access restaurants in their city
        if (currentUser != null &&
                "CUSTOMER".equalsIgnoreCase(currentUser.getRole())
                && currentUser.getCity() != null
                && !currentUser.getCity().isBlank()
                && !currentUser.getCity().trim().equalsIgnoreCase(
                restaurant.getCity() == null
                        ? ""
                        : restaurant.getCity().trim()
        )) {

            return "redirect:/restaurants";
        }

        // Fetching food items belonging to this restaurant
        // Aggregation relationship: Restaurant contains Food items
        List<Food> foods = foodService.byRestaurant(id);

        // Fetching all restaurant orders
        List<Order> orders = orderService.byRestaurant(id);

        // Fetching owner details using repository
        User owner = userRepository.findById(restaurant.getOwnerId());

        // Adding restaurant data to model
        model.addAttribute("restaurant", restaurant);

        // Sending foods list to view
        model.addAttribute("foods", foods);

        // Sending owner details
        model.addAttribute("owner", owner);

        // Sending order details
        model.addAttribute("orders", orders);

        // Stream API used to filter reviews related to restaurant orders
        model.addAttribute("reviews", reviewService.all().stream()

                // Filtering reviews matching order IDs
                .filter(rv -> orders.stream()
                        .anyMatch(o -> o.getId().equals(rv.getOrderId())))

                .toList());

        // Total number of orders
        model.addAttribute("totalOrders", orders.size());

        // Counting completed orders using method reference
        // Polymorphism may occur internally through overridden methods
        model.addAttribute("completedOrders",
                orders.stream().filter(Order::isCompleted).count());

        // Counting active orders
        model.addAttribute("activeOrders",
                orders.stream().filter(Order::isActive).count());

        // Returning restaurant profile page
        return "restaurant-profile";
    }

    // Handles admin access for restaurant profile
    @GetMapping("/admin/restaurants/{id}")
    public String adminRestaurantProfile(@PathVariable String id,
                                         HttpSession session,
                                         Model model) {

        // Getting logged-in admin user
        User currentUser = (User) session.getAttribute("user");

        // Authorization checking
        // Only ADMIN users can access this page
        if (currentUser == null ||
                !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {

            return "redirect:/admin-login";
        }

        // Reusing existing method to avoid duplicate code
        // Reusability is an important OOP principle
        return restaurantProfile(id, session, model);
    }
}

