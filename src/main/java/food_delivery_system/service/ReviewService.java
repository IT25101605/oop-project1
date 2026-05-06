package food_delivery_system.service;

import food_delivery_system.model.Review;
import food_delivery_system.repository.ReviewRepository;

import java.util.*;

public class ReviewService {

    private final ReviewRepository repo = new ReviewRepository();

    // CREATE
    public void addReview(Review review) {

        String line = review.getReviewId() + "," +
                review.getCustomerEmail() + "," +
                review.getMessage() + "," +
                review.getRating();

        repo.save(line);
    }

    // READ
    public List<Review> getAllReviews() {

        List<Review> list = new ArrayList<>();

        for (String line : repo.findAll()) {
            String[] data = line.split(",");

            if (data.length == 4) {
                list.add(new Review(
                        data[0],
                        data[1],
                        data[2],
                        Integer.parseInt(data[3])
                ));
            }
        }
        return list;
    }

    // DELETE
    public void deleteReview(String id) {

        List<String> updated = new ArrayList<>();

        for (String line : repo.findAll()) {
            if (!line.startsWith(id + ",")) {
                updated.add(line);
            }
        }

        repo.overwrite(updated);
    }
}