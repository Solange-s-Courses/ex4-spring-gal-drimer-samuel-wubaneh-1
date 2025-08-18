package com.example.ex4springgaldrimer1.controller;

import com.example.ex4springgaldrimer1.entity.Product;
import com.example.ex4springgaldrimer1.entity.ProductComment;
import com.example.ex4springgaldrimer1.entity.User;
import com.example.ex4springgaldrimer1.service.ProductService;
import com.example.ex4springgaldrimer1.service.UserService;
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
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    // Product Catalog - Main product listing page
    @GetMapping
    public String productCatalog(@RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "12") int size,
                                 @RequestParam(value = "sort", defaultValue = "name") String sort,
                                 @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                 @RequestParam(value = "category", required = false) String category,
                                 @RequestParam(value = "brand", required = false) String brand,
                                 @RequestParam(value = "search", required = false) String search,
                                 @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
                                 @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
                                 @RequestParam(value = "inStock", required = false) Boolean inStock,
                                 Model model) {

        // Create pageable with sorting
        Sort.Direction sortDirection = direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        // Get products with filters
        Page<Product> products;
        if (search != null && !search.trim().isEmpty()) {
            // If searching, use search functionality
            List<Product> searchResults = productService.searchProducts(search);
            products = new org.springframework.data.domain.PageImpl<>(searchResults, pageable, searchResults.size());
        } else {
            // Use advanced filtering
            products = productService.getProductsWithFilters(null, category, brand, minPrice, maxPrice, inStock, pageable);
        }

        // Get filter options
        List<String> categories = productService.getAllCategories();
        List<String> brands = productService.getAllBrands();

        // Add attributes to model
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("brands", brands);
        model.addAttribute("currentCategory", category);
        model.addAttribute("currentBrand", brand);
        model.addAttribute("currentSearch", search);
        model.addAttribute("currentMinPrice", minPrice);
        model.addAttribute("currentMaxPrice", maxPrice);
        model.addAttribute("currentInStock", inStock);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentDirection", direction);

        // Add statistics
        model.addAttribute("totalProducts", productService.getTotalProducts());
        model.addAttribute("productsInStock", productService.getProductsInStockCount());

        return "products/catalog";
    }

    // Product Details Page
    @GetMapping("/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        try {
            Product product = productService.getProductById(id);
            List<ProductComment> comments = productService.getApprovedComments(id);

            // Calculate rating statistics
            double averageRating = productService.getProductAverageRating(id);
            long reviewCount = productService.getProductReviewCount(id);

            // Get current user for review form
            User currentUser = userService.getCurrentUser();

            model.addAttribute("product", product);
            model.addAttribute("comments", comments);
            model.addAttribute("averageRating", averageRating);
            model.addAttribute("reviewCount", reviewCount);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("newComment", new ProductComment());

            return "products/details";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Product not found");
            return "redirect:/products";
        }
    }

    // Add Product Review (Customer only)
    @PostMapping("/{id}/review")
    public String addReview(@PathVariable Long id,
                            @Valid @ModelAttribute("newComment") ProductComment comment,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            // Reload product details with errors
            Product product = productService.getProductById(id);
            List<ProductComment> comments = productService.getApprovedComments(id);
            double averageRating = productService.getProductAverageRating(id);
            long reviewCount = productService.getProductReviewCount(id);

            model.addAttribute("product", product);
            model.addAttribute("comments", comments);
            model.addAttribute("averageRating", averageRating);
            model.addAttribute("reviewCount", reviewCount);
            model.addAttribute("currentUser", currentUser);

            return "products/details";
        }

        try {
            productService.addComment(id, currentUser, comment.getTitle(), comment.getContent(), comment.getRating());
            redirectAttributes.addFlashAttribute("reviewMessage",
                    "Thank you for your review! It will be visible after approval.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to submit review: " + e.getMessage());
        }

        return "redirect:/products/" + id;
    }

    // Search Products (AJAX endpoint)
    @GetMapping("/search")
    public String searchProducts(@RequestParam("q") String query, Model model) {
        List<Product> products = productService.searchProducts(query);
        List<String> categories = productService.getAllCategories();
        List<String> brands = productService.getAllBrands();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("brands", brands);
        model.addAttribute("currentSearch", query);
        model.addAttribute("searchResults", true);

        return "products/catalog";
    }

    // Products by Category
    @GetMapping("/category/{category}")
    public String productsByCategory(@PathVariable String category, Model model) {
        List<Product> products = productService.getProductsByCategory(category);
        List<String> categories = productService.getAllCategories();
        List<String> brands = productService.getAllBrands();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("brands", brands);
        model.addAttribute("currentCategory", category);
        model.addAttribute("categoryResults", true);

        return "products/catalog";
    }

    // Featured Products (for home page)
    @GetMapping("/featured")
    public String featuredProducts(Model model) {
        List<Product> featuredProducts = productService.getFeaturedProducts();
        List<Product> newestProducts = productService.getNewestProducts(4);
        List<Product> mostReviewedProducts = productService.getMostReviewedProducts(4);

        model.addAttribute("featuredProducts", featuredProducts);
        model.addAttribute("newestProducts", newestProducts);
        model.addAttribute("mostReviewedProducts", mostReviewedProducts);

        return "products/featured";
    }
}