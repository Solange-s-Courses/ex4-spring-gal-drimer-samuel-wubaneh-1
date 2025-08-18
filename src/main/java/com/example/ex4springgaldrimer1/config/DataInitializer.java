package com.example.ex4springgaldrimer1.config;

import com.example.ex4springgaldrimer1.entity.Product;
import com.example.ex4springgaldrimer1.entity.User;
import com.example.ex4springgaldrimer1.enums.Role;
import com.example.ex4springgaldrimer1.repository.ProductRepository;
import com.example.ex4springgaldrimer1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize database with sample data if empty
        if (userRepository.count() == 0) {
            initializeUsers();
        }

        if (productRepository.count() == 0) {
            initializeProducts();
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

    private void initializeProducts() {
        // Electronics Category
        Product laptop = new Product();
        laptop.setName("Dell XPS 13 Laptop");
        laptop.setBarcode("DELL001");
        laptop.setPrice(new BigDecimal("999.99"));
        laptop.setDescription("13-inch ultrabook with Intel Core i7 processor, 16GB RAM, and 512GB SSD. Perfect for work and entertainment.");
        laptop.setCategory("Electronics");
        laptop.setBrand("Dell");
        laptop.setStockQuantity(25);
        laptop.setWeight(1.2);
        productRepository.save(laptop);

        Product smartphone = new Product();
        smartphone.setName("Samsung Galaxy S23");
        smartphone.setBarcode("SAMSG23");
        smartphone.setPrice(new BigDecimal("699.99"));
        smartphone.setDescription("Latest smartphone with 108MP camera, 5G connectivity, and all-day battery life.");
        smartphone.setCategory("Electronics");
        smartphone.setBrand("Samsung");
        smartphone.setStockQuantity(40);
        smartphone.setWeight(0.2);
        productRepository.save(smartphone);

        Product headphones = new Product();
        headphones.setName("Sony WH-1000XM4 Headphones");
        headphones.setBarcode("SONY001");
        headphones.setPrice(new BigDecimal("279.99"));
        headphones.setDescription("Premium noise-canceling wireless headphones with 30-hour battery life.");
        headphones.setCategory("Electronics");
        headphones.setBrand("Sony");
        headphones.setStockQuantity(30);
        headphones.setWeight(0.25);
        productRepository.save(headphones);

        // Home & Garden Category
        Product coffeemaker = new Product();
        coffeemaker.setName("Keurig K-Elite Coffee Maker");
        coffeemaker.setBarcode("KEUR001");
        coffeemaker.setPrice(new BigDecimal("129.99"));
        coffeemaker.setDescription("Single-serve K-cup coffee maker with strong brew and iced coffee settings.");
        coffeemaker.setCategory("Home & Garden");
        coffeemaker.setBrand("Keurig");
        coffeemaker.setStockQuantity(20);
        coffeemaker.setWeight(3.5);
        productRepository.save(coffeemaker);

        Product vacuum = new Product();
        vacuum.setName("Dyson V15 Detect Vacuum");
        vacuum.setBarcode("DYSON15");
        vacuum.setPrice(new BigDecimal("449.99"));
        vacuum.setDescription("Powerful cordless vacuum with laser dust detection and intelligent suction.");
        vacuum.setCategory("Home & Garden");
        vacuum.setBrand("Dyson");
        vacuum.setStockQuantity(15);
        vacuum.setWeight(2.8);
        productRepository.save(vacuum);

        // Clothing Category
        Product jeans = new Product();
        jeans.setName("Levi's 501 Original Jeans");
        jeans.setBarcode("LEVI501");
        jeans.setPrice(new BigDecimal("59.99"));
        jeans.setDescription("Classic straight-fit jeans made from premium denim. Available in multiple washes.");
        jeans.setCategory("Clothing");
        jeans.setBrand("Levi's");
        jeans.setStockQuantity(50);
        jeans.setWeight(0.6);
        productRepository.save(jeans);

        Product jacket = new Product();
        jacket.setName("North Face Puffer Jacket");
        jacket.setBarcode("TNF001");
        jacket.setPrice(new BigDecimal("199.99"));
        jacket.setDescription("Warm, lightweight puffer jacket perfect for cold weather. Water-resistant and packable.");
        jacket.setCategory("Clothing");
        jacket.setBrand("The North Face");
        jacket.setStockQuantity(35);
        jacket.setWeight(0.8);
        productRepository.save(jacket);

        // Sports & Outdoors Category
        Product bike = new Product();
        bike.setName("Trek Mountain Bike");
        bike.setBarcode("TREK001");
        bike.setPrice(new BigDecimal("599.99"));
        bike.setDescription("21-speed mountain bike with aluminum frame and front suspension. Perfect for trails.");
        bike.setCategory("Sports & Outdoors");
        bike.setBrand("Trek");
        bike.setStockQuantity(12);
        bike.setWeight(13.5);
        productRepository.save(bike);

        Product tent = new Product();
        tent.setName("Coleman 4-Person Tent");
        tent.setBarcode("COLE001");
        tent.setPrice(new BigDecimal("89.99"));
        tent.setDescription("Easy-to-setup family tent with weather protection and spacious interior.");
        tent.setCategory("Sports & Outdoors");
        tent.setBrand("Coleman");
        tent.setStockQuantity(18);
        tent.setWeight(5.2);
        productRepository.save(tent);

        // Books Category
        Product book1 = new Product();
        book1.setName("The Psychology of Programming");
        book1.setBarcode("BOOK001");
        book1.setPrice(new BigDecimal("24.99"));
        book1.setDescription("Essential reading for software developers. Understand the human factors in programming.");
        book1.setCategory("Books");
        book1.setBrand("Tech Publications");
        book1.setStockQuantity(25);
        book1.setWeight(0.4);
        productRepository.save(book1);

        Product book2 = new Product();
        book2.setName("Clean Code: A Handbook");
        book2.setBarcode("BOOK002");
        book2.setPrice(new BigDecimal("34.99"));
        book2.setDescription("Learn how to write clean, maintainable code that other developers will thank you for.");
        book2.setCategory("Books");
        book2.setBrand("Programming Press");
        book2.setStockQuantity(30);
        book2.setWeight(0.5);
        productRepository.save(book2);

        System.out.println("Sample products created:");
        System.out.println("- " + productRepository.count() + " products in " + productRepository.findAllCategories().size() + " categories");
        System.out.println("- Categories: " + String.join(", ", productRepository.findAllCategories()));
    }
}