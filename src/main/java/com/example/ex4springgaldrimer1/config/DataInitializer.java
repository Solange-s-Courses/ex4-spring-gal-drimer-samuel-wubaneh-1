package com.example.ex4springgaldrimer1.config;

import com.example.ex4springgaldrimer1.entity.GameQuestion;
import com.example.ex4springgaldrimer1.entity.Product;
import com.example.ex4springgaldrimer1.entity.Store;
import com.example.ex4springgaldrimer1.entity.User;
import com.example.ex4springgaldrimer1.enums.QuestionType;
import com.example.ex4springgaldrimer1.enums.Role;
import com.example.ex4springgaldrimer1.repository.GameQuestionRepository;
import com.example.ex4springgaldrimer1.repository.ProductRepository;
import com.example.ex4springgaldrimer1.repository.StoreRepository;
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
    private StoreRepository storeRepository;

    @Autowired
    private GameQuestionRepository gameQuestionRepository;

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

        if (storeRepository.count() == 0) {
            initializeStores();
        }

        if (gameQuestionRepository.count() == 0) {
            initializeGameQuestions();
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
        book1.setBrand("Programming Press");
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

    private void initializeStores() {
        // Store 1 - New York
        Store store1 = new Store();
        store1.setName("Chain Store Manhattan");
        store1.setAddress("123 Broadway Street");
        store1.setCity("New York");
        store1.setState("NY");
        store1.setZipCode("10001");
        store1.setPhone("(212) 555-0101");
        store1.setEmail("manhattan@chainstore.com");
        store1.setWorkingHours("8:00 AM - 10:00 PM");
        store1.setWorkingDays("Monday - Sunday");
        store1.setStoreManager("Alice Johnson");
        store1.setDescription("Our flagship Manhattan location offering the complete Chain Store experience with the latest products and excellent customer service.");
        store1.setStoreSizeSqft(15000);
        store1.setParkingAvailable(false); // Manhattan - no parking
        store1.setDriveThrough(false);
        storeRepository.save(store1);

        // Store 2 - Los Angeles
        Store store2 = new Store();
        store2.setName("Chain Store Beverly Hills");
        store2.setAddress("456 Rodeo Drive");
        store2.setCity("Los Angeles");
        store2.setState("CA");
        store2.setZipCode("90210");
        store2.setPhone("(310) 555-0202");
        store2.setEmail("beverlyhills@chainstore.com");
        store2.setWorkingHours("9:00 AM - 9:00 PM");
        store2.setWorkingDays("Monday - Saturday");
        store2.setStoreManager("Carlos Rodriguez");
        store2.setDescription("Premium location in Beverly Hills featuring luxury products and personalized shopping experience.");
        store2.setStoreSizeSqft(12000);
        store2.setParkingAvailable(true);
        store2.setDriveThrough(false);
        storeRepository.save(store2);

        // Store 3 - Chicago
        Store store3 = new Store();
        store3.setName("Chain Store Downtown Chicago");
        store3.setAddress("789 Michigan Avenue");
        store3.setCity("Chicago");
        store3.setState("IL");
        store3.setZipCode("60611");
        store3.setPhone("(312) 555-0303");
        store3.setEmail("chicago@chainstore.com");
        store3.setWorkingHours("7:00 AM - 11:00 PM");
        store3.setWorkingDays("Monday - Sunday");
        store3.setStoreManager("Sarah Williams");
        store3.setDescription("Convenient downtown location perfect for busy professionals and tourists visiting the Magnificent Mile.");
        store3.setStoreSizeSqft(18000);
        store3.setParkingAvailable(true);
        store3.setDriveThrough(true);
        storeRepository.save(store3);

        // Store 4 - Miami
        Store store4 = new Store();
        store4.setName("Chain Store South Beach");
        store4.setAddress("321 Ocean Drive");
        store4.setCity("Miami");
        store4.setState("FL");
        store4.setZipCode("33139");
        store4.setPhone("(305) 555-0404");
        store4.setEmail("miami@chainstore.com");
        store4.setWorkingHours("8:00 AM - 12:00 AM");
        store4.setWorkingDays("Monday - Sunday");
        store4.setStoreManager("Miguel Santos");
        store4.setDescription("Vibrant South Beach location offering beach essentials, electronics, and summer collections.");
        store4.setStoreSizeSqft(10000);
        store4.setParkingAvailable(true);
        store4.setDriveThrough(false);
        storeRepository.save(store4);

        // Store 5 - Dallas
        Store store5 = new Store();
        store5.setName("Chain Store Dallas Central");
        store5.setAddress("654 Main Street");
        store5.setCity("Dallas");
        store5.setState("TX");
        store5.setZipCode("75201");
        store5.setPhone("(214) 555-0505");
        store5.setEmail("dallas@chainstore.com");
        store5.setWorkingHours("6:00 AM - 10:00 PM");
        store5.setWorkingDays("Monday - Sunday");
        store5.setStoreManager("Jennifer Davis");
        store5.setDescription("Large family-friendly store serving the Dallas metroplex with extensive product selection and ample parking.");
        store5.setStoreSizeSqft(22000);
        store5.setParkingAvailable(true);
        store5.setDriveThrough(true);
        storeRepository.save(store5);

        // Store 6 - Seattle
        Store store6 = new Store();
        store6.setName("Chain Store Pike Place");
        store6.setAddress("987 Pike Street");
        store6.setCity("Seattle");
        store6.setState("WA");
        store6.setZipCode("98101");
        store6.setPhone("(206) 555-0606");
        store6.setEmail("seattle@chainstore.com");
        store6.setWorkingHours("8:00 AM - 9:00 PM");
        store6.setWorkingDays("Monday - Sunday");
        store6.setStoreManager("David Chen");
        store6.setDescription("Located near famous Pike Place Market, specializing in local products and tech accessories.");
        store6.setStoreSizeSqft(8000);
        store6.setParkingAvailable(false); // Downtown Seattle
        store6.setDriveThrough(false);
        storeRepository.save(store6);

        // Store 7 - Denver
        Store store7 = new Store();
        store7.setName("Chain Store Denver Highlands");
        store7.setAddress("147 16th Street");
        store7.setCity("Denver");
        store7.setState("CO");
        store7.setZipCode("80202");
        store7.setPhone("(303) 555-0707");
        store7.setEmail("denver@chainstore.com");
        store7.setWorkingHours("7:00 AM - 10:00 PM");
        store7.setWorkingDays("Monday - Sunday");
        store7.setStoreManager("Amanda Taylor");
        store7.setDescription("Mountain lifestyle store featuring outdoor gear, sports equipment, and cold-weather essentials.");
        store7.setStoreSizeSqft(14000);
        store7.setParkingAvailable(true);
        store7.setDriveThrough(true);
        storeRepository.save(store7);

        // Store 8 - Atlanta (Temporarily Closed)
        Store store8 = new Store();
        store8.setName("Chain Store Buckhead");
        store8.setAddress("258 Peachtree Road");
        store8.setCity("Atlanta");
        store8.setState("GA");
        store8.setZipCode("30309");
        store8.setPhone("(404) 555-0808");
        store8.setEmail("atlanta@chainstore.com");
        store8.setWorkingHours("Temporarily Closed");
        store8.setWorkingDays("Closed for Renovation");
        store8.setStoreManager("Robert Brown");
        store8.setDescription("Currently undergoing major renovation. Expected to reopen with expanded layout and new features.");
        store8.setStoreSizeSqft(16000);
        store8.setParkingAvailable(true);
        store8.setDriveThrough(false);
        store8.setIsActive(false); // Temporarily closed
        storeRepository.save(store8);

        System.out.println("Sample stores created:");
        System.out.println("- " + storeRepository.count() + " stores in " + storeRepository.findAllActiveCities().size() + " cities");
        System.out.println("- Active stores: " + storeRepository.countByIsActive(true));
        System.out.println("- Cities: " + String.join(", ", storeRepository.findAllActiveCities()));
    }

    private void initializeGameQuestions() {
        // Product Knowledge Questions
        GameQuestion q1 = new GameQuestion();
        q1.setQuestionText("Which brand offers the Dell XPS 13 Laptop in our store?");
        q1.setOptionA("HP");
        q1.setOptionB("Dell");
        q1.setOptionC("Apple");
        q1.setOptionD("Lenovo");
        q1.setCorrectAnswer("B");
        q1.setQuestionType(QuestionType.PRODUCT);
        q1.setPoints(10);
        q1.setDifficultyLevel(1);
        q1.setExplanation("The Dell XPS 13 is manufactured by Dell, known for their business and consumer laptops.");
        gameQuestionRepository.save(q1);

        GameQuestion q2 = new GameQuestion();
        q2.setQuestionText("What is the price range of the Samsung Galaxy S23 in our catalog?");
        q2.setOptionA("$499.99");
        q2.setOptionB("$599.99");
        q2.setOptionC("$699.99");
        q2.setOptionD("$799.99");
        q2.setCorrectAnswer("C");
        q2.setQuestionType(QuestionType.PRODUCT);
        q2.setPoints(15);
        q2.setDifficultyLevel(2);
        q2.setExplanation("The Samsung Galaxy S23 is priced at $699.99 in our current catalog.");
        gameQuestionRepository.save(q2);

        GameQuestion q3 = new GameQuestion();
        q3.setQuestionText("Which category does the Dyson V15 Detect belong to?");
        q3.setOptionA("Electronics");
        q3.setOptionB("Home & Garden");
        q3.setOptionC("Sports & Outdoors");
        q3.setOptionD("Clothing");
        q3.setCorrectAnswer("B");
        q3.setQuestionType(QuestionType.PRODUCT);
        q3.setPoints(10);
        q3.setDifficultyLevel(1);
        q3.setExplanation("The Dyson V15 Detect is a vacuum cleaner, which falls under the Home & Garden category.");
        gameQuestionRepository.save(q3);

        GameQuestion q4 = new GameQuestion();
        q4.setQuestionText("What is the weight of the Trek Mountain Bike?");
        q4.setOptionA("10.5 kg");
        q4.setOptionB("12.0 kg");
        q4.setOptionC("13.5 kg");
        q4.setOptionD("15.0 kg");
        q4.setCorrectAnswer("C");
        q4.setQuestionType(QuestionType.PRODUCT);
        q4.setPoints(20);
        q4.setDifficultyLevel(3);
        q4.setExplanation("The Trek Mountain Bike weighs 13.5 kg, making it a sturdy yet manageable mountain bike.");
        gameQuestionRepository.save(q4);

        // Store Information Questions
        GameQuestion q5 = new GameQuestion();
        q5.setQuestionText("Which Chain Store location is our flagship Manhattan store?");
        q5.setOptionA("456 Rodeo Drive");
        q5.setOptionB("123 Broadway Street");
        q5.setOptionC("789 Michigan Avenue");
        q5.setOptionD("321 Ocean Drive");
        q5.setCorrectAnswer("B");
        q5.setQuestionType(QuestionType.STORE);
        q5.setPoints(10);
        q5.setDifficultyLevel(1);
        q5.setExplanation("Our flagship Manhattan store is located at 123 Broadway Street in New York.");
        gameQuestionRepository.save(q5);

        GameQuestion q6 = new GameQuestion();
        q6.setQuestionText("Which store manager runs the Beverly Hills location?");
        q6.setOptionA("Alice Johnson");
        q6.setOptionB("Carlos Rodriguez");
        q6.setOptionC("Sarah Williams");
        q6.setOptionD("Miguel Santos");
        q6.setCorrectAnswer("B");
        q6.setQuestionType(QuestionType.STORE);
        q6.setPoints(15);
        q6.setDifficultyLevel(2);
        q6.setExplanation("Carlos Rodriguez is the store manager at our Beverly Hills location on Rodeo Drive.");
        gameQuestionRepository.save(q6);

        GameQuestion q7 = new GameQuestion();
        q7.setQuestionText("Which Chain Store location has both parking and drive-through services?");
        q7.setOptionA("Manhattan");
        q7.setOptionB("Beverly Hills");
        q7.setOptionC("Downtown Chicago");
        q7.setOptionD("Pike Place Seattle");
        q7.setCorrectAnswer("C");
        q7.setQuestionType(QuestionType.STORE);
        q7.setPoints(15);
        q7.setDifficultyLevel(2);
        q7.setExplanation("Our Downtown Chicago store offers both parking and drive-through convenience for customers.");
        gameQuestionRepository.save(q7);

        GameQuestion q8 = new GameQuestion();
        q8.setQuestionText("What is the store size of our Dallas Central location?");
        q8.setOptionA("15,000 sq ft");
        q8.setOptionB("18,000 sq ft");
        q8.setOptionC("20,000 sq ft");
        q8.setOptionD("22,000 sq ft");
        q8.setCorrectAnswer("D");
        q8.setQuestionType(QuestionType.STORE);
        q8.setPoints(20);
        q8.setDifficultyLevel(3);
        q8.setExplanation("Dallas Central is our largest store at 22,000 square feet, serving the Dallas metroplex.");
        gameQuestionRepository.save(q8);

        GameQuestion q9 = new GameQuestion();
        q9.setQuestionText("Which store is currently closed for renovation?");
        q9.setOptionA("Manhattan");
        q9.setOptionB("Denver Highlands");
        q9.setOptionC("Buckhead Atlanta");
        q9.setOptionD("South Beach Miami");
        q9.setCorrectAnswer("C");
        q9.setQuestionType(QuestionType.STORE);
        q9.setPoints(10);
        q9.setDifficultyLevel(1);
        q9.setExplanation("Our Buckhead Atlanta location is temporarily closed for major renovation and expansion.");
        gameQuestionRepository.save(q9);

        // General Chain Store Knowledge
        GameQuestion q10 = new GameQuestion();
        q10.setQuestionText("How many customer loyalty points do you typically earn for completing a game?");
        q10.setOptionA("10 points");
        q10.setOptionB("15 points");
        q10.setOptionC("20+ points");
        q10.setOptionD("5 points");
        q10.setCorrectAnswer("C");
        q10.setQuestionType(QuestionType.GENERAL);
        q10.setPoints(10);
        q10.setDifficultyLevel(1);
        q10.setExplanation("You earn 20 base points for completing a game, plus accuracy bonuses!");
        gameQuestionRepository.save(q10);

        GameQuestion q11 = new GameQuestion();
        q11.setQuestionText("What technology stack does our website use?");
        q11.setOptionA("React + Node.js");
        q11.setOptionB("Spring Boot + Thymeleaf");
        q11.setOptionC("Django + Python");
        q11.setOptionD("Angular + Express");
        q11.setCorrectAnswer("B");
        q11.setQuestionType(QuestionType.GENERAL);
        q11.setPoints(15);
        q11.setDifficultyLevel(2);
        q11.setExplanation("Our website is built with Spring Boot MVC and Thymeleaf for server-side rendering.");
        gameQuestionRepository.save(q11);

        GameQuestion q12 = new GameQuestion();
        q12.setQuestionText("In how many cities do we currently have active stores?");
        q12.setOptionA("6 cities");
        q12.setOptionB("7 cities");
        q12.setOptionC("8 cities");
        q12.setOptionD("9 cities");
        q12.setCorrectAnswer("B");
        q12.setQuestionType(QuestionType.GENERAL);
        q12.setPoints(15);
        q12.setDifficultyLevel(2);
        q12.setExplanation("We have active stores in 7 cities: NYC, LA, Chicago, Miami, Dallas, Seattle, and Denver.");
        gameQuestionRepository.save(q12);

        GameQuestion q13 = new GameQuestion();
        q13.setQuestionText("What are the two main types of users in our system?");
        q13.setOptionA("Buyers and Sellers");
        q13.setOptionB("Members and Guests");
        q13.setOptionC("Customers and Admins");
        q13.setOptionD("Users and Managers");
        q13.setCorrectAnswer("C");
        q13.setQuestionType(QuestionType.GENERAL);
        q13.setPoints(10);
        q13.setDifficultyLevel(1);
        q13.setExplanation("Our system has two main user roles: Customers who shop and play games, and Admins who manage the system.");
        gameQuestionRepository.save(q13);

        GameQuestion q14 = new GameQuestion();
        q14.setQuestionText("Which feature allows customers to share their shopping experience?");
        q14.setOptionA("Shopping Cart");
        q14.setOptionB("Product Reviews");
        q14.setOptionC("Loyalty Game");
        q14.setOptionD("Store Locator");
        q14.setCorrectAnswer("B");
        q14.setQuestionType(QuestionType.GENERAL);
        q14.setPoints(10);
        q14.setDifficultyLevel(1);
        q14.setExplanation("Customers can write detailed product and store reviews to share their experiences with other shoppers.");
        gameQuestionRepository.save(q14);

        GameQuestion q15 = new GameQuestion();
        q15.setQuestionText("What is the minimum number of questions needed to start a loyalty game?");
        q15.setOptionA("3 questions");
        q15.setOptionB("5 questions");
        q15.setOptionC("10 questions");
        q15.setOptionD("15 questions");
        q15.setCorrectAnswer("B");
        q15.setQuestionType(QuestionType.GENERAL);
        q15.setPoints(20);
        q15.setDifficultyLevel(3);
        q15.setExplanation("The system requires at least 5 active questions in the database to allow customers to start a new game.");
        gameQuestionRepository.save(q15);

        // Additional questions for variety
        GameQuestion q16 = new GameQuestion();
        q16.setQuestionText("Which product category has the most expensive item in our catalog?");
        q16.setOptionA("Electronics");
        q16.setOptionB("Home & Garden");
        q16.setOptionC("Sports & Outdoors");
        q16.setOptionD("Clothing");
        q16.setCorrectAnswer("A");
        q16.setQuestionType(QuestionType.PRODUCT);
        q16.setPoints(15);
        q16.setDifficultyLevel(2);
        q16.setExplanation("Electronics category contains the Dell XPS 13 Laptop at $999.99, our most expensive single item.");
        gameQuestionRepository.save(q16);

        GameQuestion q17 = new GameQuestion();
        q17.setQuestionText("What time does our Manhattan store open?");
        q17.setOptionA("7:00 AM");
        q17.setOptionB("8:00 AM");
        q17.setOptionC("9:00 AM");
        q17.setOptionD("10:00 AM");
        q17.setCorrectAnswer("B");
        q17.setQuestionType(QuestionType.STORE);
        q17.setPoints(10);
        q17.setDifficultyLevel(1);
        q17.setExplanation("Our Manhattan flagship store opens at 8:00 AM to serve early commuters.");
        gameQuestionRepository.save(q17);

        GameQuestion q18 = new GameQuestion();
        q18.setQuestionText("What happens to your customer points when you write a product review?");
        q18.setOptionA("Nothing");
        q18.setOptionB("You lose 5 points");
        q18.setOptionC("You gain 10 points");
        q18.setOptionD("You gain 20 points");
        q18.setCorrectAnswer("C");
        q18.setQuestionType(QuestionType.GENERAL);
        q18.setPoints(15);
        q18.setDifficultyLevel(2);
        q18.setExplanation("Writing a product or store review earns you 10 customer loyalty points!");
        gameQuestionRepository.save(q18);

        GameQuestion q19 = new GameQuestion();
        q19.setQuestionText("Which store has the largest floor space?");
        q19.setOptionA("Manhattan");
        q19.setOptionB("Beverly Hills");
        q19.setOptionC("Downtown Chicago");
        q19.setOptionD("Dallas Central");
        q19.setCorrectAnswer("D");
        q19.setQuestionType(QuestionType.STORE);
        q19.setPoints(15);
        q19.setDifficultyLevel(2);
        q19.setExplanation("Dallas Central has 22,000 sq ft, making it our largest store location.");
        gameQuestionRepository.save(q19);

        GameQuestion q20 = new GameQuestion();
        q20.setQuestionText("What is the brand of our camping tent product?");
        q20.setOptionA("Coleman");
        q20.setOptionB("REI");
        q20.setOptionC("North Face");
        q20.setOptionD("Patagonia");
        q20.setCorrectAnswer("A");
        q20.setQuestionType(QuestionType.PRODUCT);
        q20.setPoints(10);
        q20.setDifficultyLevel(1);
        q20.setExplanation("We carry the Coleman 4-Person Tent in our Sports & Outdoors section.");
        gameQuestionRepository.save(q20);

        System.out.println("Sample game questions created:");
        System.out.println("- " + gameQuestionRepository.count() + " total questions");
        System.out.println("- " + gameQuestionRepository.countByQuestionTypeAndIsActive(QuestionType.PRODUCT, true) + " product questions");
        System.out.println("- " + gameQuestionRepository.countByQuestionTypeAndIsActive(QuestionType.STORE, true) + " store questions");
        System.out.println("- " + gameQuestionRepository.countByQuestionTypeAndIsActive(QuestionType.GENERAL, true) + " general questions");
    }
}