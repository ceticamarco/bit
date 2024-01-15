package com.ceticamarco.bits;

import com.ceticamarco.bits.user.User;
import com.ceticamarco.bits.user.UserService;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private UserService userService;

	@Test
	public void addNewUser() throws Exception {
		var user = new User();
		user.setUsername("john");
		user.setEmail("john@example.com");
		user.setPassword("qwerty");

		when(userService.addNewUser(any(User.class))).thenReturn(Either.right("userId123"));

		mockMvc.perform(MockMvcRequestBuilders.post("/users/new")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user)))
				.andExpect(MockMvcResultMatchers.status().isOk());

		Mockito.verify(userService, Mockito.times(1)).addNewUser(any(User.class));
	}

	@Test
	public void deleteExistingUser() throws Exception {
		var user = new User();
		user.setEmail("john@example.com");
		user.setPassword("qwerty");

		when(userService.deleteUser(any(User.class))).thenReturn(Optional.empty());

		mockMvc.perform(MockMvcRequestBuilders.delete("/users/delete")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user)))
				.andExpect(MockMvcResultMatchers.status().isOk());

		Mockito.verify(userService, Mockito.times(1)).deleteUser(any(User.class));
	}
}
