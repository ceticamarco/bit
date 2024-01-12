package com.ceticamarco.bits.user;

import io.vavr.control.Either;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Either<Error, String> addNewUser(User user) {
        // Search the user by email and by username
        var userEmail = userRepository.findUserByEmail(user.getEmail());
        var userName = userRepository.findUserByUsername(user.getUsername());

        // If they are found, return an error
        if(userEmail.isPresent() || userName.isPresent()) {
            return Either.left(new Error("Email or username already taken"));
        }

        // Hash the password
        var hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        // Otherwise save the new user into the database and return its ID
        var userId = userRepository.save(user).getId();

        return Either.right(userId);
    }

    @Transactional
    public Optional<Error> deleteUser(User user) {
        // Search user password by its email
        var rawPassword = user.getPassword();
        var encodedPassword = userRepository.findUserByEmail(user.getEmail());

        // Check whether user exists
        if(encodedPassword.isEmpty()) {
            return Optional.of(new Error("Cannot find user"));
        }

        // Otherwise compare the hash
        var isHashEqual = passwordEncoder.matches(rawPassword, encodedPassword.get().getPassword());
        if(!isHashEqual) {
            return Optional.of(new Error("Wrong password"));
        }

        var modifiedRows = userRepository.deleteUserByEmail(user.getEmail());

        return modifiedRows != 1
                ? Optional.of(new Error("Error while deleting user"))
                : Optional.empty();
    }
}
