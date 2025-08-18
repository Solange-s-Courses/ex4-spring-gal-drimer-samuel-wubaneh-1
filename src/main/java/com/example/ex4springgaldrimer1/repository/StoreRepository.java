package com.example.ex4springgaldrimer1.repository;

import com.example.ex4springgaldrimer1.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    // Basic queries
    Optional<Store> findByAddress(String address);
    boolean existsByAddress(String address);

    // Active stores
    List<Store> findByIsActive(Boolean isActive);
    List<Store> findByIsActiveOrderByName(Boolean isActive);

    // Location-based queries
    List<Store> findByCityIgnoreCase(String city);
    List<Store> findByStateIgnoreCase(String state);
    List<Store> findByCityIgnoreCaseAndStateIgnoreCase(String city, String state);
    List<Store> findByZipCode(String zipCode);

    @Query("SELECT DISTINCT s.city FROM Store s WHERE s.isActive = true ORDER BY s.city")
    List<String> findAllActiveCities();

    @Query("SELECT DISTINCT s.state FROM Store s WHERE s.isActive = true ORDER BY s.state")
    List<String> findAllActiveStates();

    // Search queries
    List<Store> findByNameContainingIgnoreCase(String name);

    @Query("SELECT s FROM Store s WHERE s.isActive = true AND " +
            "(LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.address) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.city) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.storeManager) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Store> searchActiveStores(@Param("search") String search);

    // Advanced filtering
    @Query("SELECT s FROM Store s WHERE " +
            "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:city IS NULL OR LOWER(s.city) = LOWER(:city)) AND " +
            "(:state IS NULL OR LOWER(s.state) = LOWER(:state)) AND " +
            "(:zipCode IS NULL OR s.zipCode = :zipCode) AND " +
            "(:isActive IS NULL OR s.isActive = :isActive) AND " +
            "(:parkingAvailable IS NULL OR s.parkingAvailable = :parkingAvailable) AND " +
            "(:driveThrough IS NULL OR s.driveThrough = :driveThrough)")
    Page<Store> findWithFilters(@Param("name") String name,
                                @Param("city") String city,
                                @Param("state") String state,
                                @Param("zipCode") String zipCode,
                                @Param("isActive") Boolean isActive,
                                @Param("parkingAvailable") Boolean parkingAvailable,
                                @Param("driveThrough") Boolean driveThrough,
                                Pageable pageable);

    // Feature-based queries
    List<Store> findByParkingAvailable(Boolean parkingAvailable);
    List<Store> findByDriveThrough(Boolean driveThrough);
    List<Store> findByParkingAvailableAndDriveThrough(Boolean parkingAvailable, Boolean driveThrough);

    // Product relationship queries
    @Query("SELECT s FROM Store s JOIN s.products p WHERE p.id = :productId AND s.isActive = true")
    List<Store> findActiveStoresByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(p) FROM Store s JOIN s.products p WHERE s.id = :storeId")
    Long countProductsByStoreId(@Param("storeId") Long storeId);

    // Manager queries
    List<Store> findByStoreManagerContainingIgnoreCase(String managerName);

    // Statistics queries
    long countByIsActive(Boolean isActive);
    long countByCity(String city);
    long countByState(String state);

    @Query("SELECT COUNT(s) FROM Store s WHERE s.isActive = true AND s.parkingAvailable = true")
    long countActiveStoresWithParking();

    @Query("SELECT COUNT(s) FROM Store s WHERE s.isActive = true AND s.driveThrough = true")
    long countActiveStoresWithDriveThrough();

    @Query("SELECT AVG(SIZE(s.comments)) FROM Store s WHERE s.isActive = true")
    Double getAverageCommentsPerStore();

    // Top rated stores
    @Query("SELECT s FROM Store s WHERE s.isActive = true AND SIZE(s.comments) > 0 " +
            "ORDER BY (SELECT AVG(CAST(c.rating AS double)) FROM StoreComment c WHERE c.store = s AND c.status = 'APPROVED') DESC")
    List<Store> findTopRatedStores(Pageable pageable);

    // Most reviewed stores
    @Query("SELECT s FROM Store s WHERE s.isActive = true " +
            "ORDER BY (SELECT COUNT(c) FROM StoreComment c WHERE c.store = s AND c.status = 'APPROVED') DESC")
    List<Store> findMostReviewedStores(Pageable pageable);

    // Recently opened stores
    @Query("SELECT s FROM Store s WHERE s.isActive = true ORDER BY s.openingDate DESC")
    List<Store> findRecentlyOpenedStores(Pageable pageable);
}