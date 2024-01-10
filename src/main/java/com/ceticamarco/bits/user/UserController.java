package com.ceticamarco.bits.user;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        var errors = new HashMap<String, String>();

        ex.getBindingResult().getAllErrors().forEach((e) -> {
            var fieldName = ((FieldError) e).getField();
            var errMessage = e.getDefaultMessage();
            errors.put(fieldName, errMessage);
        });

        return errors;
    }

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Add a new user
     *
     * @param user the new user
     * @return on success, the userId, on failure the error message
     */
    @PostMapping("/users")
    public ResponseEntity<String> submitUser(@Valid @RequestBody User user) {
        var res = userService.addNewUser(user);

        return res.isRight()
                ? new ResponseEntity<>(res.get(), HttpStatus.OK)
                : new ResponseEntity<>(res.getLeft().getMessage(), HttpStatus.BAD_REQUEST);
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
