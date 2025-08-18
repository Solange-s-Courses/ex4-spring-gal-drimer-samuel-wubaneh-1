package com.example.ex4springgaldrimer1.service;

import com.example.ex4springgaldrimer1.entity.Product;
import com.example.ex4springgaldrimer1.entity.ProductComment;
import com.example.ex4springgaldrimer1.entity.User;
import com.example.ex4springgaldrimer1.enums.CommentStatus;
import com.example.ex4springgaldrimer1.repository.ProductCommentRepository;
import com.example.ex4springgaldrimer1.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCommentRepository productCommentRepository;

    // Product CRUD operations
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Product createProduct(String name, String barcode, BigDecimal price, String description, String category) {
        // Validate barcode uniqueness
        if (productRepository.existsByBarcode(barcode)) {
            throw new RuntimeException("Product with barcode " + barcode + " already exists");
        }

        Product product = new Product(name, barcode, price, description, category);
        return productRepository.save(product);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Optional<Product> findByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public List<Product> getProductsInStock() {
        return productRepository.findByInStock(true);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // Category operations
    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category);
    }

    // Brand operations
    public List<String> getAllBrands() {
        return productRepository.findAllBrands();
    }

    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    // Search operations
    public List<Product> searchProducts(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.searchProducts(searchTerm.trim());
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    // Price operations
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }

    public BigDecimal getMinPrice() {
        return productRepository.getMinPrice();
    }

    public BigDecimal getMaxPrice() {
        return productRepository.getMaxPrice();
    }

    public Double getAveragePrice() {
        return productRepository.getAveragePrice();
    }

    // Advanced filtering
    public Page<Product> getProductsWithFilters(String name, String category, String brand,
                                                BigDecimal minPrice, BigDecimal maxPrice,
                                                Boolean inStock, Pageable pageable) {
        return productRepository.findWithFilters(name, category, brand, minPrice, maxPrice, inStock, pageable);
    }

    // Featured products
    public List<Product> getNewestProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdDate"));
        return productRepository.findNewestProducts(pageable);
    }

    public List<Product> getMostReviewedProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findMostReviewedProducts(pageable);
    }

    public List<Product> getFeaturedProducts() {
        // Return newest products as featured
        return getNewestProducts(6);
    }

    // Stock management
    public void updateStock(Long productId, Integer quantity) {
        Product product = getProductById(productId);
        product.setStockQuantity(quantity);
        product.setInStock(quantity > 0);
        productRepository.save(product);
    }

    public void decreaseStock(Long productId, Integer quantity) {
        Product product = getProductById(productId);
        int newQuantity = Math.max(0, product.getStockQuantity() - quantity);
        updateStock(productId, newQuantity);
    }

    public void increaseStock(Long productId, Integer quantity) {
        Product product = getProductById(productId);
        int newQuantity = product.getStockQuantity() + quantity;
        updateStock(productId, newQuantity);
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findByStockQuantityLessThan(10);
    }

    // Product Comments/Reviews
    public ProductComment addComment(Long productId, User user, String title, String content, Integer rating) {
        Product product = getProductById(productId);

        ProductComment comment = new ProductComment(title, content, rating, user, product);
        ProductComment savedComment = productCommentRepository.save(comment);

        // Award customer points for writing a review
        if (user.getRole() == com.example.ex4springgaldrimer1.enums.Role.CUSTOMER) {
            user.addCustomerPoints(10); // 10 points for writing a review
        }

        return savedComment;
    }

    public List<ProductComment> getApprovedComments(Long productId) {
        return productCommentRepository.findByProductIdAndStatus(productId, CommentStatus.APPROVED);
    }

    public Page<ProductComment> getApprovedComments(Long productId, Pageable pageable) {
        return productCommentRepository.findByProductIdAndStatus(productId, CommentStatus.APPROVED, pageable);
    }

    public List<ProductComment> getPendingComments() {
        return productCommentRepository.findByStatusOrderByTimestampAsc(CommentStatus.PENDING);
    }

    public void approveComment(Long commentId) {
        ProductComment comment = productCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setStatus(CommentStatus.APPROVED);
        productCommentRepository.save(comment);
    }

    public void rejectComment(Long commentId) {
        ProductComment comment = productCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setStatus(CommentStatus.REJECTED);
        productCommentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        productCommentRepository.deleteById(commentId);
    }

    // Statistics and analytics
    public long getTotalProducts() {
        return productRepository.count();
    }

    public long getProductsInStockCount() {
        return productRepository.countByInStock(true);
    }

    public long getProductsOutOfStockCount() {
        return productRepository.countByInStock(false);
    }

    public long getProductCountByCategory(String category) {
        return productRepository.countByCategory(category);
    }

    public double getProductAverageRating(Long productId) {
        Double rating = productCommentRepository.findAverageRatingByProductIdAndStatus(productId, CommentStatus.APPROVED);
        return rating != null ? rating : 0.0;
    }

    public long getProductReviewCount(Long productId) {
        return productCommentRepository.countByProductIdAndStatus(productId, CommentStatus.APPROVED);
    }

    // Validation methods
    public boolean isBarcodeTaken(String barcode) {
        return productRepository.existsByBarcode(barcode);
    }

    public boolean isBarcodeTaken(String barcode, Long excludeProductId) {
        Optional<Product> existing = productRepository.findByBarcode(barcode);
        return existing.isPresent() && !existing.get().getId().equals(excludeProductId);
    }
}