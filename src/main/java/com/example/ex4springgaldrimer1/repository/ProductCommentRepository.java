package com.example.ex4springgaldrimer1.repository;

import com.example.ex4springgaldrimer1.entity.ProductComment;
import com.example.ex4springgaldrimer1.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCommentRepository extends JpaRepository<ProductComment, Long> {

    // Product-specific comments
    List<ProductComment> findByProductIdAndStatus(Long productId, CommentStatus status);
    List<ProductComment> findByProductIdOrderByTimestampDesc(Long productId);
    Page<ProductComment> findByProductIdAndStatus(Long productId, CommentStatus status, Pageable pageable);

    // User-specific comments
    List<ProductComment> findByUserIdOrderByTimestampDesc(Long userId);
    Page<ProductComment> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);

    // Status-based queries
    List<ProductComment> findByStatus(CommentStatus status);
    List<ProductComment> findByStatusOrderByTimestampAsc(CommentStatus status);
    Page<ProductComment> findByStatusOrderByTimestampDesc(CommentStatus status, Pageable pageable);

    // Rating queries
    List<ProductComment> findByProductIdAndRating(Long productId, Integer rating);
    List<ProductComment> findByRatingGreaterThanEqual(Integer rating);

    // Statistics queries
    long countByProductIdAndStatus(Long productId, CommentStatus status);
    long countByStatus(CommentStatus status);
    long countByUserId(Long userId);

    @Query("SELECT AVG(pc.rating) FROM ProductComment pc WHERE pc.product.id = :productId AND pc.status = :status")
    Double findAverageRatingByProductIdAndStatus(@Param("productId") Long productId, @Param("status") CommentStatus status);

    @Query("SELECT COUNT(pc) FROM ProductComment pc WHERE pc.product.id = :productId AND pc.status = 'APPROVED' AND pc.rating = :rating")
    long countByProductIdAndStatusApprovedAndRating(@Param("productId") Long productId, @Param("rating") Integer rating);

    // Recent comments
    @Query("SELECT pc FROM ProductComment pc WHERE pc.status = 'APPROVED' ORDER BY pc.timestamp DESC")
    List<ProductComment> findRecentApprovedComments(Pageable pageable);
}