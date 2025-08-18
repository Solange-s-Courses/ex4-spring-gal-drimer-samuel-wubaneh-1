package com.example.ex4springgaldrimer1.controller;

import com.example.ex4springgaldrimer1.entity.Store;
import com.example.ex4springgaldrimer1.entity.StoreComment;
import com.example.ex4springgaldrimer1.entity.User;
import com.example.ex4springgaldrimer1.service.StoreService;
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

import java.util.List;

@Controller
@RequestMapping("/stores")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private UserService userService;

    // Store Listing - Main stores page
    @GetMapping
    public String storeList(@RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "12") int size,
                            @RequestParam(value = "sort", defaultValue = "name") String sort,
                            @RequestParam(value = "direction", defaultValue = "asc") String direction,
                            @RequestParam(value = "city", required = false) String city,
                            @RequestParam(value = "state", required = false) String state,
                            @RequestParam(value = "zipCode", required = false) String zipCode,
                            @RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "parkingAvailable", required = false) Boolean parkingAvailable,
                            @RequestParam(value = "driveThrough", required = false) Boolean driveThrough,
                            @RequestParam(value = "isActive", required = false) Boolean isActive,
                            Model model) {

        // Create pageable with sorting
        Sort.Direction sortDirection = direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        // Get stores with filters
        Page<Store> stores;
        if (search != null && !search.trim().isEmpty()) {
            // If searching, use search functionality
            List<Store> searchResults = storeService.searchActiveStores(search);
            // Convert List to Page for consistency
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), searchResults.size());
            List<Store> pageContent = searchResults.subList(start, end);
            stores = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, searchResults.size());
        } else {
            // Use advanced filtering
            stores = storeService.getStoresWithFilters(null, city, state, zipCode, isActive, parkingAvailable, driveThrough, pageable);
        }

        // Get filter options
        List<String> cities = storeService.getAllActiveCities();
        List<String> states = storeService.getAllActiveStates();

        // Add attributes to model
        model.addAttribute("stores", stores);
        model.addAttribute("cities", cities);
        model.addAttribute("states", states);
        model.addAttribute("currentCity", city);
        model.addAttribute("currentState", state);
        model.addAttribute("currentZipCode", zipCode);
        model.addAttribute("currentSearch", search);
        model.addAttribute("currentParkingAvailable", parkingAvailable);
        model.addAttribute("currentDriveThrough", driveThrough);
        model.addAttribute("currentIsActive", isActive);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentDirection", direction);

        // Add statistics
        model.addAttribute("totalStores", storeService.getTotalStores());
        model.addAttribute("activeStores", storeService.getActiveStoresCount());
        model.addAttribute("storesWithParking", storeService.getStoresWithParkingCount());
        model.addAttribute("storesWithDriveThrough", storeService.getStoresWithDriveThoughCount());

        return "stores/list";
    }

    // Store Details Page
    @GetMapping("/{id}")
    public String storeDetails(@PathVariable Long id, Model model) {
        try {
            Store store = storeService.getStoreById(id);
            List<StoreComment> comments = storeService.getApprovedComments(id);

            // Calculate rating statistics
            double averageRating = storeService.getStoreAverageRating(id);
            long reviewCount = storeService.getStoreReviewCount(id);
            long recommendationCount = storeService.getStoreRecommendationCount(id);

            // Get detailed ratings
            double serviceRating = storeService.getStoreServiceRating(id);
            double cleanlinessRating = storeService.getStoreCleanlinessRating(id);
            double locationRating = storeService.getStoreLocationRating(id);

            // Get current user for review form
            User currentUser = userService.getCurrentUser();

            // Get stores in same city for recommendations
            List<Store> nearbyStores = storeService.getStoresByCity(store.getCity())
                    .stream()
                    .filter(s -> !s.getId().equals(id) && s.getIsActive())
                    .limit(3)
                    .toList();

            model.addAttribute("store", store);
            model.addAttribute("comments", comments);
            model.addAttribute("averageRating", averageRating);
            model.addAttribute("reviewCount", reviewCount);
            model.addAttribute("recommendationCount", recommendationCount);
            model.addAttribute("serviceRating", serviceRating);
            model.addAttribute("cleanlinessRating", cleanlinessRating);
            model.addAttribute("locationRating", locationRating);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("newComment", new StoreComment());
            model.addAttribute("nearbyStores", nearbyStores);

            return "stores/details";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Store not found");
            return "redirect:/stores";
        }
    }

    // Add Store Review (Customer only)
    @PostMapping("/{id}/review")
    public String addReview(@PathVariable Long id,
                            @Valid @ModelAttribute("newComment") StoreComment comment,
                            @RequestParam(value = "serviceRating", required = false) Integer serviceRating,
                            @RequestParam(value = "cleanlinessRating", required = false) Integer cleanlinessRating,
                            @RequestParam(value = "locationRating", required = false) Integer locationRating,
                            @RequestParam(value = "wouldRecommend", required = false) Boolean wouldRecommend,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            // Reload store details with errors
            Store store = storeService.getStoreById(id);
            List<StoreComment> comments = storeService.getApprovedComments(id);
            double averageRating = storeService.getStoreAverageRating(id);
            long reviewCount = storeService.getStoreReviewCount(id);

            model.addAttribute("store", store);
            model.addAttribute("comments", comments);
            model.addAttribute("averageRating", averageRating);
            model.addAttribute("reviewCount", reviewCount);
            model.addAttribute("currentUser", currentUser);

            return "stores/details";
        }

        try {
            // Add detailed comment with additional ratings
            storeService.addDetailedComment(id, currentUser, comment.getTitle(), comment.getContent(),
                    comment.getRating(), serviceRating, cleanlinessRating, locationRating, wouldRecommend);

            redirectAttributes.addFlashAttribute("reviewMessage",
                    "Thank you for your detailed review! It will be visible after approval.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to submit review: " + e.getMessage());
        }

        return "redirect:/stores/" + id;
    }

    // Search Stores (AJAX endpoint)
    @GetMapping("/search")
    public String searchStores(@RequestParam("q") String query, Model model) {
        List<Store> stores = storeService.searchActiveStores(query);
        List<String> cities = storeService.getAllActiveCities();
        List<String> states = storeService.getAllActiveStates();

        model.addAttribute("stores", stores);
        model.addAttribute("cities", cities);
        model.addAttribute("states", states);
        model.addAttribute("currentSearch", query);
        model.addAttribute("searchResults", true);

        return "stores/list";
    }

    // Stores by City
    @GetMapping("/city/{city}")
    public String storesByCity(@PathVariable String city, Model model) {
        List<Store> stores = storeService.getStoresByCity(city);
        List<String> cities = storeService.getAllActiveCities();
        List<String> states = storeService.getAllActiveStates();

        model.addAttribute("stores", stores);
        model.addAttribute("cities", cities);
        model.addAttribute("states", states);
        model.addAttribute("currentCity", city);
        model.addAttribute("cityResults", true);

        return "stores/list";
    }

    // Stores by State
    @GetMapping("/state/{state}")
    public String storesByState(@PathVariable String state, Model model) {
        List<Store> stores = storeService.getStoresByState(state);
        List<String> cities = storeService.getAllActiveCities();
        List<String> states = storeService.getAllActiveStates();

        model.addAttribute("stores", stores);
        model.addAttribute("cities", cities);
        model.addAttribute("states", states);
        model.addAttribute("currentState", state);
        model.addAttribute("stateResults", true);

        return "stores/list";
    }

    // Featured Stores (for home page)
    @GetMapping("/featured")
    public String featuredStores(Model model) {
        List<Store> featuredStores = storeService.getFeaturedStores();
        List<Store> topRatedStores = storeService.getTopRatedStores(4);
        List<Store> recentStores = storeService.getRecentlyOpenedStores(4);

        model.addAttribute("featuredStores", featuredStores);
        model.addAttribute("topRatedStores", topRatedStores);
        model.addAttribute("recentStores", recentStores);

        return "stores/featured";
    }

    // Stores with specific features
    @GetMapping("/with-parking")
    public String storesWithParking(Model model) {
        List<Store> stores = storeService.getStoresWithParking();
        model.addAttribute("stores", stores);
        model.addAttribute("filterTitle", "Stores with Parking");
        return "stores/list";
    }

    @GetMapping("/with-drive-through")
    public String storesWithDriveThrough(Model model) {
        List<Store> stores = storeService.getStoresWithDriveThrough();
        model.addAttribute("stores", stores);
        model.addAttribute("filterTitle", "Stores with Drive-Through");
        return "stores/list";
    }
}