package food_delivery_system.controller;

import food_delivery_system.model.*;
import food_delivery_system.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

// @Controller annotation marks this class as a Spring MVC Controller
// Controller layer handles HTTP requests and responses in MVC architecture
@Controller
public class CouponController {

    // Dependency Injection using @Autowired
    // Service layer object is injected automatically by Spring Framework
    @Autowired private CouponService couponService;

    // Restaurant service used for restaurant-related business operations
    @Autowired private RestaurantService restaurantService;

    // Private helper method used for authentication and authorization
    // Encapsulation: method is private and accessible only inside this class
    private User requireOwner(HttpSession s) {

        // Getting logged-in user object from session
        User u = (User) s.getAttribute("user");

        // Checking whether user is logged in and role is OWNER
        // Basic validation and security checking
        if (u == null || !"OWNER".equalsIgnoreCase(u.getRole())) return null;

        return u;
    }

    // Handles GET request for viewing coupons page
    // MVC Controller responsibility: sending data to frontend view
    @GetMapping("/owner/coupons")
    public String list(HttpSession s, Model m) {

        // Checking owner authorization
        User u = requireOwner(s);

        // Redirecting to login page if user is not authenticated
        if (u == null) return "redirect:/login";

        // Calling service layer to get restaurants owned by current user
        List<Restaurant> myRestaurants = restaurantService.byOwner(u.getId());

        // Collection Framework usage
        // ArrayList used to store all coupons from multiple restaurants
        // Aggregation relationship: Restaurant contains Coupons
        java.util.List<Coupon> all = new java.util.ArrayList<>();

        // Looping through restaurants and collecting coupons
        for (Restaurant r : myRestaurants)
            all.addAll(couponService.byRestaurant(r.getId()));

        // Adding attributes to Model object to send data to frontend
        m.addAttribute("coupons", all);
        m.addAttribute("restaurants", myRestaurants);
        m.addAttribute("restaurantService", restaurantService);

        // Returning Thymeleaf/JSP page name
        return "manage-coupons";
    }

    // Handles POST request for saving or updating coupons
    @PostMapping("/owner/coupons/save")
    public String save(@RequestParam(required = false) String id,
                       @RequestParam String restaurantId,
                       @RequestParam String code,
                       @RequestParam(defaultValue = "PERCENT") String type,
                       @RequestParam double value,
                       @RequestParam(defaultValue = "0") double minOrder,
                       @RequestParam(required = false) String expiryDate,
                       @RequestParam(required = false) String enabled,
                       @RequestParam(required = false) String description,
                       HttpSession s, RedirectAttributes ra) {

        // Converting checkbox/string value into boolean
        boolean enabledFlag =
                "true".equalsIgnoreCase(enabled) ||
                        "on".equalsIgnoreCase(enabled) ||
                        "1".equals(enabled);

        // Authorization checking
        User u = requireOwner(s);

        if (u == null) return "redirect:/login";

        // Fetching restaurant details using service layer
        Restaurant r = restaurantService.byId(restaurantId);

        // Validation to ensure owner manages only own restaurants
        if (r == null || !u.getId().equals(r.getOwnerId())) {

            // Flash attribute used to temporarily send error message
            ra.addFlashAttribute("error", "Invalid restaurant");

            return "redirect:/owner/coupons";
        }

        // Ternary operator used for checking update or create operation
        Coupon c = (id != null && !id.isBlank()) ? couponService.byId(id) : null;

        // If coupon does not exist, create new coupon
        if (c == null) {

            // Object creation using constructor
            // OOP concept: Abstraction using model class
            c = new Coupon(
                    null,
                    restaurantId,
                    code,
                    type,
                    value,
                    minOrder,
                    expiryDate,
                    enabledFlag,
                    description
            );

            // Repository/service operation for saving data
            couponService.save(c);

            ra.addFlashAttribute("success", "Coupon created");

        } else {

            // Updating coupon object using setter methods
            // Encapsulation: private variables accessed through setters
            c.setRestaurantId(restaurantId);
            c.setCode(code);
            c.setType(type);
            c.setValue(value);
            c.setMinOrder(minOrder);
            c.setExpiryDate(expiryDate);
            c.setEnabled(enabledFlag);
            c.setDescription(description);

            // Updating existing coupon
            couponService.update(c);

            ra.addFlashAttribute("success", "Coupon updated");
        }

        // Getting previous page URL using request headers
        // Used for redirecting user back to previous page
        String referer = ((jakarta.servlet.http.HttpServletRequest)
                ((org.springframework.web.context.request.ServletRequestAttributes)
                        org.springframework.web.context.request.RequestContextHolder
                                .currentRequestAttributes()).getRequest())
                .getHeader("Referer");

        // Redirecting based on previous page
        if (referer != null &&
                referer.contains("/owner") &&
                !referer.contains("/owner/coupons")) {

            return "redirect:/owner";
        }

        return "redirect:/owner/coupons";
    }

    // Handles enabling/disabling coupon
    @PostMapping("/owner/coupons/toggle/{id}")
    public String toggle(@PathVariable String id, HttpSession s) {

        // Authorization checking
        if (requireOwner(s) == null) return "redirect:/login";

        // Fetching coupon by ID
        Coupon c = couponService.byId(id);

        // Null checking prevents NullPointerException
        if (c != null) {

            // Polymorphism may occur internally in service/repository layer
            // Toggling coupon enabled status
            c.setEnabled(!c.isEnabled());

            // Updating modified coupon
            couponService.update(c);
        }

        return "redirect:/owner/coupons";
    }

    // Handles coupon deletion request
    @PostMapping("/owner/coupons/delete/{id}")
    public String delete(@PathVariable String id, HttpSession s) {

        // Checking authorization
        if (requireOwner(s) == null) return "redirect:/login";

        // Calling service layer delete operation
        // Repository pattern handles database CRUD operations
        couponService.delete(id);

        return "redirect:/owner/coupons";
    }
}

