/*package food_delivery_system.service;

import food_delivery_system.repository.UserRepository;
import java.util.*;

// OOP: Abstraction (service hides file logic)

public class CustomerService {

    private final UserRepository repo = new UserRepository();

    // CREATE
    public void register(String line) {
        repo.save(line);
    }

    // READ
    public List<String> getAll() {
        return repo.findAll();
    }

    // UPDATE
    public void update(List<String> updated) {
        repo.update(updated);
    }
}*/