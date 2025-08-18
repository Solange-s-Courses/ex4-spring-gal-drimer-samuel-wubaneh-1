package com.example.ex4springgaldrimer1.repository;

import com.example.ex4springgaldrimer1.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Basic queries
    Optional<Product> findByBarcode(String barcode);
    boolean existsByBarcode(String barcode);

    // Category queries
    List<Product> findByCategory(String category);
    List<Product> findByCategoryIgnoreCase(String category);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL ORDER BY p.category")
    List<String> findAllCategories();

    // Brand queries
    List<Product> findByBrand(String brand);

    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.brand IS NOT NULL ORDER BY p.brand")
    List<String> findAllBrands();

    // Search queries
    List<Product> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Product> searchProducts(@Param("search") String search);

    // Price range queries
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("SELECT p FROM Product p WHERE p.price >= :minPrice AND p.price <= :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    // Stock queries
    List<Product> findByInStock(Boolean inStock);
    List<Product> findByStockQuantityLessThan(Integer quantity);

    // Advanced search with pagination
    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:category IS NULL OR LOWER(p.category) = LOWER(:category)) AND " +
            "(:brand IS NULL OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:inStock IS NULL OR p.inStock = :inStock)")
    Page<Product> findWithFilters(@Param("name") String name,
                                  @Param("category") String category,
                                  @Param("brand") String brand,
                                  @Param("minPrice") BigDecimal minPrice,
                                  @Param("maxPrice") BigDecimal maxPrice,
                                  @Param("inStock") Boolean inStock,
                                  Pageable pageable);

    // Popular/Featured products
    @Query("SELECT p FROM Product p WHERE p.inStock = true ORDER BY p.createdDate DESC")
    List<Product> findNewestProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.inStock = true ORDER BY SIZE(p.comments) DESC")
    List<Product> findMostReviewedProducts(Pageable pageable);

    // Statistics queries
    long countByCategory(String category);
    long countByInStock(Boolean inStock);

    @Query("SELECT AVG(p.price) FROM Product p WHERE p.inStock = true")
    Double getAveragePrice();

    @Query("SELECT MIN(p.price) FROM Product p WHERE p.inStock = true")
    BigDecimal getMinPrice();

    @Query("SELECT MAX(p.price) FROM Product p WHERE p.inStock = true")
    BigDecimal getMaxPrice();
}