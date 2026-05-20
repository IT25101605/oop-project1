package food_delivery_system.service;

import food_delivery_system.model.Restaurant;
import food_delivery_system.repository.FoodRepository;
import food_delivery_system.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// @Service annotation marks this class as Service layer
// Service layer handles business logic
// MVC Architecture:
// Service connects Controller layer and Repository layer
@Service
public class RestaurantService {

    // Dependency Injection using @Autowired
    // Spring automatically injects repository objects

    // Repository used for restaurant CRUD operations
    @Autowired
    private RestaurantRepository repo;

    // Repository used for food-related operations
    @Autowired
    private FoodRepository foodRepo;

    // Retrieves all restaurants
    public List<Restaurant> all() {

        // Delegating operation to Repository layer
        return repo.findAll();
    }

    // Retrieves restaurants belonging to an owner
    // Aggregation relationship:
    // One owner can manage multiple restaurants
    public List<Restaurant> byOwner(String ownerId) {

        return repo.findByOwner(ownerId);
    }

    // Checks whether owner already has a restaurant
    public boolean ownerHasRestaurant(String ownerId) {

        // isEmpty() checks whether list contains data
        return !byOwner(ownerId).isEmpty();
    }

    // Retrieves restaurant using ID
    public Restaurant byId(String id) {

        return repo.findById(id);
    }

    // Adds new restaurant
    public Restaurant add(Restaurant r) {

        // Saving restaurant through Repository layer
        return repo.save(r);
    }

    // Updates restaurant details
    public void update(Restaurant r) {

        repo.update(r);
    }

    // Deletes restaurant and related food items
    public void delete(String id) {

        // Composition/Aggregation relationship:
        // Foods belong to Restaurant

        // Deleting all foods linked with restaurant
        foodRepo.deleteByRestaurant(id);

        // Deleting restaurant
        repo.delete(id);
    }
}

