package com.example.ex4springgaldrimer1.entity;

import com.example.ex4springgaldrimer1.enums.QuestionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_questions")
public class GameQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Question text is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @NotBlank(message = "Option A is required")
    @Column(nullable = false)
    private String optionA;

    @NotBlank(message = "Option B is required")
    @Column(nullable = false)
    private String optionB;

    @NotBlank(message = "Option C is required")
    @Column(nullable = false)
    private String optionC;

    @NotBlank(message = "Option D is required")
    @Column(nullable = false)
    private String optionD;

    @NotBlank(message = "Correct answer is required")
    @Column(nullable = false)
    private String correctAnswer; // A, B, C, or D

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType questionType; // PRODUCT, STORE, GENERAL

    @NotNull(message = "Points value is required")
    @Column(nullable = false)
    private Integer points = 10; // Points awarded for correct answer

    @Column(name = "difficulty_level")
    private Integer difficultyLevel = 1; // 1 = Easy, 2 = Medium, 3 = Hard

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(columnDefinition = "TEXT")
    private String explanation; // Optional explanation for the answer

    // Reference IDs for context (optional)
    @Column(name = "product_id")
    private Long productId; // If question is about a specific product

    @Column(name = "store_id")
    private Long storeId; // If question is about a specific store

    // Constructors
    public GameQuestion() {
        this.createdDate = LocalDateTime.now();
        this.isActive = true;
        this.points = 10;
        this.difficultyLevel = 1;
    }

    public GameQuestion(String questionText, String optionA, String optionB, String optionC, String optionD,
                        String correctAnswer, QuestionType questionType) {
        this();
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.questionType = questionType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(Integer difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    // Helper methods
    public String getCorrectOptionText() {
        switch (correctAnswer.toUpperCase()) {
            case "A": return optionA;
            case "B": return optionB;
            case "C": return optionC;
            case "D": return optionD;
            default: return "Unknown";
        }
    }

    public String getDifficultyText() {
        switch (difficultyLevel) {
            case 1: return "Easy";
            case 2: return "Medium";
            case 3: return "Hard";
            default: return "Unknown";
        }
    }

    @Override
    public String toString() {
        return "GameQuestion{" +
                "id=" + id +
                ", questionType=" + questionType +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", points=" + points +
                ", difficultyLevel=" + difficultyLevel +
                '}';
    }
}