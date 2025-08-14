package com.example.ex4springgaldrimer1.controller;

import com.example.ex4springgaldrimer1.entity.User;
import com.example.ex4springgaldrimer1.enums.Role;
import com.example.ex4springgaldrimer1.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password!");
        }

        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been logged out successfully!");
        }

        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("message", "Create a new account");
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult,
                               @RequestParam("confirmPassword") String confirmPassword,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Please fix the errors below");
            return "register";
        }

        // Check if passwords match
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("user", user);
            model.addAttribute("passwordError", "Passwords do not match");
            return "register";
        }

        try {
            // Check if username already exists
            if (!userService.isUsernameAvailable(user.getUsername())) {
                model.addAttribute("user", user);
                model.addAttribute("usernameError", "Username already exists");
                return "register";
            }

            // Check if email already exists
            if (!userService.isEmailAvailable(user.getEmail())) {
                model.addAttribute("user", user);
                model.addAttribute("emailError", "Email already exists");
                return "register";
            }

            // Register the user as CUSTOMER
            userService.registerCustomer(user.getUsername(), user.getEmail(), user.getPassword());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration successful! You can now login with your credentials.");

            return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("user", user);
            model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "register";
        }
    }
}