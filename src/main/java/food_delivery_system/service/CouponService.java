package food_delivery_system.service;

import food_delivery_system.model.Coupon;
import food_delivery_system.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// @Service annotation marks this class as Service layer
// Service layer contains business logic
// MVC Architecture:
// Service layer works between Controller and Repository
@Service
public class CouponService {

    // Dependency Injection using @Autowired
    // Repository object automatically injected by Spring
    @Autowired
    private CouponRepository repo;

    // Retrieves all coupons
    public List<Coupon> all() {

        // Delegating database/file operation to Repository layer
        return repo.findAll();
    }

    // Retrieves coupons belonging to a restaurant
    // Aggregation relationship:
    // One restaurant can contain multiple coupons
    public List<Coupon> byRestaurant(String rid) {

        return repo.findByRestaurant(rid);
    }

    // Retrieves coupon by ID
    public Coupon byId(String id) {

        return repo.findById(id);
    }

    // Retrieves coupon using code and restaurant ID
    public Coupon byCode(String code, String restaurantId) {

        return repo.findByCodeAndRestaurant(code, restaurantId);
    }

    // Saves new coupon
    public Coupon save(Coupon c) {

        return repo.save(c);
    }

    // Updates coupon details
    public void update(Coupon c) {

        repo.update(c);
    }

    // Deletes coupon using ID
    public void delete(String id) {

        repo.delete(id);
    }

    /**
     * Static inner class used for storing coupon result details
     * Abstraction:
     * Encapsulates coupon validation result into one object
     */
    public static class CouponResult {

        // final variables cannot be changed after initialization
        // Immutable object design

        // Indicates whether coupon is valid
        public final boolean ok;

        // Discount amount
        public final double discount;

        // Coupon code
        public final String code;

        // Result message
        public final String message;

        // Constructor for initializing result object
        public CouponResult(boolean ok,
                            double discount,
                            String code,
                            String message) {

            this.ok = ok;

            this.discount = discount;

            // Null checking prevents errors
            this.code = code == null ? "" : code;

            this.message = message;
        }
    }

    /**
     * Validate and compute discount for coupon code
     * Business logic method
     */
    public CouponResult apply(String code,
                              String restaurantId,
                              double subtotal) {

        // Validation for empty coupon code
        if (code == null || code.isBlank())

            return new CouponResult(true, 0, "", "");

        // Fetching coupon from repository
        Coupon c = repo.findByCodeAndRestaurant(code, restaurantId);

        // Invalid coupon validation
        if (c == null)

            return new CouponResult(
                    false,
                    0,
                    code,
                    "Invalid coupon code"
            );

        // Checking whether coupon is enabled
        if (!c.isEnabled())

            return new CouponResult(
                    false,
                    0,
                    code,
                    "Coupon is disabled"
            );

        // Checking whether coupon has expired
        if (c.isExpired())

            return new CouponResult(
                    false,
                    0,
                    code,
                    "Coupon has expired"
            );

        // Minimum order validation
        if (subtotal < c.getMinOrder())

            return new CouponResult(
                    false,
                    0,
                    code,

                    // Dynamic message generation
                    "Minimum order Rs. "
                            + c.getMinOrder()
                            + " required for this coupon"
            );

        // Returning successful coupon result
        return new CouponResult(

                true,

                // Calculating discount amount
                c.computeDiscount(subtotal),

                c.getCode(),

                "Coupon applied"
        );
    }
}

