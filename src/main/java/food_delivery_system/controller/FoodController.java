package food_delivery_system.controller;

import food_delivery_system.model.Food;
import food_delivery_system.service.FoodService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/foods")   // ✅ base URL
public class FoodController {

    private final FoodService service;

    // ✅ Constructor Injection
    public FoodController(FoodService service) {
        this.service = service;
    }

    // ✅ FIX: handle /foods → redirect to /foods/all
    @GetMapping("")
    public String redirectToAll() {
        return "redirect:/foods/all";
    }

    // SHOW ADD PAGE
    @GetMapping("/add")
    public String showAddPage() {
        return "add-food";
    }

    // CREATE
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

    // READ ALL
    @GetMapping("/all")
    public String viewFoods(Model model) {
        model.addAttribute("foods", service.getAllFoods());
        return "view-foods";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String deleteFood(@PathVariable String id) {
        service.deleteFood(id);
        return "redirect:/foods/all";
    }

    // SHOW EDIT PAGE
    @GetMapping("/edit/{id}")
    public String editFood(@PathVariable String id, Model model) {
        model.addAttribute("food", service.getById(id));
        return "edit-food";
    }

    // UPDATE
    @PostMapping("/update")
    public String updateFood(@RequestParam String id,
                             @RequestParam String name,
                             @RequestParam double price,
                             @RequestParam String restaurantId) {

        Food f = new Food(id, name, price, restaurantId);
        service.updateFood(f);

        return "redirect:/foods/all";
    }
}