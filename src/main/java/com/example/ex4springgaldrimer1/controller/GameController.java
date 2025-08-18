package com.example.ex4springgaldrimer1.controller;

import com.example.ex4springgaldrimer1.entity.GameQuestion;
import com.example.ex4springgaldrimer1.entity.GameSession;
import com.example.ex4springgaldrimer1.entity.User;
import com.example.ex4springgaldrimer1.service.GameService;
import com.example.ex4springgaldrimer1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    // Game Home Page
    @GetMapping
    public String gameHome(Model model) {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return "redirect:/login";
        }

        // Check if user has an active session
        Optional<GameSession> activeSession = gameService.getCurrentSession(currentUser);

        // Game statistics
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("hasActiveSession", activeSession.isPresent());
        model.addAttribute("canStartNewGame", gameService.canStartNewGame(currentUser));
        model.addAttribute("totalQuestions", gameService.getActiveQuestionsCount());
        model.addAttribute("userGamesPlayed", gameService.getTotalGamesPlayedByUser(currentUser));
        model.addAttribute("userHighestScore", gameService.getUserHighestScore(currentUser));
        model.addAttribute("userAverageScore", gameService.getUserAverageScore(currentUser));
        model.addAttribute("globalHighestScore", gameService.getHighestScore());

        // Recent game history
        List<GameSession> recentGames = gameService.getUserGameHistory(currentUser);
        model.addAttribute("recentGames", recentGames.stream().limit(5).toList());

        // Leaderboard (top 10)
        Page<GameSession> topScores = gameService.getTopScores(10);
        model.addAttribute("topScores", topScores.getContent());

        if (activeSession.isPresent()) {
            model.addAttribute("activeSession", activeSession.get());
        }

        return "game/home";
    }

    // Start New Game
    @PostMapping("/start")
    public String startGame(@RequestParam(value = "totalQuestions", defaultValue = "10") int totalQuestions,
                            RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!currentUser.isCustomer()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only customers can play the loyalty game!");
            return "redirect:/game";
        }

        if (!gameService.canStartNewGame(currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot start a new game. You may have an active session or insufficient questions available.");
            return "redirect:/game";
        }

        try {
            GameSession session = gameService.startNewGame(currentUser, totalQuestions);
            redirectAttributes.addFlashAttribute("successMessage", "New game started! Good luck!");
            return "redirect:/game/play/" + session.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to start game: " + e.getMessage());
            return "redirect:/game";
        }
    }

    // Play Game - Show Current Question
    @GetMapping("/play/{sessionId}")
    public String playGame(@PathVariable Long sessionId, Model model, RedirectAttributes redirectAttributes) {
        System.out.println("=== DEBUG: Play Game ===");
        System.out.println("Session ID: " + sessionId);

        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            GameSession session = gameService.getSessionById(sessionId);
            System.out.println("Found session: " + session.getId());
            System.out.println("Session user: " + session.getUser().getUsername());
            System.out.println("Current user: " + currentUser.getUsername());

            // Verify session belongs to current user
            if (!session.getUser().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Access denied to this game session.");
                return "redirect:/game";
            }

            // Check if game is already completed
            if (session.isGameOver()) {
                System.out.println("Game is over, redirecting to results");
                return "redirect:/game/results/" + sessionId;
            }

            // Get next question
            System.out.println("Getting next question...");
            GameQuestion question = gameService.getNextQuestion(session);

            if (question == null) {
                System.out.println("No more questions, redirecting to results");
                return "redirect:/game/results/" + sessionId;
            }

            System.out.println("Question loaded: " + question.getQuestionText());
            System.out.println("Session questions answered: " + session.getQuestionsAnswered());
            System.out.println("Session total questions: " + session.getTotalQuestions());

            model.addAttribute("session", session);
            model.addAttribute("question", question);
            model.addAttribute("questionNumber", session.getQuestionsAnswered() + 1);

            System.out.println("=== DEBUG: Returning play template ===");
            return "game/play";

        } catch (Exception e) {
            System.out.println("ERROR in playGame: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Game session error: " + e.getMessage());
            return "redirect:/game";
        }
    }

    // Submit Answer - IMPROVED VERSION
    @PostMapping("/play/answer")
    public String submitAnswer(@RequestParam(value = "sessionId", required = false) String sessionIdStr,
                               @RequestParam(value = "questionId", required = false) String questionIdStr,
                               @RequestParam(value = "answer", required = false) String answer,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        System.out.println("=== DEBUG: Submit Answer ===");
        System.out.println("Session ID String: '" + sessionIdStr + "'");
        System.out.println("Question ID String: '" + questionIdStr + "'");
        System.out.println("Answer: '" + answer + "'");

        // Check for null or empty parameters
        if (sessionIdStr == null || sessionIdStr.trim().isEmpty()) {
            System.out.println("ERROR: Session ID is null or empty");
            redirectAttributes.addFlashAttribute("errorMessage", "Session ID is missing");
            return "redirect:/game";
        }

        if (questionIdStr == null || questionIdStr.trim().isEmpty()) {
            System.out.println("ERROR: Question ID is null or empty");
            redirectAttributes.addFlashAttribute("errorMessage", "Question ID is missing");
            return "redirect:/game";
        }

        if (answer == null || answer.trim().isEmpty()) {
            System.out.println("ERROR: Answer is null or empty");
            redirectAttributes.addFlashAttribute("errorMessage", "Answer is missing");
            return "redirect:/game";
        }

        // Parse IDs
        Long sessionId;
        Long questionId;

        try {
            sessionId = Long.parseLong(sessionIdStr.trim());
            questionId = Long.parseLong(questionIdStr.trim());
            System.out.println("Parsed Session ID: " + sessionId);
            System.out.println("Parsed Question ID: " + questionId);
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Cannot parse IDs - " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid session or question ID");
            return "redirect:/game";
        }

        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            System.out.println("ERROR: No current user");
            return "redirect:/login";
        }

        System.out.println("Current user: " + currentUser.getUsername());

        try {
            GameSession session = gameService.getSessionById(sessionId);
            System.out.println("Found session: " + session.getId());
            System.out.println("Session questions answered: " + session.getQuestionsAnswered());
            System.out.println("Session current score: " + session.getCurrentScore());

            // Verify session belongs to current user
            if (!session.getUser().getId().equals(currentUser.getId())) {
                System.out.println("ERROR: Session doesn't belong to user");
                redirectAttributes.addFlashAttribute("errorMessage", "Access denied to this game session.");
                return "redirect:/game";
            }

            // Submit answer
            System.out.println("Submitting answer...");
            boolean isCorrect = gameService.submitAnswer(session, questionId, answer);
            System.out.println("Answer is correct: " + isCorrect);

            GameQuestion question = gameService.getQuestionById(questionId);
            System.out.println("Question loaded: " + question.getQuestionText());

            // Reload session to get updated values
            session = gameService.getSessionById(sessionId);
            System.out.println("Updated session - Questions answered: " + session.getQuestionsAnswered());
            System.out.println("Updated session - Current score: " + session.getCurrentScore());
            System.out.println("Updated session - Is completed: " + session.getIsCompleted());

            // Show answer feedback - SIMPLE VERSION
            model.addAttribute("session", session);
            model.addAttribute("question", question);
            model.addAttribute("userAnswer", answer);
            model.addAttribute("isCorrect", isCorrect);
            model.addAttribute("correctAnswer", question.getCorrectAnswer());
            model.addAttribute("pointsEarned", isCorrect ? question.getPoints() : 0);

            System.out.println("=== DEBUG: Returning simple answer template ===");
            return "game/simple-answer";

        } catch (Exception e) {
            System.out.println("ERROR in submitAnswer: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error submitting answer: " + e.getMessage());
            return "redirect:/game";
        }
    }

    // Game Results
    @GetMapping("/results/{sessionId}")
    public String gameResults(@PathVariable Long sessionId, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            GameSession session = gameService.getSessionById(sessionId);

            // Verify session belongs to current user
            if (!session.getUser().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Access denied to this game session.");
                return "redirect:/game";
            }

            // Ensure game is completed
            if (!session.getIsCompleted()) {
                session = gameService.endGame(session);
            }

            model.addAttribute("session", session);
            model.addAttribute("isNewHighScore", session.getCurrentScore().equals(currentUser.getHighestGameScore()));
            model.addAttribute("userAverageScore", gameService.getUserAverageScore(currentUser));
            model.addAttribute("globalAverageScore", gameService.getAverageScore());

            return "game/results";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error loading results: " + e.getMessage());
            return "redirect:/game";
        }
    }

    // Continue Game (if user has active session)
    @GetMapping("/continue")
    public String continueGame(RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<GameSession> activeSession = gameService.getCurrentSession(currentUser);

        if (activeSession.isPresent()) {
            return "redirect:/game/play/" + activeSession.get().getId();
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No active game session found.");
            return "redirect:/game";
        }
    }

    // End Current Game (quit early)
    @PostMapping("/end/{sessionId}")
    public String endGame(@PathVariable Long sessionId, RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            GameSession session = gameService.getSessionById(sessionId);

            // Verify session belongs to current user
            if (!session.getUser().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Access denied to this game session.");
                return "redirect:/game";
            }

            gameService.endGame(session);
            redirectAttributes.addFlashAttribute("successMessage", "Game ended. Your progress has been saved!");
            return "redirect:/game/results/" + sessionId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error ending game: " + e.getMessage());
            return "redirect:/game";
        }
    }

    // Leaderboard
    @GetMapping("/leaderboard")
    public String leaderboard(@RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "20") int size,
                              Model model) {

        Page<GameSession> topScores = gameService.getTopScores(size);
        List<GameSession> recentGames = gameService.getRecentGames(7); // Last 7 days

        model.addAttribute("topScores", topScores.getContent());
        model.addAttribute("recentGames", recentGames.stream().limit(10).toList());
        model.addAttribute("totalGames", gameService.getTotalGamesPlayed());
        model.addAttribute("averageScore", gameService.getAverageScore());
        model.addAttribute("highestScore", gameService.getHighestScore());

        return "game/leaderboard";
    }

    // User Game History
    @GetMapping("/history")
    public String gameHistory(Model model) {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return "redirect:/login";
        }

        List<GameSession> gameHistory = gameService.getUserGameHistory(currentUser);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("gameHistory", gameHistory);
        model.addAttribute("totalGames", gameService.getTotalGamesPlayedByUser(currentUser));
        model.addAttribute("averageScore", gameService.getUserAverageScore(currentUser));
        model.addAttribute("highestScore", gameService.getUserHighestScore(currentUser));

        return "game/history";
    }
}