package com.example.ex4springgaldrimer1.repository;

import com.example.ex4springgaldrimer1.entity.StoreComment;
import com.example.ex4springgaldrimer1.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreCommentRepository extends JpaRepository<StoreComment, Long> {

    // Store-specific comments
    List<StoreComment> findByStoreIdAndStatus(Long storeId, CommentStatus status);
    List<StoreComment> findByStoreIdOrderByTimestampDesc(Long storeId);
    Page<StoreComment> findByStoreIdAndStatus(Long storeId, CommentStatus status, Pageable pageable);

    // User-specific comments
    List<StoreComment> findByUserIdOrderByTimestampDesc(Long userId);
    Page<StoreComment> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);

    // Status-based queries
    List<StoreComment> findByStatus(CommentStatus status);
    List<StoreComment> findByStatusOrderByTimestampAsc(CommentStatus status);
    Page<StoreComment> findByStatusOrderByTimestampDesc(CommentStatus status, Pageable pageable);

    // Rating queries
    List<StoreComment> findByStoreIdAndRating(Long storeId, Integer rating);
    List<StoreComment> findByRatingGreaterThanEqual(Integer rating);

    // Recommendation queries
    List<StoreComment> findByStoreIdAndWouldRecommend(Long storeId, Boolean wouldRecommend);
    List<StoreComment> findByWouldRecommend(Boolean wouldRecommend);

    // Statistics queries
    long countByStoreIdAndStatus(Long storeId, CommentStatus status);
    long countByStatus(CommentStatus status);
    long countByUserId(Long userId);
    long countByStoreIdAndWouldRecommend(Long storeId, Boolean wouldRecommend);

    @Query("SELECT AVG(sc.rating) FROM StoreComment sc WHERE sc.store.id = :storeId AND sc.status = :status")
    Double findAverageRatingByStoreIdAndStatus(@Param("storeId") Long storeId, @Param("status") CommentStatus status);

    @Query("SELECT AVG(sc.serviceRating) FROM StoreComment sc WHERE sc.store.id = :storeId AND sc.status = 'APPROVED' AND sc.serviceRating IS NOT NULL")
    Double findAverageServiceRatingByStoreId(@Param("storeId") Long storeId);

    @Query("SELECT AVG(sc.cleanlinessRating) FROM StoreComment sc WHERE sc.store.id = :storeId AND sc.status = 'APPROVED' AND sc.cleanlinessRating IS NOT NULL")
    Double findAverageCleanlinessRatingByStoreId(@Param("storeId") Long storeId);

    @Query("SELECT AVG(sc.locationRating) FROM StoreComment sc WHERE sc.store.id = :storeId AND sc.status = 'APPROVED' AND sc.locationRating IS NOT NULL")
    Double findAverageLocationRatingByStoreId(@Param("storeId") Long storeId);

    @Query("SELECT COUNT(sc) FROM StoreComment sc WHERE sc.store.id = :storeId AND sc.status = 'APPROVED' AND sc.rating = :rating")
    long countByStoreIdAndStatusApprovedAndRating(@Param("storeId") Long storeId, @Param("rating") Integer rating);

    // Recent comments
    @Query("SELECT sc FROM StoreComment sc WHERE sc.status = 'APPROVED' ORDER BY sc.timestamp DESC")
    List<StoreComment> findRecentApprovedComments(Pageable pageable);

    // High rating comments
    @Query("SELECT sc FROM StoreComment sc WHERE sc.status = 'APPROVED' AND sc.rating >= :minRating ORDER BY sc.rating DESC, sc.timestamp DESC")
    List<StoreComment> findHighRatingComments(@Param("minRating") Integer minRating, Pageable pageable);
}