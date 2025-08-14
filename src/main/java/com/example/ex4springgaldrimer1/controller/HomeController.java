package com.example.ex4springgaldrimer1.controller;

import com.example.ex4springgaldrimer1.entity.User;
import com.example.ex4springgaldrimer1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(@RequestParam(value = "logout", required = false) String logout,
                       @RequestParam(value = "login", required = false) String login,
                       Model model) {

        model.addAttribute("message", "Welcome to Chain Store Website!");
        model.addAttribute("description", "Spring MVC + Thymeleaf is working correctly");
        model.addAttribute("student", "Gal Drimer");
        model.addAttribute("project", "Ex4 - Chain Store Network");

        // Get current user information
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");

        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            User currentUser = userService.getCurrentUser();
            if (currentUser != null) {
                model.addAttribute("currentUser", currentUser);
                model.addAttribute("isAdmin", currentUser.isAdmin());

                // Show login success message if user just logged in
                if ("success".equals(login)) {
                    model.addAttribute("loginMessage", "Logged in Successfully! " + currentUser.getUsername());
                }
            }
        }

        // Show logout message if user just logged out
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been successfully logged out!");
        }

        return "index"; // Returns templates/index.html
    }

    @GetMapping("/test")
    public String test(Model model) {
        model.addAttribute("currentTime", java.time.LocalDateTime.now());
        model.addAttribute("projectType", "Spring MVC + Thymeleaf");
        model.addAttribute("packageName", "com.example.ex4springgaldrimer1");
        return "test"; // Returns templates/test.html
    }
}