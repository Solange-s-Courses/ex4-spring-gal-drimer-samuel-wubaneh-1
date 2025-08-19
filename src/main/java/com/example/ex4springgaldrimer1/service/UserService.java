package com.example.ex4springgaldrimer1.service;

import com.example.ex4springgaldrimer1.entity.User;
import com.example.ex4springgaldrimer1.enums.Role;
import com.example.ex4springgaldrimer1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Authentication methods
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole().getAuthority())
                .accountExpired(false)
                .accountLocked(!user.getEnabled())
                .credentialsExpired(false)
                .disabled(!user.getEnabled())
                .build();
    }

    // User registration
    public User registerUser(String username, String email, String password, Role role) {
        // Validate input
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        return userRepository.save(user);
    }

    // Convenience method for customer registration
    public User registerCustomer(String username, String email, String password) {
        return registerUser(username, email, password, Role.CUSTOMER);
    }

    // User management methods
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllCustomers() {
        return userRepository.findByRoleOrderByCustomerPointsDesc(Role.CUSTOMER);
    }

    public Page<User> getTopCustomersByPoints(Pageable pageable) {
        return userRepository.findTopCustomersByPoints(pageable);
    }

    public Page<User> getTopCustomersByGameScore(Pageable pageable) {
        return userRepository.findTopCustomersByGameScore(pageable);
    }

    // Get current authenticated user
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }

    // User profile updates
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void updatePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void updateCustomerPoints(String username, int points) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        user.addCustomerPoints(points);
        userRepository.save(user);
    }

    public void updateHighestGameScore(String username, int score) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        user.updateHighestScore(score);
        userRepository.save(user);
    }

    // Validation methods
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    // Admin methods
    public void enableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void disableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(false);
        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // Statistics
    public long getTotalCustomers() {
        return userRepository.countByRole(Role.CUSTOMER);
    }

    public long getTotalAdmins() {
        return userRepository.countByRole(Role.ADMIN);
    }

    public Double getAverageCustomerPoints() {
        return userRepository.getAverageCustomerPoints();
    }

    public Integer getHighestGameScore() {
        return userRepository.getHighestGameScore();
    }
    /**
     * Search customers by username or email
     */
    public List<User> searchCustomers(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllCustomers();
        }
        return userRepository.searchCustomers(searchTerm.trim());
    }
}