package com.ceticamarco.bits.user;

import io.vavr.control.Either;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Either<Error, String> addNewUser(User user) {
        // Search the user by its email
        var userEmail = userRepository.findUserByEmail(user.getEmail());

        // If it's found, return an error
        if(userEmail.isPresent()) {
            return Either.left(new Error("Email already exists."));
        }

        // TODO: Hash the password

        // Otherwise save the new user into the database and return its ID
        var userId = userRepository.save(user).getId();

        return Either.right(userId);
    }
}
