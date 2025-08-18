package com.example.ex4springgaldrimer1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name cannot exceed 100 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Barcode is required")
    @Column(unique = true, nullable = false)
    private String barcode;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Category is required")
    @Column(nullable = false)
    private String category;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "in_stock", nullable = false)
    private Boolean inStock = true;

    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;

    @Column
    private String brand;

    @Column(name = "weight_kg")
    private Double weight;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductComment> comments;

    // Constructors
    public Product() {
        this.createdDate = LocalDateTime.now();
        this.inStock = true;
        this.stockQuantity = 0;
    }

    public Product(String name, String barcode, BigDecimal price, String description, String category) {
        this();
        this.name = name;
        this.barcode = barcode;
        this.price = price;
        this.description = description;
        this.category = category;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public List<ProductComment> getComments() {
        return comments;
    }

    public void setComments(List<ProductComment> comments) {
        this.comments = comments;
    }

    // Helper methods
    public long getApprovedCommentsCount() {
        if (comments == null) return 0;
        return comments.stream()
                .filter(comment -> comment.getStatus() == com.example.ex4springgaldrimer1.enums.CommentStatus.APPROVED)
                .count();
    }

    public double getAverageRating() {
        if (comments == null || comments.isEmpty()) return 0.0;
        return comments.stream()
                .filter(comment -> comment.getStatus() == com.example.ex4springgaldrimer1.enums.CommentStatus.APPROVED)
                .mapToInt(ProductComment::getRating)
                .average()
                .orElse(0.0);
    }

    public String getFormattedPrice() {
        return "$" + String.format("%.2f", price);
    }

    public String getStockStatus() {
        if (!inStock) return "Out of Stock";
        if (stockQuantity <= 5) return "Low Stock";
        return "In Stock";
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", barcode='" + barcode + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", inStock=" + inStock +
                '}';
    }
}