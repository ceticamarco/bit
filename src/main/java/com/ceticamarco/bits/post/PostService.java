package com.ceticamarco.bits.post;

import com.ceticamarco.bits.user.User;
import com.ceticamarco.bits.user.UserRepository;
import io.vavr.control.Either;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    private boolean isUserAuthorized(User user) {
        var userSearch = userRepository.findUserByEmail(user.getEmail());
        var rawPassword = user.getPassword();

        // Check whether user exists and whether its password is correct
        if(userSearch.filter(s -> passwordEncoder.matches(rawPassword, s.getPassword())).isEmpty()) {
            return false;
        }

        // Check whether user is authorized
        return userSearch.get().getRole() == User.UserRole.PRIVILEGED;
    }

    @Autowired
    public PostService(UserRepository userRepository, PostRepository postRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Either<Error, List<Post>> getPosts(User user) {
        // Check if user exists, credentials are correct and the role is 'PRIVILEGED'
        if(!isUserAuthorized(user)) {
            return Either.left(new Error("Wrong credentials or insufficient privileges"));
        }

        // Otherwise, retrieve all posts and filter out expired one
        return Either.right(postRepository.findAll().stream().map(post -> {
            if(post.getUser() != null) {
                post.getUser().setId(null);
                post.getUser().setPassword(null);
            }
            return post;
        })
            .filter(post -> post.getExpirationDate() == null || post.getExpirationDate().isAfter(LocalDate.now()))
            .collect(Collectors.toList()));
    }

    public Either<Error, Post> getPostById(String postId) {
        var post = postRepository.findById(postId);

        // Check whether the post exists
        if(post.isEmpty()) {
            return Either.left(new Error("Cannot find post"));
        }

        // Check if post is expired
        if(post.get().getExpirationDate() != null && !post.get().getExpirationDate().isAfter(LocalDate.now())) {
            return Either.left(new Error("This post has expired"));
        }

        // Conceal personal user information if available
        if(post.get().getUser() != null) {
            post.get().getUser().setId(null);
            post.get().getUser().setPassword(null);
        }

        return Either.right(post.get());
    }

    public Either<Error, List<Post>> getPostByTitle(Post post) {
        // Check if user exists, credentials are correct and the role is 'PRIVILEGED'
        if(!isUserAuthorized(post.getUser())) {
            return Either.left(new Error("Wrong credentials or insufficient privileges"));
        }

        // Otherwise, retrieve all posts by title and filter out expired one
        return Either.right(postRepository.findPostByTitle(post.getTitle()).stream().map(p -> {
            // Conceal user information
            if(p.getUser() != null) {
                p.getUser().setId(null);
                p.getUser().setPassword(null);
            }

            return p;
        })
            .filter(p -> p.getExpirationDate() == null || p.getExpirationDate().isAfter(LocalDate.now()))
            .collect(Collectors.toList()));
    }

    public Either<Error, String> addNewPost(Post post) {
        // Check whether the user email and user password are specified
        if(post.getUser() != null && post.getUser().getEmail() != null && post.getUser().getPassword() != null) {
            var user = userRepository.findUserByEmail(post.getUser().getEmail());
            var rawPassword = post.getUser().getPassword();

            // Then check if user is registered
            if(user.filter(s -> passwordEncoder.matches(rawPassword, s.getPassword())).isEmpty()) {
                return Either.left(new Error("Wrong email or password"));
            }
            // Finally, link the user to the post
            user.ifPresent(post::setUser);
        } else {
            // Otherwise save the post without user information(i.e., anonymously)
            post.setUser(null);
        }

        // Save the post into the database and return its ID
        var postId = postRepository.save(post).getId();

        return Either.right(postId);
    }

    @Transactional
    public Optional<Error> updatePost(Post req, String postId) {
        var post = postRepository.findById(postId);

        // Check whether the post exists
        if(post.isEmpty()) {
            return Optional.of(new Error("Cannot find post"));
        }

        // Check whether email and password are specified in the request
        if(req.getUser() == null || req.getUser().getEmail() == null || req.getUser().getPassword() == null) {
            return Optional.of(new Error("Email or password not provided"));
        }

        // Check whether post is anonymous
        if(post.get().getUser() == null) {
            return Optional.of(new Error("Cannot modify an anonymous post"));
        }

        // Check if user is registered
        var user = userRepository.findUserByEmail(req.getUser().getEmail());
        var rawPassword = req.getUser().getPassword();
        if(user.isEmpty()) {
            return Optional.of(new Error("Cannot find this user"));
        }

        // Check if credentials are correct
        if(!passwordEncoder.matches(rawPassword, user.get().getPassword())) {
            return Optional.of(new Error("Wrong password"));
        }

        // Check if user has ownership over post
        if(!Objects.equals(user.get().getId(), post.get().getUser().getId())) {
            return Optional.of(new Error("Cannot modify this post"));
        }

        // Otherwise update both title and content
        var modifiedRows = postRepository.updatePostById(req.getTitle(), req.getContent(), postId);

        return modifiedRows != 1
                ? Optional.of(new Error("Error while updating post"))
                : Optional.empty();
    }

    @Transactional
    public Optional<Error> deletePost(User req, String postId) {
        var post = postRepository.findById(postId);

        // Check whether the post exists
        if(post.isEmpty()) {
            return Optional.of(new Error("Cannot find post"));
        }

        // Check whether post is anonymous
        if(post.get().getUser() == null) {
            return Optional.of(new Error("Cannot delete an anonymous post"));
        }

        // Check if user is registered
        var user = userRepository.findUserByEmail(req.getEmail());
        var rawPassword = req.getPassword();
        if(user.isEmpty()) {
            return Optional.of(new Error("Cannot find this user"));
        }

        // Check if credentials are correct
        if(!passwordEncoder.matches(rawPassword, user.get().getPassword())) {
            return Optional.of(new Error("Wrong password"));
        }

        // Check if user has ownership over post
        if(!Objects.equals(user.get().getId(), post.get().getUser().getId())) {
            return Optional.of(new Error("Cannot delete this post"));
        }

        // Otherwise delete the post
        var modifiedRows = postRepository.deletePostById(postId);

        return modifiedRows != 1
                ? Optional.of(new Error("Error while deleting post"))
                : Optional.empty();
    }
}
