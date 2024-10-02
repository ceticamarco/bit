package com.ceticamarco.bits.user;

import com.ceticamarco.bits.ApiResult.ApiError;
import com.ceticamarco.bits.ApiResult.ApiResult;
import com.ceticamarco.bits.ApiResult.ApiSuccess;
import com.ceticamarco.bits.json.JsonEmitter;
import com.ceticamarco.lambdatonic.Left;
import com.ceticamarco.lambdatonic.Right;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
public class UserController {
    private final UserService userService;

    /**
     * Check if user registration is disabled or not by reading the
     * 'BIT_DISABLE_SIGNUP' environment variable.
     *
     * @return true if 'BIT_DISABLE_SIGNUP' is equal to 1, false otherwise
     */
    private boolean isSignupDisabled() {
        var env_var = System.getenv("BIT_DISABLE_SIGNUP");

        return Objects.equals(env_var, "1");
    }

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get all users if user is PRIVILEGED
     *
     * @param user the email and the password
     * @return on success, the list of users, on failure the error message
     */
    @PostMapping("/api/users")
    public ResponseEntity<ApiResult> getUsers(@RequestBody User user) {
        // Check if email and password are specified
        if(user.getPassword() == null || user.getEmail() == null) {
            return new ResponseEntity<>(new ApiError("Specify both email and password"), HttpStatus.BAD_REQUEST);
        }

        // Get post list
        var result = userService.getUsers(user);
        switch (result) {
            case Left<Error, List<User>> err -> { return new ResponseEntity<>( new ApiError(err.value().getMessage()), HttpStatus.BAD_REQUEST); }
            case Right<Error, List<User>> content -> { return new ResponseEntity<>(new ApiSuccess<>(content.value()), HttpStatus.OK); }
        }
    }

    /**
     * Add a new user
     *
     * @param user the new user
     * @return on success, the userId, on failure the error message
     */
    @PostMapping("/api/users/new")
    public ResponseEntity<String> submitUser(@Valid @RequestBody User user) {
        // Check if user registration is disabled
        if(isSignupDisabled()) {
            var jsonOutput = new JsonEmitter<>("Registration is disabled").emitJsonKey("error");
            return new ResponseEntity<>(jsonOutput, HttpStatus.BAD_REQUEST);
        }

        var result = userService.addNewUser(user);
        switch (result) {
            case Left<Error, String> err -> {
                var jsonOutput = new JsonEmitter<>(err.value().getMessage()).emitJsonKey("error");
                return new ResponseEntity<>(jsonOutput, HttpStatus.BAD_REQUEST);
            }
            case Right<Error, String> content -> {
                var jsonOutput = new JsonEmitter<>(content.value()).emitJsonKey("user_id");
                return new ResponseEntity<>(jsonOutput, HttpStatus.OK);
            }
        }
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
            var jsonOutput = new JsonEmitter<>("Specify both email and password").emitJsonKey("error");
            return new ResponseEntity<>(jsonOutput, HttpStatus.BAD_REQUEST);
        }
        // Delete the user
        var res = userService.deleteUser(user);
        if(res.isPresent()) {
            var jsonOutput = new JsonEmitter<>(res.get().getMessage()).emitJsonKey("error");
            return new ResponseEntity<>(jsonOutput, HttpStatus.BAD_REQUEST);
        }

        var jsonOutput = new JsonEmitter<>("OK").emitJsonKey("status");
        return new ResponseEntity<>(jsonOutput, HttpStatus.OK);
    }
}
