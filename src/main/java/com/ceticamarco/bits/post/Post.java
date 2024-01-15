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

    @NotEmpty(message = "title cannot be empty")
    @Column(name = "title", nullable = false)
    private String title;

    @NotEmpty(message = "content cannot be empty")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    public Post() {}

    public String getId() { return this.id; }

    public String getTitle() { return this.title; }

    public String getContent() { return this.content; }

    public LocalDate getExpirationDate() { return this.expirationDate; }

    public User getUser() { return this.user; }

    public void setUser(User user) { this.user = user; }

    public void setCreatedAt(LocalDate date) { this.createdAt = date; }

    public void setId(String id) { this.id = id; }

    public void setTitle(String title) { this.title = title; }

    public void setContent(String content) { this.content = content; }

    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
}
