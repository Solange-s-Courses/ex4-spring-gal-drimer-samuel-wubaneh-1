package com.example.ex4springgaldrimer1.config;

import com.example.ex4springgaldrimer1.entity.User;
import com.example.ex4springgaldrimer1.enums.Role;
import com.example.ex4springgaldrimer1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize database with sample data if empty
        if (userRepository.count() == 0) {
            initializeUsers();
        }
    }

    private void initializeUsers() {
        // Create admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@chainstore.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        // Create sample customer users
        User customer1 = new User();
        customer1.setUsername("john_doe");
        customer1.setEmail("john@example.com");
        customer1.setPassword(passwordEncoder.encode("password123"));
        customer1.setRole(Role.CUSTOMER);
        customer1.setCustomerPoints(150);
        customer1.setHighestGameScore(85);
        userRepository.save(customer1);

        User customer2 = new User();
        customer2.setUsername("jane_smith");
        customer2.setEmail("jane@example.com");
        customer2.setPassword(passwordEncoder.encode("password123"));
        customer2.setRole(Role.CUSTOMER);
        customer2.setCustomerPoints(230);
        customer2.setHighestGameScore(120);
        userRepository.save(customer2);

        User customer3 = new User();
        customer3.setUsername("mike_wilson");
        customer3.setEmail("mike@example.com");
        customer3.setPassword(passwordEncoder.encode("password123"));
        customer3.setRole(Role.CUSTOMER);
        customer3.setCustomerPoints(95);
        customer3.setHighestGameScore(67);
        userRepository.save(customer3);

        System.out.println("Sample users created:");
        System.out.println("Admin: admin / admin123");
        System.out.println("Customer: john_doe / password123");
        System.out.println("Customer: jane_smith / password123");
        System.out.println("Customer: mike_wilson / password123");
    }
}