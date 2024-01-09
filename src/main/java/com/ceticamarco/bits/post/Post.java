package com.ceticamarco.bits.post;

import com.ceticamarco.bits.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;

@Entity
@Table(name = "bts_posts")
public class Post {
    @Id
    @Column(name = "postID", nullable = false)
    @GeneratedValue(generator = "customUUID")
    @GenericGenerator(
            name = "customUUID",
            type = com.ceticamarco.bits.customGenerator.CustomUUID.class
    )
    private String id;

    @Column(name = "title", nullable = false)
    @NotEmpty(message = "title cannot be empty")
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    @NotEmpty(message = "content cannot be empty")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;
}
