package food_delivery_system.service;

import food_delivery_system.model.Food;
import food_delivery_system.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// @Service annotation marks this class as Service Layer in MVC architecture
// Service layer contains business logic and acts as a bridge between Controller and Repository
@Service
public class FoodService {
    // Dependency Injection using Spring (@Autowired)
    // Service depends on Repository for data access
    @Autowired private FoodRepository repo;

    // Returns all food items
    public List<Food> all() {
        return repo.findAll();
    }

    public Food byId(String id) {
        return repo.findById(id);
    }

    public List<Food> byRestaurant(String rid) {
        return repo.findByRestaurant(rid);
    }

    public List<Food> search(String q) {
        return repo.search(q);
    }

    public Food add(Food f) {
        return repo.save(f);
    }

    public void update(Food f) {
        repo.update(f);
    }

    public void delete(String id) {
        repo.delete(id);
    }
}