package food_delivery_system.controller;

import food_delivery_system.model.Review;
import food_delivery_system.service.ReviewService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService service = new ReviewService();

    // SHOW PAGE
    @GetMapping("/add")
    public String reviewPage() {
        return "add-review";
    }

    // CREATE
    @PostMapping("/add")
    public String addReview(@RequestParam String email,
                            @RequestParam String message,
                            @RequestParam int rating) {

        Review review = new Review(
                UUID.randomUUID().toString(),
                email,
                message,
                rating
        );

        service.addReview(review);

        return "redirect:/review/all";
    }

    // READ
    @GetMapping("/all")
    public String viewReviews(Model model) {

        model.addAttribute("reviews", service.getAllReviews());
        return "view-reviews";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String deleteReview(@PathVariable String id) {

        service.deleteReview(id);
        return "redirect:/review/all";
    }
}