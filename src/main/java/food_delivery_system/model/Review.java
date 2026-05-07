package food_delivery_system.model;

// Encapsulation
public class Review {

    private String reviewId;
    private String customerEmail;
    private String message;
    private int rating;

    public Review() {}

    public Review(String reviewId, String customerEmail, String message, int rating) {
        this.reviewId = reviewId;
        this.customerEmail = customerEmail;
        this.message = message;
        this.rating = rating;
    }

    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
}