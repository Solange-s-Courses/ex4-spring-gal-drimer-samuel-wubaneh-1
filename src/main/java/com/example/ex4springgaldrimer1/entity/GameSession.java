package com.example.ex4springgaldrimer1.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_sessions")
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "session_start", nullable = false)
    private LocalDateTime sessionStart;

    @Column(name = "session_end")
    private LocalDateTime sessionEnd;

    @Column(name = "current_score", nullable = false)
    private Integer currentScore = 0;

    @Column(name = "questions_answered", nullable = false)
    private Integer questionsAnswered = 0;

    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers = 0;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions = 10; // Default game length

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;

    @Column(name = "customer_points_awarded")
    private Integer customerPointsAwarded = 0;

    // Track which questions have been asked in this session
    @Column(columnDefinition = "TEXT")
    private String askedQuestionIds; // Comma-separated list of question IDs

    // Constructors
    public GameSession() {
        this.sessionStart = LocalDateTime.now();
        this.currentScore = 0;
        this.questionsAnswered = 0;
        this.correctAnswers = 0;
        this.totalQuestions = 10;
        this.isCompleted = false;
        this.customerPointsAwarded = 0;
    }

    public GameSession(User user) {
        this();
        this.user = user;
    }

    public GameSession(User user, Integer totalQuestions) {
        this(user);
        this.totalQuestions = totalQuestions;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getSessionStart() {
        return sessionStart;
    }

    public void setSessionStart(LocalDateTime sessionStart) {
        this.sessionStart = sessionStart;
    }

    public LocalDateTime getSessionEnd() {
        return sessionEnd;
    }

    public void setSessionEnd(LocalDateTime sessionEnd) {
        this.sessionEnd = sessionEnd;
    }

    public Integer getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(Integer currentScore) {
        this.currentScore = currentScore;
    }

    public Integer getQuestionsAnswered() {
        return questionsAnswered;
    }

    public void setQuestionsAnswered(Integer questionsAnswered) {
        this.questionsAnswered = questionsAnswered;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Integer getCustomerPointsAwarded() {
        return customerPointsAwarded;
    }

    public void setCustomerPointsAwarded(Integer customerPointsAwarded) {
        this.customerPointsAwarded = customerPointsAwarded;
    }

    public String getAskedQuestionIds() {
        return askedQuestionIds;
    }

    public void setAskedQuestionIds(String askedQuestionIds) {
        this.askedQuestionIds = askedQuestionIds;
    }

    // Helper methods
    public double getAccuracyPercentage() {
        if (questionsAnswered == 0) return 0.0;
        return (double) correctAnswers / questionsAnswered * 100.0;
    }

    public boolean isGameOver() {
        return isCompleted || questionsAnswered >= totalQuestions;
    }

    public int getRemainingQuestions() {
        return Math.max(0, totalQuestions - questionsAnswered);
    }

    public void addCorrectAnswer(int points) {
        this.questionsAnswered++;
        this.correctAnswers++;
        this.currentScore += points;
    }

    public void addIncorrectAnswer() {
        this.questionsAnswered++;
    }

    public void addAskedQuestion(Long questionId) {
        if (askedQuestionIds == null || askedQuestionIds.isEmpty()) {
            askedQuestionIds = questionId.toString();
        } else {
            askedQuestionIds += "," + questionId;
        }
    }

    public void completeGame() {
        this.isCompleted = true;
        this.sessionEnd = LocalDateTime.now();

        // Award customer points based on performance
        // Base points for completing the game + bonus for accuracy
        int basePoints = 20; // Points for completing the game
        int accuracyBonus = (int) (getAccuracyPercentage() / 10); // 1 point per 10% accuracy
        this.customerPointsAwarded = basePoints + accuracyBonus;
    }

    public long getDurationMinutes() {
        if (sessionEnd == null) {
            return java.time.Duration.between(sessionStart, LocalDateTime.now()).toMinutes();
        }
        return java.time.Duration.between(sessionStart, sessionEnd).toMinutes();
    }

    @Override
    public String toString() {
        return "GameSession{" +
                "id=" + id +
                ", currentScore=" + currentScore +
                ", questionsAnswered=" + questionsAnswered +
                ", correctAnswers=" + correctAnswers +
                ", isCompleted=" + isCompleted +
                '}';
    }
}