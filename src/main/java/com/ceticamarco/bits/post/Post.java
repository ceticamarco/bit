package com.ceticamarco.bits.post;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.NonNull;

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
    @NonNull private String id;

    @Column(name = "title", nullable = false)
    @NonNull private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    @NonNull private String content;

    @Column(name = "created_at", nullable = false)
    @NonNull private LocalDate createdAt;

    @Column(name = "expiration_date", nullable = true)
    @NonNull private LocalDate expirationDate;

    public Post(String id, String title, String content, LocalDate createdAt, LocalDate expirationDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.expirationDate = expirationDate;
    }
}
