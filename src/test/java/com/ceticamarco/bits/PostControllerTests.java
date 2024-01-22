package com.ceticamarco.bits;

import com.ceticamarco.bits.post.Post;
import com.ceticamarco.bits.post.PostService;
import com.ceticamarco.bits.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PostService postService;

    @Test
    public void getAllPosts() throws Exception {
        var post = new Post();
        post.setId("abc123");
        post.setTitle("test");
        post.setContent("This is a test");

        when(postService.getPosts()).thenReturn(List.of(post));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(post)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(postService, Mockito.times(1)).getPosts();
    }

    @Test
    public void getPostsById() throws Exception {
        var post = new Post();
        post.setId("abc123");
        post.setTitle("test");
        post.setContent("This is a test");

        when(postService.getPostById(anyString())).thenReturn(Either.right(any(Post.class)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/abc123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(postService, Mockito.times(1)).getPostById(anyString());
    }

    @Test
    public void getPostByTitle() throws Exception {
        var post = new Post();
        post.setId("abc123");
        post.setTitle("test");
        post.setContent("This is a test");

        when(postService.getPostByTitle(anyString())).thenReturn(List.of(post));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/bytitle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(postService, Mockito.times(1)).getPostByTitle(anyString());
    }

    @Test
    public void submitPost() throws Exception {
        var post = new Post();
        post.setTitle("test");
        post.setContent("This is a test");

        when(postService.addNewPost(any(Post.class))).thenReturn(Either.right(anyString()));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(postService, Mockito.times(1)).addNewPost(any(Post.class));
    }

    @Test
    public void updatePost() throws Exception {
        var post = new Post();
        post.setTitle("test");
        post.setContent("This is a test");

        when(postService.updatePost(any(Post.class), anyString())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/abc123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(postService, Mockito.times(1)).updatePost(any(Post.class), anyString());
    }

    @Test
    public void deletePost() throws Exception {
        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("qwerty");

        when(postService.deletePost(any(User.class), anyString())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/abc123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(postService, Mockito.times(1)).deletePost(any(User.class), anyString());
    }
}
