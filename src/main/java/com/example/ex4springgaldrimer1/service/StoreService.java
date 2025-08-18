package com.example.ex4springgaldrimer1.service;

import com.example.ex4springgaldrimer1.entity.Store;
import com.example.ex4springgaldrimer1.entity.StoreComment;
import com.example.ex4springgaldrimer1.entity.User;
import com.example.ex4springgaldrimer1.enums.CommentStatus;
import com.example.ex4springgaldrimer1.repository.StoreCommentRepository;
import com.example.ex4springgaldrimer1.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private StoreCommentRepository storeCommentRepository;

    // Store CRUD operations
    public Store saveStore(Store store) {
        return storeRepository.save(store);
    }

    public Store createStore(String name, String address, String city, String state, String zipCode) {
        // Validate address uniqueness
        if (storeRepository.existsByAddress(address)) {
            throw new RuntimeException("Store with address " + address + " already exists");
        }

        Store store = new Store(name, address, city, state, zipCode);
        return storeRepository.save(store);
    }

    public Optional<Store> findById(Long id) {
        return storeRepository.findById(id);
    }

    public Store getStoreById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Store not found with id: " + id));
    }

    public Optional<Store> findByAddress(String address) {
        return storeRepository.findByAddress(address);
    }

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    public Page<Store> getAllStores(Pageable pageable) {
        return storeRepository.findAll(pageable);
    }

    public List<Store> getActiveStores() {
        return storeRepository.findByIsActiveOrderByName(true);
    }

    public void deleteStore(Long id) {
        storeRepository.deleteById(id);
    }

    public void activateStore(Long id) {
        Store store = getStoreById(id);
        store.setIsActive(true);
        storeRepository.save(store);
    }

    public void deactivateStore(Long id) {
        Store store = getStoreById(id);
        store.setIsActive(false);
        storeRepository.save(store);
    }

    // Location operations
    public List<String> getAllActiveCities() {
        return storeRepository.findAllActiveCities();
    }

    public List<String> getAllActiveStates() {
        return storeRepository.findAllActiveStates();
    }

    public List<Store> getStoresByCity(String city) {
        return storeRepository.findByCityIgnoreCase(city);
    }

    public List<Store> getStoresByState(String state) {
        return storeRepository.findByStateIgnoreCase(state);
    }

    public List<Store> getStoresByCityAndState(String city, String state) {
        return storeRepository.findByCityIgnoreCaseAndStateIgnoreCase(city, state);
    }

    public List<Store> getStoresByZipCode(String zipCode) {
        return storeRepository.findByZipCode(zipCode);
    }

    // Search operations
    public List<Store> searchActiveStores(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getActiveStores();
        }
        return storeRepository.searchActiveStores(searchTerm.trim());
    }

    public List<Store> searchStoresByName(String name) {
        return storeRepository.findByNameContainingIgnoreCase(name);
    }

    // Advanced filtering
    public Page<Store> getStoresWithFilters(String name, String city, String state, String zipCode,
                                            Boolean isActive, Boolean parkingAvailable, Boolean driveThrough,
                                            Pageable pageable) {
        return storeRepository.findWithFilters(name, city, state, zipCode, isActive, parkingAvailable, driveThrough, pageable);
    }

    // Feature-based queries
    public List<Store> getStoresWithParking() {
        return storeRepository.findByParkingAvailable(true);
    }

    public List<Store> getStoresWithDriveThrough() {
        return storeRepository.findByDriveThrough(true);
    }

    public List<Store> getStoresWithBothFeatures() {
        return storeRepository.findByParkingAvailableAndDriveThrough(true, true);
    }

    // Featured stores
    public List<Store> getTopRatedStores(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return storeRepository.findTopRatedStores(pageable);
    }

    public List<Store> getMostReviewedStores(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return storeRepository.findMostReviewedStores(pageable);
    }

    public List<Store> getRecentlyOpenedStores(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "openingDate"));
        return storeRepository.findRecentlyOpenedStores(pageable);
    }

    public List<Store> getFeaturedStores() {
        // Return top rated stores as featured
        return getTopRatedStores(6);
    }

    // Product relationship operations
    public List<Store> getStoresByProductId(Long productId) {
        return storeRepository.findActiveStoresByProductId(productId);
    }

    public Long getProductCountByStoreId(Long storeId) {
        return storeRepository.countProductsByStoreId(storeId);
    }

    // Store Comments/Reviews
    public StoreComment addComment(Long storeId, User user, String title, String content, Integer rating) {
        Store store = getStoreById(storeId);

        StoreComment comment = new StoreComment(title, content, rating, user, store);
        StoreComment savedComment = storeCommentRepository.save(comment);

        // Award customer points for writing a review
        if (user.getRole() == com.example.ex4springgaldrimer1.enums.Role.CUSTOMER) {
            user.addCustomerPoints(10); // 10 points for writing a review
        }

        return savedComment;
    }

    public StoreComment addDetailedComment(Long storeId, User user, String title, String content, Integer rating,
                                           Integer serviceRating, Integer cleanlinessRating, Integer locationRating,
                                           Boolean wouldRecommend) {
        Store store = getStoreById(storeId);

        StoreComment comment = new StoreComment(title, content, rating, user, store);
        comment.setServiceRating(serviceRating);
        comment.setCleanlinessRating(cleanlinessRating);
        comment.setLocationRating(locationRating);
        comment.setWouldRecommend(wouldRecommend);

        StoreComment savedComment = storeCommentRepository.save(comment);

        // Award customer points for detailed review
        if (user.getRole() == com.example.ex4springgaldrimer1.enums.Role.CUSTOMER) {
            user.addCustomerPoints(15); // 15 points for detailed review
        }

        return savedComment;
    }

    public List<StoreComment> getApprovedComments(Long storeId) {
        return storeCommentRepository.findByStoreIdAndStatus(storeId, CommentStatus.APPROVED);
    }

    public Page<StoreComment> getApprovedComments(Long storeId, Pageable pageable) {
        return storeCommentRepository.findByStoreIdAndStatus(storeId, CommentStatus.APPROVED, pageable);
    }

    public List<StoreComment> getPendingComments() {
        return storeCommentRepository.findByStatusOrderByTimestampAsc(CommentStatus.PENDING);
    }

    public void approveComment(Long commentId) {
        StoreComment comment = storeCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setStatus(CommentStatus.APPROVED);
        storeCommentRepository.save(comment);
    }

    public void rejectComment(Long commentId) {
        StoreComment comment = storeCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setStatus(CommentStatus.REJECTED);
        storeCommentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        storeCommentRepository.deleteById(commentId);
    }

    // Statistics and analytics
    public long getTotalStores() {
        return storeRepository.count();
    }

    public long getActiveStoresCount() {
        return storeRepository.countByIsActive(true);
    }

    public long getInactiveStoresCount() {
        return storeRepository.countByIsActive(false);
    }

    public long getStoresWithParkingCount() {
        return storeRepository.countActiveStoresWithParking();
    }

    public long getStoresWithDriveThoughCount() {
        return storeRepository.countActiveStoresWithDriveThrough();
    }

    public long getStoreCountByCity(String city) {
        return storeRepository.countByCity(city);
    }

    public long getStoreCountByState(String state) {
        return storeRepository.countByState(state);
    }

    public Double getAverageCommentsPerStore() {
        return storeRepository.getAverageCommentsPerStore();
    }

    public double getStoreAverageRating(Long storeId) {
        Double rating = storeCommentRepository.findAverageRatingByStoreIdAndStatus(storeId, CommentStatus.APPROVED);
        return rating != null ? rating : 0.0;
    }

    public long getStoreReviewCount(Long storeId) {
        return storeCommentRepository.countByStoreIdAndStatus(storeId, CommentStatus.APPROVED);
    }

    public long getStoreRecommendationCount(Long storeId) {
        return storeCommentRepository.countByStoreIdAndWouldRecommend(storeId, true);
    }

    public double getStoreServiceRating(Long storeId) {
        Double rating = storeCommentRepository.findAverageServiceRatingByStoreId(storeId);
        return rating != null ? rating : 0.0;
    }

    public double getStoreCleanlinessRating(Long storeId) {
        Double rating = storeCommentRepository.findAverageCleanlinessRatingByStoreId(storeId);
        return rating != null ? rating : 0.0;
    }

    public double getStoreLocationRating(Long storeId) {
        Double rating = storeCommentRepository.findAverageLocationRatingByStoreId(storeId);
        return rating != null ? rating : 0.0;
    }

    // Validation methods
    public boolean isAddressTaken(String address) {
        return storeRepository.existsByAddress(address);
    }

    public boolean isAddressTaken(String address, Long excludeStoreId) {
        Optional<Store> existing = storeRepository.findByAddress(address);
        return existing.isPresent() && !existing.get().getId().equals(excludeStoreId);
    }
}