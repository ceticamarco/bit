package com.ceticamarco.bits.user;

import com.ceticamarco.bits.exception.GenericErrorException;
import com.ceticamarco.bits.exception.UnauthorizedUserException;
import com.ceticamarco.bits.json.JsonEmitter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get all users if user is PRIVILEGED
     *
     * @Param user the email and the password
     * @return on success, the list of users, on failure the error message
     */
    @GetMapping("/api/users")
    public ResponseEntity<List<User>> getUsers(@RequestBody User user) {
        // Check if email and password are specified
        if(user.getPassword() == null || user.getEmail() == null) {
            throw new GenericErrorException("Specify both email and password", "error");
        }

        // Get post list
        var res = userService.getUsers(user);

        // Check if user is authorized
        if(res.isLeft()) {
            throw new UnauthorizedUserException(res.getLeft().getMessage());
        }

        return new ResponseEntity<>(res.get(), HttpStatus.OK);
    }

    /**
     * Add a new user
     *
     * @param user the new user
     * @return on success, the userId, on failure the error message
     */
    @PostMapping("/api/users/new")
    public ResponseEntity<String> submitUser(@Valid @RequestBody User user) {
        var res = userService.addNewUser(user);
        if(res.isLeft()) {
            throw new GenericErrorException(res.getLeft().getMessage(), "error");
        }

        var jsonOutput = new JsonEmitter<>(res.get()).emitJsonKey("user_id");
        return new ResponseEntity<>(jsonOutput, HttpStatus.OK);
    }

    /**
     * Delete an existing user
     *
     * @param user the email and the password of the user
     * @return on failure, the error message
     */
    @DeleteMapping("/api/users/delete")
    public ResponseEntity<String> deleteUser(@RequestBody User user) {
        // Check if email and password are specified
        if(user.getPassword() == null || user.getEmail() == null) {
            throw new GenericErrorException("Specify both email and password", "error");
        }
        // Delete the user
        var res = userService.deleteUser(user);
        if(res.isPresent()) {
            throw new GenericErrorException(res.get().getMessage(), "error");
        }

        var jsonOutput = new JsonEmitter<>("OK").emitJsonKey("status");
        return new ResponseEntity<>(jsonOutput, HttpStatus.OK);
    }
}
