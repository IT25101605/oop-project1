package food_delivery_system.controller;

import food_delivery_system.model.Food;
import food_delivery_system.service.FoodService;
import food_delivery_system.service.RestaurantService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/foods")
public class FoodController {

    private final FoodService service;
    private final RestaurantService restaurantService;

    // CONSTRUCTOR INJECTION
    public FoodController(FoodService service,
                          RestaurantService restaurantService) {

        this.service = service;
        this.restaurantService = restaurantService;
    }

    // REDIRECT
    @GetMapping("")
    public String redirectToAll() {
        return "redirect:/foods/all";
    }

    // =========================================
    // SHOW ADD FOOD PAGE
    // =========================================
    @GetMapping("/add")
    public String showAddPage(HttpSession session,
                              Model model) {

        String email = (String) session.getAttribute("email");

        // show ONLY owner's restaurants
        model.addAttribute(
                "restaurants",
                restaurantService.getRestaurantsByOwner(email)
        );

        return "add-food";
    }

    // =========================================
    // CREATE FOOD
    // =========================================
    @PostMapping("/add")
    public String addFood(@RequestParam String name,
                          @RequestParam double price,
                          @RequestParam String restaurantId) {

        Food f = new Food(
                UUID.randomUUID().toString(),
                name,
                price,
                restaurantId
        );

        service.addFood(f);

        return "redirect:/foods/all";
    }

    // =========================================
    // VIEW ALL FOODS
    // =========================================
    @GetMapping("/all")
    public String viewFoods(Model model) {

        model.addAttribute("foods",
                service.getAllFoods());

        return "view-foods";
    }

    // =========================================
    // DELETE FOOD
    // =========================================
    @GetMapping("/delete/{id}")
    public String deleteFood(@PathVariable String id) {

        service.deleteFood(id);

        return "redirect:/foods/all";
    }

    // =========================================
    // SHOW EDIT PAGE
    // =========================================
    @GetMapping("/edit/{id}")
    public String editFood(@PathVariable String id,
                           Model model) {

        model.addAttribute(
                "food",
                service.getById(id)
        );

        return "edit-food";
    }

    // =========================================
    // UPDATE FOOD
    // =========================================
    @PostMapping("/update")
    public String updateFood(@RequestParam String id,
                             @RequestParam String name,
                             @RequestParam double price,
                             @RequestParam String restaurantId) {

        Food f = new Food(
                id,
                name,
                price,
                restaurantId
        );

        service.updateFood(f);

        return "redirect:/foods/all";
    }

    // CUSTOMER VIEW
    @GetMapping("/customer/restaurants")
    public String customerRestaurantFoods(Model model) {

        model.addAttribute(
                "restaurants",
                restaurantService.getAllRestaurants()
        );

        model.addAttribute(
                "foods",
                service.getAllFoods()
        );

        return "customer-restaurants";
    }
}