package com.ceticamarco.bits.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
public class PostController {
    private final PostService postService;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationExceptions(MethodArgumentNotValidException ex) {
        var errors = new HashMap<String, String>();
        var jsonErrors = "";
        var objectMapper = new ObjectMapper();

        ex.getBindingResult().getAllErrors().forEach((e) -> {
            var fieldName = ((FieldError) e).getField();
            var errMessage = e.getDefaultMessage();
            errors.put(fieldName, errMessage);
        });

        try {
            jsonErrors = objectMapper.writeValueAsString(errors);
        } catch(JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }

        return jsonErrors;
    }

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * Get the list of all posts
     *
     * @return the list of the posts
     */
    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getPosts() {
        return new ResponseEntity<>(postService.getPosts(), HttpStatus.OK);
    }

    /**
     * Get a single post by ID
     *
     * @param postId the ID of the requested post
     * @return the content of the post
     */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<String> getPostById(@PathVariable("postId") String postId) {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        var res = postService.getPostById(postId).map(post -> {
            try {
                return objectMapper.writeValueAsString(post);
            } catch(JsonProcessingException e) {
                throw new RuntimeException(e.getMessage());
            }
        }).swap().map(error -> {
            try {
                var jsonNode = objectMapper.createObjectNode().put("error", error.getMessage());
                return objectMapper.writeValueAsString(jsonNode);
            } catch(JsonProcessingException e) {
                throw new RuntimeException(e.getMessage());
            }
        }).swap();

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
    @PostMapping("/posts")
    public ResponseEntity<List<Post>> getPostByTitle(@RequestBody Post req) {
        return new ResponseEntity<>(postService.getPostByTitle(req.getTitle()), HttpStatus.OK);
    }

    /**
     * Add a new post
     *
     * @param post the new post to be submitted
     * @return on success the new postId, on failure the error message
     */
    @PostMapping("/posts/new")
    public ResponseEntity<String> submitPost(@Valid @RequestBody Post post) {
        var objectMapper = new ObjectMapper();
        var res = postService.addNewPost(post).map(postId -> {
            try {
                var jsonNode = objectMapper.createObjectNode().put("post_id", postId);
                return objectMapper.writeValueAsString(jsonNode);
            } catch(JsonProcessingException e) {
                throw new RuntimeException(e.getMessage());
            }
        }).swap().map(error -> {
            try {
                var jsonNode = objectMapper.createObjectNode().put("error", error.getMessage());
                return objectMapper.writeValueAsString(jsonNode);
            } catch(JsonProcessingException e) {
                throw new RuntimeException(e.getMessage());
            }
        }).swap();

        return res.isRight()
                ? new ResponseEntity<>(res.get(), HttpStatus.OK)
                :  new ResponseEntity<>(res.getLeft(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Update a post
     *
     * @param post the post to update
     * @return on failure, the error message.
     */
    @PutMapping("/posts")
    public String updatePost(@RequestBody Post post) {
        return "";
    }

    /**
     * Delete a post
     *
     * @param postId the post ID to delete
     * @return on failure, the error message
     */
    @DeleteMapping("/posts/{postId}")
    public String deletePost(@PathVariable("postId") Integer postId) {
        return "";
    }
}
