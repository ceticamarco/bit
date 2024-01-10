package com.ceticamarco.bits.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    Optional<User> findUserByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username = ?1")
    Optional<User> findUserByUsername(String username);

    @Query("SELECT u.password FROM User u WHERE u.email = ?1")
    Optional<String> findPasswordByEmail(String email);
    
    void deleteUserByEmail(String email);
}