package com.example.ex4springgaldrimer1.repository;

import com.example.ex4springgaldrimer1.entity.User;
import com.example.ex4springgaldrimer1.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Authentication queries
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    // Validation queries
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Admin queries
    List<User> findByRoleOrderByCustomerPointsDesc(Role role);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.customerPoints >= :minPoints")
    List<User> findTopCustomers(@Param("role") Role role, @Param("minPoints") Integer minPoints);

    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER' ORDER BY u.customerPoints DESC")
    Page<User> findTopCustomersByPoints(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER' ORDER BY u.highestGameScore DESC")
    Page<User> findTopCustomersByGameScore(Pageable pageable);

    // Search queries
    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER' AND " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<User> searchCustomers(@Param("search") String search);

    // Statistics queries
    long countByRole(Role role);

    @Query("SELECT AVG(u.customerPoints) FROM User u WHERE u.role = 'CUSTOMER'")
    Double getAverageCustomerPoints();

    @Query("SELECT MAX(u.highestGameScore) FROM User u WHERE u.role = 'CUSTOMER'")
    Integer getHighestGameScore();
}