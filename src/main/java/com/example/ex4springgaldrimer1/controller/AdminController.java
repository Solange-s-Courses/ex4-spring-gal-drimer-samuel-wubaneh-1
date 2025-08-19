package com.example.ex4springgaldrimer1.controller;

import com.example.ex4springgaldrimer1.entity.*;
import com.example.ex4springgaldrimer1.enums.CommentStatus;
import com.example.ex4springgaldrimer1.enums.QuestionType;
import com.example.ex4springgaldrimer1.enums.Role;
import com.example.ex4springgaldrimer1.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private GameService gameService;

    // Admin Dashboard
    @GetMapping
    public String adminDashboard(Model model) {
        // Dashboard statistics
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("totalCustomers", userService.getTotalCustomers());
        model.addAttribute("totalProducts", productService.getTotalProducts());
        model.addAttribute("totalStores", storeService.getTotalStores());
        model.addAttribute("activeStores", storeService.getActiveStoresCount());
        model.addAttribute("totalGameQuestions", gameService.getTotalQuestions());
        model.addAttribute("totalGamesPlayed", gameService.getTotalGamesPlayed());

        // Recent activities
        model.addAttribute("recentGames", gameService.getRecentGames(7).stream().limit(5).toList());
        model.addAttribute("pendingProductReviews", productService.getPendingComments().stream().limit(5).toList());
        model.addAttribute("pendingStoreReviews", storeService.getPendingComments().stream().limit(5).toList());

        return "admin/dashboard";
    }

    // ==================== USER MANAGEMENT ====================

    @GetMapping("/users")
    public String manageUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              @RequestParam(value = "role", required = false) Role role,
                              @RequestParam(value = "search", required = false) String search,
                              Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("registrationDate").descending());

        List<User> users;
        if (role != null) {
            users = userService.getAllCustomers(); // Filter by role
        } else if (search != null && !search.trim().isEmpty()) {
            users = userService.searchCustomers(search);
        } else {
            users = userService.getAllUsers();
        }

        model.addAttribute("users", users);
        model.addAttribute("currentRole", role);
        model.addAttribute("currentSearch", search);
        model.addAttribute("totalCustomers", userService.getTotalCustomers());
        model.addAttribute("roles", Role.values());

        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getEnabled()) {
                userService.disableUser(id);
                redirectAttributes.addFlashAttribute("successMessage", "User " + user.getUsername() + " has been disabled.");
            } else {
                userService.enableUser(id);
                redirectAttributes.addFlashAttribute("successMessage", "User " + user.getUsername() + " has been enabled.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating user status: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User " + user.getUsername() + " has been deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting user: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    // ==================== PRODUCT MANAGEMENT ====================

    @GetMapping("/products")
    public String manageProducts(@RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                 @RequestParam(value = "category", required = false) String category,
                                 @RequestParam(value = "search", required = false) String search,
                                 Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Product> products = productService.getProductsWithFilters(search, category, null, null, null, null, pageable);

        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("currentCategory", category);
        model.addAttribute("currentSearch", search);

        return "admin/products";
    }

    @GetMapping("/products/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("brands", productService.getAllBrands());
        return "admin/product-form";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("brands", productService.getAllBrands());
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@Valid @ModelAttribute Product product,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("brands", productService.getAllBrands());
            return "admin/product-form";
        }

        try {
            // Check for barcode uniqueness if it's a new product or barcode changed
            if (product.getId() == null || productService.isBarcodeTaken(product.getBarcode(), product.getId())) {
                if (productService.isBarcodeTaken(product.getBarcode())) {
                    model.addAttribute("barcodeError", "Barcode already exists");
                    model.addAttribute("categories", productService.getAllCategories());
                    model.addAttribute("brands", productService.getAllBrands());
                    return "admin/product-form";
                }
            }

            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("successMessage",
                    product.getId() == null ? "Product created successfully!" : "Product updated successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving product: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(id);
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product '" + product.getName() + "' has been deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting product: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // ==================== STORE MANAGEMENT ====================

    @GetMapping("/stores")
    public String manageStores(@RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size,
                               @RequestParam(value = "city", required = false) String city,
                               @RequestParam(value = "state", required = false) String state,
                               @RequestParam(value = "search", required = false) String search,
                               Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("openingDate").descending());
        Page<Store> stores = storeService.getStoresWithFilters(search, city, state, null, null, null, null, pageable);

        model.addAttribute("stores", stores);
        model.addAttribute("cities", storeService.getAllActiveCities());
        model.addAttribute("states", storeService.getAllActiveStates());
        model.addAttribute("currentCity", city);
        model.addAttribute("currentState", state);
        model.addAttribute("currentSearch", search);

        return "admin/stores";
    }

    @GetMapping("/stores/add")
    public String addStoreForm(Model model) {
        model.addAttribute("store", new Store());
        model.addAttribute("states", storeService.getAllActiveStates());
        return "admin/store-form";
    }

    @GetMapping("/stores/edit/{id}")
    public String editStoreForm(@PathVariable Long id, Model model) {
        Store store = storeService.getStoreById(id);
        model.addAttribute("store", store);
        model.addAttribute("states", storeService.getAllActiveStates());
        return "admin/store-form";
    }

    @PostMapping("/stores/save")
    public String saveStore(@Valid @ModelAttribute Store store,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("states", storeService.getAllActiveStates());
            return "admin/store-form";
        }

        try {
            // Check for address uniqueness
            if (store.getId() == null || storeService.isAddressTaken(store.getAddress(), store.getId())) {
                if (storeService.isAddressTaken(store.getAddress())) {
                    model.addAttribute("addressError", "A store with this address already exists");
                    model.addAttribute("states", storeService.getAllActiveStates());
                    return "admin/store-form";
                }
            }

            storeService.saveStore(store);
            redirectAttributes.addFlashAttribute("successMessage",
                    store.getId() == null ? "Store created successfully!" : "Store updated successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving store: " + e.getMessage());
        }

        return "redirect:/admin/stores";
    }

    @PostMapping("/stores/{id}/toggle-status")
    public String toggleStoreStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Store store = storeService.getStoreById(id);

            if (store.getIsActive()) {
                storeService.deactivateStore(id);
                redirectAttributes.addFlashAttribute("successMessage", "Store '" + store.getName() + "' has been deactivated.");
            } else {
                storeService.activateStore(id);
                redirectAttributes.addFlashAttribute("successMessage", "Store '" + store.getName() + "' has been activated.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating store status: " + e.getMessage());
        }

        return "redirect:/admin/stores";
    }

    @PostMapping("/stores/{id}/delete")
    public String deleteStore(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Store store = storeService.getStoreById(id);
            storeService.deleteStore(id);
            redirectAttributes.addFlashAttribute("successMessage", "Store '" + store.getName() + "' has been deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting store: " + e.getMessage());
        }

        return "redirect:/admin/stores";
    }

    // ==================== GAME QUESTIONS MANAGEMENT ====================

    @GetMapping("/questions")
    public String manageQuestions(@RequestParam(value = "type", required = false) QuestionType type,
                                  @RequestParam(value = "active", required = false) Boolean active,
                                  Model model) {

        List<GameQuestion> questions;
        if (type != null) {
            questions = gameService.getQuestionsByType(type);
        } else if (active != null) {
            questions = gameService.getActiveQuestions();
        } else {
            questions = gameService.getAllQuestions();
        }

        model.addAttribute("questions", questions);
        model.addAttribute("questionTypes", QuestionType.values());
        model.addAttribute("currentType", type);
        model.addAttribute("currentActive", active);

        return "admin/questions";
    }

    @GetMapping("/questions/add")
    public String addQuestionForm(Model model) {
        model.addAttribute("question", new GameQuestion());
        model.addAttribute("questionTypes", QuestionType.values());
        return "admin/question-form";
    }

    @GetMapping("/questions/edit/{id}")
    public String editQuestionForm(@PathVariable Long id, Model model) {
        GameQuestion question = gameService.getQuestionById(id);
        model.addAttribute("question", question);
        model.addAttribute("questionTypes", QuestionType.values());
        return "admin/question-form";
    }

    @PostMapping("/questions/save")
    public String saveQuestion(@Valid @ModelAttribute GameQuestion question,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("questionTypes", QuestionType.values());
            return "admin/question-form";
        }

        try {
            gameService.saveQuestion(question);
            redirectAttributes.addFlashAttribute("successMessage",
                    question.getId() == null ? "Question created successfully!" : "Question updated successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving question: " + e.getMessage());
        }

        return "redirect:/admin/questions";
    }

    @PostMapping("/questions/{id}/toggle-status")
    public String toggleQuestionStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            GameQuestion question = gameService.getQuestionById(id);
            question.setIsActive(!question.getIsActive());
            gameService.saveQuestion(question);

            String status = question.getIsActive() ? "activated" : "deactivated";
            redirectAttributes.addFlashAttribute("successMessage", "Question has been " + status + ".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating question status: " + e.getMessage());
        }

        return "redirect:/admin/questions";
    }

    @PostMapping("/questions/{id}/delete")
    public String deleteQuestion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            gameService.deleteQuestion(id);
            redirectAttributes.addFlashAttribute("successMessage", "Question has been deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting question: " + e.getMessage());
        }

        return "redirect:/admin/questions";
    }

    // ==================== REVIEW MANAGEMENT ====================

    @GetMapping("/reviews")
    public String manageReviews(@RequestParam(value = "type", defaultValue = "product") String type,
                                @RequestParam(value = "status", required = false) CommentStatus status,
                                Model model) {

        if ("product".equals(type)) {
            List<ProductComment> comments;
            if (status != null) {
                comments = productService.getPendingComments();
            } else {
                comments = productService.getPendingComments(); // Get all pending by default
            }
            model.addAttribute("productComments", comments);
            model.addAttribute("activeTab", "product");
        } else {
            List<StoreComment> comments = storeService.getPendingComments();
            model.addAttribute("storeComments", comments);
            model.addAttribute("activeTab", "store");
        }

        model.addAttribute("commentStatuses", CommentStatus.values());
        model.addAttribute("currentStatus", status);

        return "admin/reviews";
    }

    @PostMapping("/reviews/product/{id}/approve")
    public String approveProductReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.approveComment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product review approved.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error approving review: " + e.getMessage());
        }
        return "redirect:/admin/reviews?type=product";
    }

    @PostMapping("/reviews/product/{id}/reject")
    public String rejectProductReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.rejectComment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product review rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting review: " + e.getMessage());
        }
        return "redirect:/admin/reviews?type=product";
    }

    @PostMapping("/reviews/store/{id}/approve")
    public String approveStoreReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            storeService.approveComment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Store review approved.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error approving review: " + e.getMessage());
        }
        return "redirect:/admin/reviews?type=store";
    }

    @PostMapping("/reviews/store/{id}/reject")
    public String rejectStoreReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            storeService.rejectComment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Store review rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting review: " + e.getMessage());
        }
        return "redirect:/admin/reviews?type=store";
    }


}