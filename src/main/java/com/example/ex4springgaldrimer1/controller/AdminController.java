package com.example.ex4springgaldrimer1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("message", "Welcome to Admin Dashboard!");
        model.addAttribute("user", "Admin User");
        return "admin/dashboard"; // Returns templates/admin/dashboard.html
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("message", "User Management");
        return "admin/users"; // Returns templates/admin/users.html
    }
}