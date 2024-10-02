package com.ceticamarco.bits.user;

import com.ceticamarco.bits.post.Post;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "bt_users")
public class User {
    public enum UserRole {
        PRIVILEGED,
        UNPRIVILEGED
    }

    @Id
    @Column(name = "userID", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotEmpty(message = "username cannot be empty")
    @Column(name = "username", nullable = false)
    private String username;

    @NotEmpty(message = "email cannot be empty")
    @Column(name = "email", nullable = false)
    private String email;

    @NotEmpty(message = "password cannot be empty")
    @Column(name = "password", nullable = false)
    private String password;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @Column(name = "role", nullable = false)
    private UserRole role;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Post> post;

    public User() {}

    public String getId() { return this.id; }

    public String getEmail() { return this.email; }

    public String getUsername() { return this.username; }

    public String getPassword() { return this.password; }

    public UserRole getRole() { return this.role; }

    public void setPassword(String password) { this.password = password; }

    public void setId(String id) { this.id = id; }

    public void setUsername(String username) { this.username = username; }

    public void setEmail(String email) { this.email = email; }

    public void setRole(UserRole userRole) { this.role = userRole; }
}
