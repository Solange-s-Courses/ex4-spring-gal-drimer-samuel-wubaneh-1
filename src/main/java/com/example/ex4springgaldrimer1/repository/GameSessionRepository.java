// GameSessionRepository.java
package com.example.ex4springgaldrimer1.repository;

import com.example.ex4springgaldrimer1.entity.GameSession;
import com.example.ex4springgaldrimer1.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    // User's sessions
    List<GameSession> findByUserOrderBySessionStartDesc(User user);
    List<GameSession> findByUserAndIsCompletedOrderBySessionStartDesc(User user, Boolean isCompleted);

    // Active (incomplete) session for user
    Optional<GameSession> findByUserAndIsCompleted(User user, Boolean isCompleted);

    // Recent sessions
    List<GameSession> findBySessionStartAfterOrderBySessionStartDesc(LocalDateTime after);

    // Top scores
    @Query("SELECT gs FROM GameSession gs WHERE gs.isCompleted = true ORDER BY gs.currentScore DESC")
    Page<GameSession> findTopScores(Pageable pageable);

    @Query("SELECT gs FROM GameSession gs WHERE gs.isCompleted = true AND gs.user = :user ORDER BY gs.currentScore DESC")
    Page<GameSession> findTopScoresByUser(@Param("user") User user, Pageable pageable);

    // Statistics
    long countByUser(User user);
    long countByUserAndIsCompleted(User user, Boolean isCompleted);

    @Query("SELECT AVG(gs.currentScore) FROM GameSession gs WHERE gs.isCompleted = true AND gs.user = :user")
    Double findAverageScoreByUser(@Param("user") User user);

    @Query("SELECT MAX(gs.currentScore) FROM GameSession gs WHERE gs.isCompleted = true AND gs.user = :user")
    Integer findHighestScoreByUser(@Param("user") User user);

    @Query("SELECT AVG(gs.currentScore) FROM GameSession gs WHERE gs.isCompleted = true")
    Double findAverageScore();

    @Query("SELECT MAX(gs.currentScore) FROM GameSession gs WHERE gs.isCompleted = true")
    Integer findHighestScore();
}