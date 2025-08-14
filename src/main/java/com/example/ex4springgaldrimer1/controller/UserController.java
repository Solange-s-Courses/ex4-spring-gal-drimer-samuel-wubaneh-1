package com.example.ex4springgaldrimer1.controller;

import com.example.ex4springgaldrimer1.entity.User;
import com.example.ex4springgaldrimer1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String userProfile(Model model) {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("message", "Welcome to your profile, " + currentUser.getUsername() + "!");

        // Add some statistics
        model.addAttribute("totalCustomers", userService.getTotalCustomers());
        model.addAttribute("averagePoints", userService.getAverageCustomerPoints());
        model.addAttribute("highestScore", userService.getHighestGameScore());

        return "customer/profile";
    }
}