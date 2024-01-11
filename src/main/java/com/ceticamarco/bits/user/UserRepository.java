package com.ceticamarco.bits.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findUserByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findUserByUsername(@Param("username") String username);

    @Query("SELECT u.password FROM User u WHERE u.email = :email")
    Optional<String> findPasswordByEmail(@Param("email") String email);
    
    void deleteUserByEmail(String email);
}