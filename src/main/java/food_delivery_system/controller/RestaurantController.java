package food_delivery_system.controller;

import food_delivery_system.model.Restaurant;
import food_delivery_system.service.RestaurantService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/restaurant")
public class RestaurantController {

    private final RestaurantService service = new RestaurantService();

    // SHOW ADD PAGE
    @GetMapping("/add")
    public String showAddPage() {
        return "add-restaurant";
    }

    // CREATE
    // OWNER EMAIL
    @PostMapping("/add")
    public String addRestaurant(@RequestParam String name,
                                @RequestParam String location,
                                HttpSession session) {

        String ownerEmail =
                (String) session.getAttribute("email");

        Restaurant r = new Restaurant(
                UUID.randomUUID().toString(),
                name,
                location,
                ownerEmail
        );

        service.addRestaurant(r);

        return "redirect:/restaurant/my";
    }

    // READ ALL RESTAURANTS
    // CUSTOMER VIEW
    @GetMapping("/all")
    public String viewRestaurants(Model model) {

        model.addAttribute(
                "restaurants",
                service.getAllRestaurants()
        );

        return "view-restaurants";
    }

    // OWNER RESTAURANTS ONLY
    @GetMapping("/my")
    public String myRestaurants(HttpSession session,
                                Model model) {

        String ownerEmail =
                (String) session.getAttribute("email");

        model.addAttribute(
                "restaurants",
                service.getRestaurantsByOwner(ownerEmail)
        );

        return "owner-restaurants";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String deleteRestaurant(@PathVariable String id) {

        service.deleteRestaurant(id);

        return "redirect:/restaurant/my";
    }


    // SHOW EDIT PAGE
    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable String id,
                           Model model) {

        model.addAttribute(
                "restaurant",
                service.getById(id)
        );

        return "edit-restaurant";
    }

    // UPDATE
    @PostMapping("/update")
    public String updateRestaurant(@RequestParam String id,
                                   @RequestParam String name,
                                   @RequestParam String location,
                                   HttpSession session) {

        String ownerEmail =
                (String) session.getAttribute("email");

        Restaurant r = new Restaurant(
                id,
                name,
                location,
                ownerEmail
        );

        service.updateRestaurant(r);

        return "redirect:/restaurant/my";
    }
}