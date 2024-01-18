package com.ceticamarco.bits.post;

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
     * Get the list of all posts
     *
     * @return the list of the posts
     */
    @CrossOrigin
    @GetMapping("/api/posts")
    public ResponseEntity<List<Post>> getPosts() {
        return new ResponseEntity<>(postService.getPosts(), HttpStatus.OK);
    }

    /**
     * Get a single post by ID
     *
     * @param postId the ID of the requested post
     * @return the content of the post
     */
    @CrossOrigin
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
     * Get posts by title
     *
     * @param req the body contains the title.
     *            Without the title, it acts the same as 'GET /posts'
     * @return the list of posts
     */
    @CrossOrigin
    @GetMapping("/api/posts/bytitle")
    public ResponseEntity<List<Post>> getPostByTitle(@RequestBody Post req) {
        return new ResponseEntity<>(postService.getPostByTitle(req.getTitle()), HttpStatus.OK);
    }

    /**
     * Add a new post
     *
     * @param post the new post to be submitted
     * @return on success the new postId, on failure the error message
     */
    @CrossOrigin
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
    @CrossOrigin
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
    @CrossOrigin
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
