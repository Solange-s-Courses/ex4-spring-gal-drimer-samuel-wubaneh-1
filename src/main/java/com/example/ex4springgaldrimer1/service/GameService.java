package com.example.ex4springgaldrimer1.service;

import com.example.ex4springgaldrimer1.entity.GameQuestion;
import com.example.ex4springgaldrimer1.entity.GameSession;
import com.example.ex4springgaldrimer1.entity.User;
import com.example.ex4springgaldrimer1.enums.QuestionType;
import com.example.ex4springgaldrimer1.repository.GameQuestionRepository;
import com.example.ex4springgaldrimer1.repository.GameSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class GameService {

    @Autowired
    private GameQuestionRepository gameQuestionRepository;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private UserService userService;

    // Game Session Management
    public GameSession startNewGame(User user) {
        return startNewGame(user, 10); // Default 10 questions
    }

    public GameSession startNewGame(User user, int totalQuestions) {
        // End any existing incomplete session
        Optional<GameSession> existingSession = gameSessionRepository.findByUserAndIsCompleted(user, false);
        if (existingSession.isPresent()) {
            endGame(existingSession.get());
        }

        // Create new session
        GameSession session = new GameSession(user, totalQuestions);
        return gameSessionRepository.save(session);
    }

    public Optional<GameSession> getCurrentSession(User user) {
        return gameSessionRepository.findByUserAndIsCompleted(user, false);
    }

    public GameSession getSessionById(Long sessionId) {
        return gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Game session not found with id: " + sessionId));
    }

    // Question Management
    public GameQuestion getNextQuestion(GameSession session) {
        List<Long> askedQuestionIds = getAskedQuestionIds(session);

        List<GameQuestion> availableQuestions;
        if (askedQuestionIds.isEmpty()) {
            availableQuestions = gameQuestionRepository.findRandomActiveQuestions(1);
        } else {
            availableQuestions = gameQuestionRepository.findRandomActiveQuestionsExcluding(askedQuestionIds, 1);
        }

        if (availableQuestions.isEmpty()) {
            // No more questions available, end the game
            endGame(session);
            return null;
        }

        GameQuestion question = availableQuestions.get(0);

        // Track that this question has been asked
        session.addAskedQuestion(question.getId());
        gameSessionRepository.save(session);

        return question;
    }

    public GameQuestion getQuestionById(Long questionId) {
        return gameQuestionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + questionId));
    }

    private List<Long> getAskedQuestionIds(GameSession session) {
        if (session.getAskedQuestionIds() == null || session.getAskedQuestionIds().isEmpty()) {
            return List.of();
        }

        return Arrays.stream(session.getAskedQuestionIds().split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    // Answer Processing
    public boolean submitAnswer(GameSession session, Long questionId, String answer) {
        GameQuestion question = getQuestionById(questionId);
        boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(answer.trim());

        if (isCorrect) {
            session.addCorrectAnswer(question.getPoints());
        } else {
            session.addIncorrectAnswer();
        }

        gameSessionRepository.save(session);

        // Check if game is complete
        if (session.isGameOver()) {
            endGame(session);
        }

        return isCorrect;
    }

    public GameSession endGame(GameSession session) {
        if (!session.getIsCompleted()) {
            session.completeGame();
            gameSessionRepository.save(session);

            // Update user's highest score and customer points
            User user = session.getUser();
            user.updateHighestScore(session.getCurrentScore());
            user.addCustomerPoints(session.getCustomerPointsAwarded());
            userService.updateUser(user);
        }

        return session;
    }

    // Logout Game Handling - NEW METHODS

    /**
     * End active game session on user logout with zero points
     */
    public void endActiveGameOnLogout(User user) {
        try {
            Optional<GameSession> activeSession = gameSessionRepository.findByUserAndIsCompleted(user, false);

            if (activeSession.isPresent()) {
                GameSession session = activeSession.get();

                // Set game as completed with current progress but zero points
                session.setIsCompleted(true);
                session.setSessionEnd(LocalDateTime.now());
                session.setCustomerPointsAwarded(0); // Zero points for incomplete game

                // Save the session
                gameSessionRepository.save(session);

                System.out.println("Ended active game session for user: " + user.getUsername() +
                        " (Session ID: " + session.getId() + ") - Zero points awarded due to logout");
            }
        } catch (Exception e) {
            // Log error but don't throw - we don't want to prevent logout
            System.err.println("Error ending active game session on logout for user " + user.getUsername() + ": " + e.getMessage());
        }
    }

    /**
     * Alternative method: End active game session and award partial points based on current progress
     */
    public void endActiveGameOnLogoutWithPartialPoints(User user) {
        try {
            Optional<GameSession> activeSession = gameSessionRepository.findByUserAndIsCompleted(user, false);

            if (activeSession.isPresent()) {
                GameSession session = activeSession.get();

                // Calculate partial points (e.g., 50% of current score)
                int partialPoints = Math.max(1, session.getCurrentScore() / 2);

                // Complete the game with partial rewards
                session.setIsCompleted(true);
                session.setSessionEnd(LocalDateTime.now());
                session.setCustomerPointsAwarded(partialPoints);

                // Update user points and high score if applicable
                user.addCustomerPoints(partialPoints);
                if (session.getCurrentScore() > user.getHighestGameScore()) {
                    user.updateHighestScore(session.getCurrentScore());
                }

                // Save both session and user
                gameSessionRepository.save(session);
                userService.updateUser(user);

                System.out.println("Ended active game session for user: " + user.getUsername() +
                        " with partial points: " + partialPoints);
            }
        } catch (Exception e) {
            System.err.println("Error ending active game session with partial points for user " + user.getUsername() + ": " + e.getMessage());
        }
    }

    /**
     * Check if user has active game session (useful for UI display)
     */
    public boolean hasActiveGameSession(User user) {
        return gameSessionRepository.findByUserAndIsCompleted(user, false).isPresent();
    }

    // Question CRUD (for admin)
    public GameQuestion saveQuestion(GameQuestion question) {
        return gameQuestionRepository.save(question);
    }

    public GameQuestion createQuestion(String questionText, String optionA, String optionB, String optionC, String optionD,
                                       String correctAnswer, QuestionType questionType, Integer points, Integer difficultyLevel) {
        GameQuestion question = new GameQuestion(questionText, optionA, optionB, optionC, optionD, correctAnswer, questionType);
        question.setPoints(points);
        question.setDifficultyLevel(difficultyLevel);
        return gameQuestionRepository.save(question);
    }

    public List<GameQuestion> getAllQuestions() {
        return gameQuestionRepository.findAll();
    }

    public List<GameQuestion> getActiveQuestions() {
        return gameQuestionRepository.findByIsActive(true);
    }

    public List<GameQuestion> getQuestionsByType(QuestionType questionType) {
        return gameQuestionRepository.findByQuestionTypeAndIsActive(questionType, true);
    }

    public void deleteQuestion(Long questionId) {
        gameQuestionRepository.deleteById(questionId);
    }

    public void deactivateQuestion(Long questionId) {
        GameQuestion question = getQuestionById(questionId);
        question.setIsActive(false);
        gameQuestionRepository.save(question);
    }

    // Statistics and Leaderboards
    public List<GameSession> getUserGameHistory(User user) {
        return gameSessionRepository.findByUserAndIsCompletedOrderBySessionStartDesc(user, true);
    }

    public Page<GameSession> getTopScores(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return gameSessionRepository.findTopScores(pageable);
    }

    public Page<GameSession> getUserTopScores(User user, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return gameSessionRepository.findTopScoresByUser(user, pageable);
    }

    public List<GameSession> getRecentGames(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return gameSessionRepository.findBySessionStartAfterOrderBySessionStartDesc(since);
    }

    // Game Statistics
    public long getTotalGamesPlayed() {
        return gameSessionRepository.count();
    }

    public long getTotalGamesPlayedByUser(User user) {
        return gameSessionRepository.countByUserAndIsCompleted(user, true);
    }

    public Double getAverageScore() {
        return gameSessionRepository.findAverageScore();
    }

    public Double getUserAverageScore(User user) {
        return gameSessionRepository.findAverageScoreByUser(user);
    }

    public Integer getHighestScore() {
        return gameSessionRepository.findHighestScore();
    }

    public Integer getUserHighestScore(User user) {
        return gameSessionRepository.findHighestScoreByUser(user);
    }

    // Question Statistics
    public long getTotalQuestions() {
        return gameQuestionRepository.count();
    }

    public long getActiveQuestionsCount() {
        return gameQuestionRepository.countByIsActive(true);
    }

    public long getQuestionCountByType(QuestionType questionType) {
        return gameQuestionRepository.countByQuestionTypeAndIsActive(questionType, true);
    }

    public long getQuestionCountByDifficulty(Integer difficultyLevel) {
        return gameQuestionRepository.countByDifficultyLevelAndIsActive(difficultyLevel, true);
    }

    // Validation
    public boolean hasActiveSession(User user) {
        return gameSessionRepository.findByUserAndIsCompleted(user, false).isPresent();
    }

    public boolean canStartNewGame(User user) {
        return !hasActiveSession(user) && getActiveQuestionsCount() >= 5; // Need at least 5 questions
    }

    // Utility Methods

    /**
     * Get all incomplete game sessions (for admin monitoring)
     */
    public List<GameSession> getAllIncompleteGames() {
        return gameSessionRepository.findByUserAndIsCompletedOrderBySessionStartDesc(null, false);
    }

    /**
     * Clean up old incomplete games (can be run as scheduled task)
     */
    public void cleanupOldIncompleteGames(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        List<GameSession> oldSessions = gameSessionRepository.findBySessionStartAfterOrderBySessionStartDesc(cutoffDate);

        for (GameSession session : oldSessions) {
            if (!session.getIsCompleted()) {
                // End old incomplete games with zero points
                session.setIsCompleted(true);
                session.setSessionEnd(LocalDateTime.now());
                session.setCustomerPointsAwarded(0);
                gameSessionRepository.save(session);
            }
        }
    }

    /**
     * Force end a specific game session (admin function)
     */
    public void forceEndGameSession(Long sessionId, boolean awardPoints) {
        try {
            GameSession session = getSessionById(sessionId);

            if (!session.getIsCompleted()) {
                session.setIsCompleted(true);
                session.setSessionEnd(LocalDateTime.now());

                if (awardPoints) {
                    // Award points based on current progress
                    session.completeGame();
                    User user = session.getUser();
                    user.updateHighestScore(session.getCurrentScore());
                    user.addCustomerPoints(session.getCustomerPointsAwarded());
                    userService.updateUser(user);
                } else {
                    // No points awarded
                    session.setCustomerPointsAwarded(0);
                }

                gameSessionRepository.save(session);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to force end game session: " + e.getMessage());
        }
    }
}