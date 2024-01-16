package com.ceticamarco.bits.user;

import com.ceticamarco.bits.customGenerator.CustomUUID;
import com.ceticamarco.bits.post.Post;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Entity
@Table(name = "bt_users")
public class User {
    @Id
    @Column(name = "userID", nullable = false)
    @GeneratedValue(generator = "customUUID")
    @GenericGenerator(
            name = "customUUID",
            type = CustomUUID.class
    )
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

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Post> post;

    public User() {}

    public String getId() { return this.id; }

    public String getEmail() { return this.email; }

    public String getUsername() { return this.username; }

    public String getPassword() { return this.password; }

    public void setPassword(String password) { this.password = password; }

    public void setId(String id) { this.id = id; }

    public void setUsername(String username) { this.username = username; }

    public void setEmail(String email) { this.email = email; }
}
