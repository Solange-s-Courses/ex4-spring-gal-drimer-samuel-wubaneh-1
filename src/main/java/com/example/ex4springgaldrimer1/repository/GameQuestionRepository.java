// GameQuestionRepository.java
package com.example.ex4springgaldrimer1.repository;

import com.example.ex4springgaldrimer1.entity.GameQuestion;
import com.example.ex4springgaldrimer1.enums.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameQuestionRepository extends JpaRepository<GameQuestion, Long> {

    // Active questions
    List<GameQuestion> findByIsActive(Boolean isActive);

    // Questions by type
    List<GameQuestion> findByQuestionTypeAndIsActive(QuestionType questionType, Boolean isActive);

    // Questions by difficulty
    List<GameQuestion> findByDifficultyLevelAndIsActive(Integer difficultyLevel, Boolean isActive);

    // Random questions for game
    @Query(value = "SELECT * FROM game_questions WHERE is_active = true ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<GameQuestion> findRandomActiveQuestions(@Param("limit") int limit);

    @Query(value = "SELECT * FROM game_questions WHERE is_active = true AND question_type = :questionType ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<GameQuestion> findRandomActiveQuestionsByType(@Param("questionType") String questionType, @Param("limit") int limit);

    @Query(value = "SELECT * FROM game_questions WHERE is_active = true AND difficulty_level = :difficultyLevel ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<GameQuestion> findRandomActiveQuestionsByDifficulty(@Param("difficultyLevel") int difficultyLevel, @Param("limit") int limit);

    // Exclude already asked questions
    @Query(value = "SELECT * FROM game_questions WHERE is_active = true AND id NOT IN :excludeIds ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<GameQuestion> findRandomActiveQuestionsExcluding(@Param("excludeIds") List<Long> excludeIds, @Param("limit") int limit);

    // Statistics
    long countByIsActive(Boolean isActive);
    long countByQuestionTypeAndIsActive(QuestionType questionType, Boolean isActive);
    long countByDifficultyLevelAndIsActive(Integer difficultyLevel, Boolean isActive);
}

