package com.example.ex4springgaldrimer1.config;

import com.example.ex4springgaldrimer1.service.UserService;
import com.example.ex4springgaldrimer1.service.GameService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(@Lazy UserService userService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    // Custom logout handler to end active games
    @Bean
    public LogoutHandler gameSessionLogoutHandler(@Lazy GameService gameService, @Lazy UserService userService) {
        return (request, response, authentication) -> {
            if (authentication != null && authentication.isAuthenticated()) {
                try {
                    // Get the current user
                    String username = authentication.getName();
                    var user = userService.findByUsername(username);

                    if (user.isPresent()) {
                        // End any active game sessions with zero points
                        gameService.endActiveGameOnLogout(user.get());
                    }
                } catch (Exception e) {
                    // Log the error but don't prevent logout
                    System.err.println("Error ending game session on logout: " + e.getMessage());
                }
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider authenticationProvider,
                                           LogoutHandler gameSessionLogoutHandler) throws Exception {
        http
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(authz -> authz
                        // STATIC RESOURCES - MUST BE FIRST AND MOST SPECIFIC
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()

                        // Public pages - no authentication required
                        .requestMatchers("/", "/home", "/test").permitAll()
                        .requestMatchers("/register", "/login").permitAll()
                        .requestMatchers("/h2-console/**").permitAll() // For H2 database console

                        // Admin pages - require ADMIN role
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Customer/User pages - require authentication
                        .requestMatchers("/profile", "/game/**").hasAnyRole("CUSTOMER", "ADMIN")

                        // Public access to products and stores (for browsing)
                        .requestMatchers("/products/**", "/stores/**").permitAll()

                        // All other pages require authentication
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/?login=success", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(gameSessionLogoutHandler) // Add our custom logout handler
                        .logoutSuccessUrl("/?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable()) // Disable CSRF for development
                .headers(headers -> headers.disable()); // Disable all header restrictions for development

        return http.build();
    }
}