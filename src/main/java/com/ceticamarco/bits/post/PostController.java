package com.ceticamarco.bits.post;

import com.ceticamarco.bits.exception.UnauthorizedUserException;
import com.ceticamarco.bits.json.JsonEmitter;
import com.ceticamarco.bits.user.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * Get the list of all posts if user is PRIVILEGED
     *
     * @return the list of the posts
     */
    @GetMapping("/api/posts")
    public ResponseEntity<List<Post>> getPosts(@RequestBody User user) {
        // Check if email and password are specified
        if(user.getPassword() == null || user.getEmail() == null) {
            throw new UnauthorizedUserException("Specify both email and password");
        }

        // Get post list
        var res = postService.getPosts(user);

        // Check if user is authorized
        if(res.isLeft()) { // TODO: implement proper generic exception handler
            throw new UnauthorizedUserException(res.getLeft().getMessage());
        }

        return new ResponseEntity<>(res.get(), HttpStatus.OK);
    }

    /**
     * Get a single post by ID
     *
     * @param postId the ID of the requested post
     * @return the content of the post
     */
    @GetMapping("/api/posts/{postId}")
    public ResponseEntity<String> getPostById(@PathVariable("postId") String postId) {
        var res = postService.getPostById(postId)
                .map(post -> new JsonEmitter<>(post).emitJsonKey())
                .swap()
                .map(error -> new JsonEmitter<>(error.getMessage()).emitJsonKey("error"))
                .swap();

        return res.isRight()
                ? new ResponseEntity<>(res.get(), HttpStatus.OK)
                :  new ResponseEntity<>(res.getLeft(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Get posts by title if user is PRIVILEGED
     *
     * @param req the body contains the title.
     *            Without the title, it acts the same as 'GET /posts'
     * @return the list of posts
     */
    @GetMapping("/api/posts/bytitle")
    public ResponseEntity<List<Post>> getPostByTitle(@RequestBody Post req) {
        // Check if email and password are specified
        if(req.getUser() == null || req.getUser().getPassword() == null || req.getUser().getEmail() == null) {
            throw new UnauthorizedUserException("Specify both email and password");
        }

        // Get post by title
        var res = postService.getPostByTitle(req);

        // Check if user is authorized
        if(res.isLeft()) { // TODO: implement proper generic exception handler
            throw new UnauthorizedUserException(res.getLeft().getMessage());
        }

        return new ResponseEntity<>(res.get(), HttpStatus.OK);
    }

    /**
     * Add a new post
     *
     * @param post the new post to be submitted
     * @return on success the new postId, on failure the error message
     */
    @PostMapping("/api/posts/new")
    public ResponseEntity<String> submitPost(@Valid @RequestBody Post post) {
        var res = postService.addNewPost(post)
                .map(postId -> new JsonEmitter<>(postId).emitJsonKey("post_id"))
                .swap()
                .map(error -> new JsonEmitter<>(error.getMessage()).emitJsonKey("error"))
                .swap();

        return res.isRight()
                ? new ResponseEntity<>(res.get(), HttpStatus.OK)
                :  new ResponseEntity<>(res.getLeft(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Update a post
     *
     * @param post the post to update
     * @param postId the id of the post to update
     * @return on failure, the error message.
     */
    @PutMapping("/api/posts/{postId}")
    public ResponseEntity<String> updatePost(@Valid @RequestBody Post post, @PathVariable("postId") String postId) {
        var res = postService.updatePost(post, postId);

        return res.map(error -> {
            var jsonOutput = new JsonEmitter<>(res.get().getMessage()).emitJsonKey("error");
            return new ResponseEntity<>(jsonOutput, HttpStatus.BAD_REQUEST);
        }).orElseGet(() -> {
            var jsonOutput = new JsonEmitter<String>("OK").emitJsonKey("status");
            return new ResponseEntity<>(jsonOutput, HttpStatus.OK);
        });
    }

    /**
     * Delete a post
     *
     * @param user the username and the password of the post owner
     * @param postId the post ID to delete
     * @return on failure, the error message
     */
    @DeleteMapping("/api/posts/{postId}")
    public ResponseEntity<String> deletePost(@RequestBody User user, @PathVariable("postId") String postId) {
        // Check if email and password are specified
        if(user.getPassword() == null || user.getEmail() == null) {
            var res = new JsonEmitter<>("Specify both email and password").emitJsonKey("error");
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

        // Delete the post
        var res = postService.deletePost(user, postId);
        return res.map(error -> {
            var jsonOutput = new JsonEmitter<>(error.getMessage()).emitJsonKey("error");
            return new ResponseEntity<>(jsonOutput, HttpStatus.BAD_REQUEST);
        }).orElseGet(() -> {
            var jsonOutput = new JsonEmitter<>("OK").emitJsonKey("status");
            return new ResponseEntity<>(jsonOutput, HttpStatus.OK);
        });
    }
}
