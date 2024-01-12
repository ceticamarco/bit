package com.ceticamarco.bits.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, String> {
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:title%")
    List<Post> findPostByTitle(@Param("title") String title);

    @Modifying
    @Query("UPDATE Post p SET p.title = :title, p.content = :content WHERE p.id = :postId")
    int updatePostById(@Param("title") String title, @Param("content") String content, @Param("postId") String postId);
}
