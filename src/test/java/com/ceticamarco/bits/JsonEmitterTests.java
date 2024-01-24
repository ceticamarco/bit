package com.ceticamarco.bits;

import com.ceticamarco.bits.json.JsonEmitter;
import com.ceticamarco.bits.post.Post;
import com.ceticamarco.bits.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonEmitterTests {
    private Post post;

    @BeforeEach
    public void tearUp() {
        this.post = new Post();
        var user = new User();

        this.post.setId("ABCD");
        this.post.setTitle("Hello World");
        this.post.setContent("This is a test");
        this.post.setExpirationDate(LocalDate.of(1970, 1, 1));
        user.setId("afj45k");
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("qwerty");
        this.post.setUser(user);
    }

    @Test
    public void testEmitJsonWithoutKey() {
        var expected = "{\"id\":\"ABCD\",\"title\":\"Hello World\",\"content\":\"This is a test\",\"expirationDate\":\"1970-01-01\",\"user\":{\"id\":\"afj45k\",\"username\":\"john\",\"email\":\"john@example.com\",\"password\":\"qwerty\",\"role\":null}}";

        // Convert object to JSON
        var get = new JsonEmitter<>(this.post).emitJsonKey();

        assertEquals(expected, get);
    }
}
