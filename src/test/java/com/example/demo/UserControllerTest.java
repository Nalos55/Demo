package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = { SecurityAutoConfiguration.class })
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class UserControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private UserRepository repository;
	@MockBean
	private UserModelAssembler assembler;

	private static final Logger log = Logger.getLogger(UserControllerTest.class.getName());

	@BeforeAll
	static void setUp() throws IOException {
		log.info("setUp");
	}

	@AfterAll
	static void tearDown() throws IOException {
		log.info("tearDown");
	}

	@Test
	public void createMockMvc() throws Exception {
		assertTrue(repository != null, "Repository is null");
		assertTrue(assembler != null, "Assembler is null");
		assertTrue(mvc != null, "Controller is null");

		log.info("repository: " + repository.toString());
		log.info("assembler: " + assembler.toString());
	}

	private String toJson(User user) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		return ow.writeValueAsString(user);

	}

	@Test
	public void all() throws Exception {
		List<User> users = new ArrayList<>();
		users.add(new User("Mario"));
		users.add(new User("Luigi"));
		users.add(new User("Peach"));
		users.add(new User("Daisy"));

		when(repository
				.findAll())
				.thenReturn(users);

		when(assembler
				.toModel(Mockito
						.any(User.class)))
				.thenCallRealMethod();

		mvc.perform(get("/users")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print());
	}

	@Test
	public void registration() throws Exception {
		User user = new User("Mario");
		String userString = toJson(user);

		when(repository
				.save(Mockito
						.any(User.class)))
				.thenReturn(user);

		when(assembler
				.toModel(Mockito
						.any(User.class)))
				.thenCallRealMethod();

		mvc.perform(post("/users")
				.content(userString)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andDo(print());
	}

	@Test
	public void delete() throws Exception {
		List<User> users = new ArrayList<>();
		users.add(new User("Mario"));
		users.add(new User("Luigi"));
		users.add(new User("Peach"));
		users.add(new User("Daisy"));

		User user = users.get(0);
		String content = toJson(user);

		doNothing().when(repository)
				.delete(Mockito
						.any(User.class));

		mvc.perform(MockMvcRequestBuilders.delete("/users")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(print());

	}

	@Test
	public void put() throws Exception {

		List<User> users = new ArrayList<>();
		User user = new User("Mario");
		users.add(user);
		user = new User("Luigi");
		users.add(user);
		user = new User("Peach");
		users.add(user);
		user = new User("Daisy");
		users.add(user);
		users.add(new User("Daisy"));
		users.add(user);

		user = users.get(0);
		String content = toJson(user);

		when(repository
				.findByUsername(Mockito
						.any(String.class)))
				.thenReturn(Optional.of(user));
		when(repository
				.save(Mockito
						.any(User.class)))
				.thenReturn(user);
		when(assembler
				.toModel(Mockito
						.any(User.class)))
				.thenCallRealMethod();

		mvc.perform(MockMvcRequestBuilders.put("/users")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andDo(print());

		user = new User("Pinocchio");
		content = toJson(user);
		when(repository
				.findByUsername(Mockito
						.any(String.class)))
				.thenReturn(Optional.empty());
		when(repository
				.save(Mockito
						.any(User.class)))
				.thenReturn(user);

		mvc.perform(MockMvcRequestBuilders.put("/users")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andDo(print());
	}

}
