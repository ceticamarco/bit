package com.ceticamarco.bits.post;

import com.ceticamarco.bits.ApiResult.ApiError;
import com.ceticamarco.bits.ApiResult.ApiResult;
import com.ceticamarco.bits.ApiResult.ApiSuccess;
import com.ceticamarco.bits.json.JsonEmitter;
import com.ceticamarco.bits.user.User;
import com.ceticamarco.lambdatonic.Left;
import com.ceticamarco.lambdatonic.Right;
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
    @PostMapping("/api/posts")
    public ResponseEntity<ApiResult> getPosts(@RequestBody User user) {
        // Check if email and password are specified
        if(user.getPassword() == null || user.getEmail() == null) {
            return new ResponseEntity<>(new ApiError("Specify both email and password"), HttpStatus.OK);
        }

        // Get post list
        var result = postService.getPosts(user);
        switch (result) {
            case Left<Error, List<Post>> err -> { return new ResponseEntity<>(new ApiError(err.value().getMessage()), HttpStatus.UNAUTHORIZED); }
            case Right<Error, List<Post>> content -> { return new ResponseEntity<>(new ApiSuccess<>(content.value()), HttpStatus.OK); }
        }
    }

    /**
     * Get a single post by ID
     *
     * @param postId the ID of the requested post
     * @return the content of the post
     */
    @GetMapping("/api/posts/{postId}")
    public ResponseEntity<String> getPostById(@PathVariable("postId") String postId) {
        var result = postService.getPostById(postId);
        String jsonOutput;
        HttpStatus httpStatus;

        switch (result) {
            case Left<Error, Post> err -> {
                jsonOutput = new JsonEmitter<>(err.value().getMessage()).emitJsonKey("error");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
            case Right<Error, Post> content -> {
                jsonOutput = new JsonEmitter<>(content.value()).emitJsonKey();
                httpStatus = HttpStatus.OK;
            }
        }

        return new ResponseEntity<>(jsonOutput, httpStatus);
    }

    /**
     * Get post content by ID
     *
     * @param postId the ID of the requested post
     * @return the content of the post(raw)
     */
    @GetMapping("/api/posts/raw/{postId}")
    public ResponseEntity<String> getPostContentById(@PathVariable("postId") String postId,
                                                     @RequestHeader(value = "Accept") String acceptHeader) {
        var result = postService.getPostById(postId);

        switch(result) {
            case Left<Error, Post> err -> { return new ResponseEntity<>(err.value().getMessage(), HttpStatus.BAD_REQUEST); }
            case Right<Error, Post> value -> {
                var content = value.value().getContent();

                // Format response according to the client type(browsers or cli clients)
                if(acceptHeader.contains("text/html")) {
                    content = content.replaceAll("<", "&lt;");
                    content = content.replaceAll(">", "&gt;");
                    content = "<pre>" + content + "</pre>";
                }

                return new ResponseEntity<>(content, HttpStatus.OK);
            }
        }
    }

    /**
     * Get posts by title if user is PRIVILEGED
     *
     * @param req the body contains the title.
     *            Without the title, it acts the same as 'GET /posts'
     * @return the list of posts
     */
    @PostMapping("/api/posts/bytitle")
    public ResponseEntity<ApiResult> getPostByTitle(@RequestBody Post req) {
        // Check if email and password are specified
        if(req.getUser() == null || req.getUser().getPassword() == null || req.getUser().getEmail() == null) {
            return new ResponseEntity<>(new ApiError("Specify both email and password"), HttpStatus.BAD_REQUEST);
        }

        // Get post by title
        var result = postService.getPostByTitle(req);

        switch (result) {
            case Left<Error, List<Post>> err -> { return new ResponseEntity<>(new ApiError(err.value().getMessage()), HttpStatus.BAD_REQUEST); }
            case Right<Error, List<Post>> content -> { return new ResponseEntity<>(new ApiSuccess<>(content.value()), HttpStatus.OK); }
        }
    }

    /**
     * Add a new post
     *
     * @param post the new post to be submitted
     * @return on success the new postId, on failure the error message
     */
    @PostMapping("/api/posts/new")
    public ResponseEntity<String> submitPost(@Valid @RequestBody Post post) {
        var result =postService.addNewPost(post);
        String jsonOutput;
        HttpStatus httpStatus;

        switch (result) {
            case Left<Error, String> err -> {
                jsonOutput = new JsonEmitter<>(err.value().getMessage()).emitJsonKey("error");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
            case Right<Error, String> content -> {
                jsonOutput = new JsonEmitter<>(content.value()).emitJsonKey("post_id");
                httpStatus = HttpStatus.OK;
            }
        }

        return new ResponseEntity<>(jsonOutput, httpStatus);
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
        if(post.getUser() == null || post.getUser().getEmail() == null || post.getUser().getPassword() == null) {
            var jsonOutput = new JsonEmitter<>("Email or password not provided").emitJsonKey("error");
            return new ResponseEntity<>(jsonOutput, HttpStatus.BAD_REQUEST);
        }
        // Update post
        var result = postService.updatePost(post, postId);

        if(result.isPresent()) {
            var jsonOutput = new JsonEmitter<>(result.get().getMessage()).emitJsonKey("error");
            return new ResponseEntity<>(jsonOutput, HttpStatus.BAD_REQUEST);
        }

        var jsonOutput = new JsonEmitter<>("OK").emitJsonKey("status");
        return new ResponseEntity<>(jsonOutput, HttpStatus.OK);
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
            var jsonOutput = new JsonEmitter<>("Specify both email and password").emitJsonKey("error");
            return new ResponseEntity<>(jsonOutput, HttpStatus.BAD_REQUEST);
        }

        // Delete the post
        var res = postService.deletePost(user, postId);
        if(res.isPresent()) {
            var jsonOutput = new JsonEmitter<>(res.get().getMessage()).emitJsonKey("error");
            return new ResponseEntity<>(jsonOutput, HttpStatus.BAD_REQUEST);
        }

        var jsonOutput = new JsonEmitter<>("OK").emitJsonKey("status");
        return new ResponseEntity<>(jsonOutput, HttpStatus.OK);
    }
}
