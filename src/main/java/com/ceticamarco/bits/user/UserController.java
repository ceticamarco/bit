package com.ceticamarco.bits.user;

import com.ceticamarco.bits.json.JsonEmitter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {
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
    @PostMapping("/api/users/new")
    public ResponseEntity<String> submitUser(@Valid @RequestBody User user) {
        var res = userService.addNewUser(user)
                .map(userId -> new JsonEmitter<>(userId).emitJsonKey("user_id"))
                .swap()
                .map(error -> new JsonEmitter<>(error.getMessage()).emitJsonKey("error"))
                .swap();

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
    @DeleteMapping("/api/users/delete")
    public ResponseEntity<String> deleteUser(@RequestBody User user) {
        // Check if email and password are specified
        if(user.getPassword() == null || user.getEmail() == null) {
            var res = new JsonEmitter<>("Specify both email and password").emitJsonKey("error");
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
        // Delete the user
        var res = userService.deleteUser(user);
        return res.map(error -> {
            var jsonOutput = new JsonEmitter<>(error.getMessage()).emitJsonKey("error");
            return new ResponseEntity<>(jsonOutput, HttpStatus.BAD_REQUEST);
        }).orElseGet(() -> {
            var jsonOutput = new JsonEmitter<>("OK").emitJsonKey("status");
            return new ResponseEntity<>(jsonOutput, HttpStatus.OK);
        });
    }
}
