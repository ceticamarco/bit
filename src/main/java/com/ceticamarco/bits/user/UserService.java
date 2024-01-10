package com.ceticamarco.bits.user;

import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
            return Either.left(new Error("Email or username already taken."));
        }

        // Hash the password
        var hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        // Otherwise save the new user into the database and return its ID
        var userId = userRepository.save(user).getId();

        return Either.right(userId);
    }
}
