package com.ceticamarco.bits.post;

import com.ceticamarco.bits.user.User;
import com.ceticamarco.bits.user.UserRepository;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PostService(UserRepository userRepository, PostRepository postRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private boolean isUserRegistered(User user) {
        var encodedPassword = userRepository.findPasswordByEmail(user.getEmail());
        var rawPassword = user.getPassword();

        // Return true if user email exists and the password matches
        return encodedPassword.filter(s -> passwordEncoder.matches(rawPassword, s)).isPresent();
    }

    List<Post> getPosts() {
        return postRepository.findAll().stream().map(post -> {
            if(post.getUser() != null) {
                post.getUser().setId(null);
                post.getUser().setPassword(null);
            }
            return post;
        }).collect(Collectors.toList());
    }

    Either<Error, Post> getPost(String postId) {
        Optional<Post> post = postRepository.findById(postId);

        // Check whether the post exists or not
        if(post.isEmpty()) {
            return Either.left(new Error("Cannot find post"));
        }

        // Conceal personal user information if available
        if(post.get().getUser() != null) {
            post.get().getUser().setId(null);
            post.get().getUser().setPassword(null);
        }

        return Either.right(post.get());

    }

    Either<Error, String> addNewPost(Post post) {
        // Check whether the user email and user password are specified
        if(post.getUser() != null && post.getUser().getEmail() != null && post.getUser().getPassword() != null) {
            // Then check if user is registered
            if(!isUserRegistered(post.getUser())) {
                return Either.left(new Error("Wrong email or password"));
            }
            // Retrieve the user by its email
            var fetchedUser = userRepository.findUserByEmail(post.getUser().getEmail());
            fetchedUser.ifPresent(post::setUser);
        } else {
            // Otherwise save the post without user information(i.e., anonymously)
            post.setUser(null);
        }

        // Get current date in YYYY-MM-DD format
        post.setCreatedAt(LocalDate.now());

        // Save the post into the database and return its ID
        var postId = postRepository.save(post).getId();

        return Either.right(postId);
    }
}
