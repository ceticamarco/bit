package com.ceticamarco.bits.user;

import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    /**
     * Add a new user
     *
     * @param user the new user
     * @return on success, the userId, on failure the error message
     */
    @PostMapping("/users")
    public String submitUser(@RequestBody User user) {
        return "";
    }

    /**
     * Update an existing user
     *
     * @param user the user to update
     * @return on failure, the error message
     */
    @PutMapping("/users")
    public String updateUser(@RequestBody User user) {
        return "";
    }

    /**
     * Delete an existing user
     *
     * @param userId the user ID to delete
     * @return on failure, the error message
     */
    @DeleteMapping("/users/{userId}")
    public String deleteUser(@PathVariable("userId") Integer userId) {
        return "";
    }
}
