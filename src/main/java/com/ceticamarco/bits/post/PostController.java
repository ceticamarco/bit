package com.ceticamarco.bits.post;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class PostController {
    /**
     * Get the list of all posts
     *
     * @return the list of the posts
     */
    @GetMapping("/posts")
    public List<Post> getPosts() {
        return null;
    }

    /**
     * Get a single post by ID
     *
     * @param postId the ID of the requested post
     * @return the content of the post
     */
    @GetMapping("/posts/{postId}")
    public String getPost(@PathVariable("postId") Integer postId) {
        return "";
    }

    /**
     * Add a new post
     *
     * @param post the new post to be submitted
     * @return on success the new postId, on failure the error message
     */
    @PostMapping("/posts")
    public String submitPost(@RequestBody Post post) {
        return "";
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
