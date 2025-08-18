package com.example.ex4springgaldrimer1.entity;

import com.example.ex4springgaldrimer1.enums.CommentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "store_comments")
public class StoreComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Comment content is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    @Column(nullable = false)
    private Integer rating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentStatus status;

    @Column
    private String title;

    // Additional store-specific fields
    @Column(name = "visit_date")
    private LocalDateTime visitDate;

    @Column(name = "service_rating")
    @Min(value = 1) @Max(value = 5)
    private Integer serviceRating;

    @Column(name = "cleanliness_rating")
    @Min(value = 1) @Max(value = 5)
    private Integer cleanlinessRating;

    @Column(name = "location_rating")
    @Min(value = 1) @Max(value = 5)
    private Integer locationRating;

    @Column(name = "would_recommend")
    private Boolean wouldRecommend;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    // Constructors
    public StoreComment() {
        this.timestamp = LocalDateTime.now();
        this.status = CommentStatus.PENDING;
        this.wouldRecommend = true;
    }

    public StoreComment(String content, Integer rating, User user, Store store) {
        this();
        this.content = content;
        this.rating = rating;
        this.user = user;
        this.store = store;
    }

    public StoreComment(String title, String content, Integer rating, User user, Store store) {
        this(content, rating, user, store);
        this.title = title;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public CommentStatus getStatus() {
        return status;
    }

    public void setStatus(CommentStatus status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDateTime visitDate) {
        this.visitDate = visitDate;
    }

    public Integer getServiceRating() {
        return serviceRating;
    }

    public void setServiceRating(Integer serviceRating) {
        this.serviceRating = serviceRating;
    }

    public Integer getCleanlinessRating() {
        return cleanlinessRating;
    }

    public void setCleanlinessRating(Integer cleanlinessRating) {
        this.cleanlinessRating = cleanlinessRating;
    }

    public Integer getLocationRating() {
        return locationRating;
    }

    public void setLocationRating(Integer locationRating) {
        this.locationRating = locationRating;
    }

    public Boolean getWouldRecommend() {
        return wouldRecommend;
    }

    public void setWouldRecommend(Boolean wouldRecommend) {
        this.wouldRecommend = wouldRecommend;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    // Helper methods
    public String getStarRating() {
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= rating) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }

    public String getFormattedTimestamp() {
        return timestamp.toLocalDate().toString();
    }

    public String getFormattedVisitDate() {
        return visitDate != null ? visitDate.toLocalDate().toString() : "Not specified";
    }

    public double getAverageSubRating() {
        int count = 0;
        int total = 0;

        if (serviceRating != null) {
            total += serviceRating;
            count++;
        }
        if (cleanlinessRating != null) {
            total += cleanlinessRating;
            count++;
        }
        if (locationRating != null) {
            total += locationRating;
            count++;
        }

        return count > 0 ? (double) total / count : rating;
    }

    @Override
    public String toString() {
        return "StoreComment{" +
                "id=" + id +
                ", rating=" + rating +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", wouldRecommend=" + wouldRecommend +
                '}';
    }
}