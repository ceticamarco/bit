package com.ceticamarco.bits.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class UserController {
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
        var objectMapper = new ObjectMapper();
        var res = userService.addNewUser(user).map(userId -> {
            try {
                var jsonNode = objectMapper.createObjectNode().put("user_id", userId);
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
                : new ResponseEntity<>(res.getLeft(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Delete an existing user
     *
     * @param user the email and the password of the user
     * @return on failure, the error message
     */
    @DeleteMapping("/users")
    public ResponseEntity<String> deleteUser(@RequestBody User user) {
        // Check if email and password are specified
        if(user.getPassword() == null || user.getEmail() == null) {
            var objectMapper = new ObjectMapper();
            var res = "";
            var jsonNode = objectMapper.createObjectNode().put("error", "Specify both email and password");
            try {
                res = objectMapper.writeValueAsString(jsonNode);
            } catch(JsonProcessingException e) {
                throw new RuntimeException(e.getMessage());
            }
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
        // Delete the user
        var res = userService.deleteUser(user);
        return res.map(error -> {
            var objectMapper = new ObjectMapper();
            var json = "";
            var jsonNode = objectMapper.createObjectNode().put("error", error.getMessage());
            try {
                json = objectMapper.writeValueAsString(jsonNode);
            } catch(JsonProcessingException e) {
                throw new RuntimeException(e.getMessage());
            }
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }).orElseGet(() -> new ResponseEntity<>("{\"status\": \"OK\"}", HttpStatus.OK));
    }
}
