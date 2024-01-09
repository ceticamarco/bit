package com.ceticamarco.bits.user;

import com.ceticamarco.bits.customGenerator.CustomUUID;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.NonNull;

@Entity
@Table(name = "b_users")
public class User {
    @Id
    @Column(name = "userID", nullable = false)
    @GeneratedValue(generator = "customUUID")
    @GenericGenerator(
            name = "customUUID",
            type = com.ceticamarco.bits.customGenerator.CustomUUID.class
    )
    @NonNull private String id;

    @Column(name = "username", nullable = false)
    @NonNull private String username;

    @Column(name = "email", nullable = false)
    @NonNull private String email;

    @Column(name = "password", nullable = false)
    @NonNull private String password;


    public User(String id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
