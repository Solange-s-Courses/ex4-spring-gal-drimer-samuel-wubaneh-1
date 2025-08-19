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
            System.out.println("=== DEBUG: Game Started ===");
            System.out.println("Created session ID: " + session.getId());
            System.out.println("Session user: " + session.getUser().getUsername());
            System.out.println("Total questions: " + session.getTotalQuestions());

            redirectAttributes.addFlashAttribute("successMessage", "New game started! Good luck!");
            return "redirect:/game/play/" + session.getId();
        } catch (Exception e) {
            System.err.println("ERROR starting game: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to start game: " + e.getMessage());
            return "redirect:/game";
        }
    }

    // Play Game - Show Current Question
    @GetMapping("/play/{sessionId}")
    public String playGame(@PathVariable Long sessionId, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            GameSession session = gameService.getSessionById(sessionId);

            System.out.println("=== DEBUG playGame ===");
            System.out.println("Session ID from URL: " + sessionId);
            System.out.println("Session object ID: " + (session != null ? session.getId() : "null"));
            System.out.println("Session user: " + (session != null && session.getUser() != null ? session.getUser().getUsername() : "null"));
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
                // No more questions available, end the game
                System.out.println("No more questions, ending game");
                gameService.endGame(session);
                return "redirect:/game/results/" + sessionId;
            }

            // IMPORTANT: Reload the session after getNextQuestion modifies it
            session = gameService.getSessionById(sessionId);

            System.out.println("After getNextQuestion - Session ID: " + (session != null ? session.getId() : "null"));
            System.out.println("Questions answered: " + session.getQuestionsAnswered());
            System.out.println("Question loaded: " + (question != null ? question.getQuestionText() : "null"));

            // Add all attributes to model
            model.addAttribute("gameSession", session);
            model.addAttribute("question", question);
            model.addAttribute("questionNumber", session.getQuestionsAnswered() + 1);

            // Verify what we're sending to template
            System.out.println("=== Model Attributes ===");
            System.out.println("session in model: " + (session != null ? "Session[id=" + session.getId() + "]" : "null"));
            System.out.println("question in model: " + (question != null ? "Question[id=" + question.getId() + "]" : "null"));

            return "game/play";

        } catch (Exception e) {
            System.err.println("ERROR in playGame: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Game session error: " + e.getMessage());
            return "redirect:/game";
        }
    }

    // Submit Answer - SIMPLIFIED VERSION
    @PostMapping("/play/answer")
    public String submitAnswer(@RequestParam Long sessionId,
                               @RequestParam Long questionId,
                               @RequestParam String answer,
                               RedirectAttributes redirectAttributes) {

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

            // Submit answer
            boolean isCorrect = gameService.submitAnswer(session, questionId, answer);

            // Get updated session
            session = gameService.getSessionById(sessionId);

            // Add flash message about the answer
            if (isCorrect) {
                redirectAttributes.addFlashAttribute("successMessage", "Correct! You earned points!");
            } else {
                GameQuestion question = gameService.getQuestionById(questionId);
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Incorrect. The correct answer was: " + question.getCorrectAnswer());
            }

            // Check if game is complete
            if (session.isGameOver()) {
                return "redirect:/game/results/" + sessionId;
            }

            // Continue to next question
            return "redirect:/game/play/" + sessionId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error submitting answer: " + e.getMessage());
            return "redirect:/game";
        }
    }

    // Game Results - FIXED VERSION
    @GetMapping("/results/{sessionId}")
    public String gameResults(@PathVariable Long sessionId, Model model, RedirectAttributes redirectAttributes) {
        System.out.println("=== DEBUG gameResults ===");
        System.out.println("Session ID: " + sessionId);

        User currentUser = userService.getCurrentUser();
        System.out.println("Current user: " + (currentUser != null ? currentUser.getUsername() : "null"));

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            GameSession session = gameService.getSessionById(sessionId);
            System.out.println("Session found: " + (session != null));

            if (session == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Game session not found.");
                return "redirect:/game";
            }

            System.out.println("Session completed: " + session.getIsCompleted());
            System.out.println("Session score: " + session.getCurrentScore());
            System.out.println("Session user: " + session.getUser().getUsername());

            // Verify session belongs to current user
            if (!session.getUser().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Access denied to this game session.");
                return "redirect:/game";
            }

            // Ensure game is completed
            if (!session.getIsCompleted()) {
                System.out.println("Completing unfinished game...");
                session = gameService.endGame(session);
            }

            // Calculate if this is a new high score - WITH NULL SAFETY
            Integer currentHighScore = currentUser.getHighestGameScore();
            Integer sessionScore = session.getCurrentScore();
            boolean isNewHighScore = false;

            if (sessionScore != null && currentHighScore != null) {
                isNewHighScore = sessionScore.equals(currentHighScore);
            }

            System.out.println("Is new high score: " + isNewHighScore);

            // Add attributes with null safety
            model.addAttribute("session", session);
            model.addAttribute("gameSession", session); // Add both for template compatibility
            model.addAttribute("isNewHighScore", isNewHighScore);

            // Add statistics with null safety
            Double userAverage = gameService.getUserAverageScore(currentUser);
            Double globalAverage = gameService.getAverageScore();

            model.addAttribute("userAverageScore", userAverage != null ? userAverage : 0.0);
            model.addAttribute("globalAverageScore", globalAverage != null ? globalAverage : 0.0);

            System.out.println("All model attributes added successfully");
            System.out.println("Returning to game/results template");

            return "game/results";

        } catch (Exception e) {
            System.err.println("ERROR in gameResults: " + e.getMessage());
            e.printStackTrace();
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

    // End Current Game (quit early) - Path Variable Version
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

    // Alternative endpoint that accepts sessionId as request parameter
    @PostMapping("/end")
    public String endGameAlternative(@RequestParam Long sessionId, RedirectAttributes redirectAttributes) {
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