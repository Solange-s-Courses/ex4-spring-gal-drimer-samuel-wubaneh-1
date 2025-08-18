package com.example.ex4springgaldrimer1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "stores")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Store name is required")
    @Size(max = 100, message = "Store name cannot exceed 100 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Address is required")
    @Column(nullable = false, unique = true)
    private String address;

    @NotBlank(message = "City is required")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "State is required")
    @Column(nullable = false)
    private String state;

    @NotBlank(message = "ZIP code is required")
    @Pattern(regexp = "\\d{5}(-\\d{4})?", message = "Invalid ZIP code format")
    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Pattern(regexp = "\\(\\d{3}\\) \\d{3}-\\d{4}", message = "Phone format should be (123) 456-7890")
    @Column
    private String phone;

    @Column
    private String email;

    @Column(name = "working_hours")
    private String workingHours;

    @Column(name = "working_days")
    private String workingDays;

    @Column(name = "store_manager")
    private String storeManager;

    @Column(name = "opening_date")
    private LocalDateTime openingDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "store_size_sqft")
    private Integer storeSizeSqft;

    @Column(name = "parking_available")
    private Boolean parkingAvailable = true;

    @Column(name = "drive_through")
    private Boolean driveThrough = false;

    // Relationships
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "store_products",
            joinColumns = @JoinColumn(name = "store_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> products;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StoreComment> comments;

    // Constructors
    public Store() {
        this.openingDate = LocalDateTime.now();
        this.isActive = true;
        this.parkingAvailable = true;
        this.driveThrough = false;
    }

    public Store(String name, String address, String city, String state, String zipCode) {
        this();
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public String getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(String workingDays) {
        this.workingDays = workingDays;
    }

    public String getStoreManager() {
        return storeManager;
    }

    public void setStoreManager(String storeManager) {
        this.storeManager = storeManager;
    }

    public LocalDateTime getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(LocalDateTime openingDate) {
        this.openingDate = openingDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStoreSizeSqft() {
        return storeSizeSqft;
    }

    public void setStoreSizeSqft(Integer storeSizeSqft) {
        this.storeSizeSqft = storeSizeSqft;
    }

    public Boolean getParkingAvailable() {
        return parkingAvailable;
    }

    public void setParkingAvailable(Boolean parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }

    public Boolean getDriveThrough() {
        return driveThrough;
    }

    public void setDriveThrough(Boolean driveThrough) {
        this.driveThrough = driveThrough;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public List<StoreComment> getComments() {
        return comments;
    }

    public void setComments(List<StoreComment> comments) {
        this.comments = comments;
    }

    // Helper methods
    public String getFullAddress() {
        return address + ", " + city + ", " + state + " " + zipCode;
    }

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
                .mapToInt(StoreComment::getRating)
                .average()
                .orElse(0.0);
    }

    public String getStatusDisplay() {
        return isActive ? "Open" : "Closed";
    }

    public String getFormattedOpeningDate() {
        if (openingDate == null) return "Unknown";
        return openingDate.toLocalDate().toString();
    }

    public int getProductCount() {
        return products != null ? products.size() : 0;
    }

    @Override
    public String toString() {
        return "Store{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}