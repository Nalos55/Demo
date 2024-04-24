package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepository;

@DataJpaTest // use in memory database
@RunWith(SpringRunner.class)
public class UserRepositoryTest {

	@Autowired
	private UserRepository repository;

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
	public void repositorySetUp() {
		assertTrue(repository != null, "UserRepository was not created.");
	}

	@Test
	public void insert() {
		repository.save(new User("Mario", "A"));
		assertTrue(repository.count() > 0, "UserRepository save failed.");
	}

	@Test
	public void find() {
		User user = new User("Mario", "A");
		repository.save(user);

		List<User> users = repository.findAll();

		assertTrue(users != null, "UserRepository find returned null.");
		assertTrue(users.get(0).getUsername().equals(user.getUsername()),
				"UserRepository find returned wrong element by username.");
	}

	@Test
	public void findAll() {
		User user1 = new User("Mario", "A");
		User user2 = new User("Luigi", "A");
		repository.save(user1);
		repository.save(user2);
		List<User> users = repository.findAll();

		assertTrue(users != null, "UserRepository findAll returned null.");
		assertTrue(users.size() > 0, "UserRepository findAll returned empty.");
	}

	@Test
	public void delete() {
		User user1 = new User("Mario", "A");
		User user2 = new User("Luigi", "A");
		repository.save(user1);
		repository.save(user2);
		List<User> users = repository.findAll();

		int previousSize = users.size();

		repository.deleteById(users.get(0).getUsername());

		users = repository.findAll();

		assertTrue(users.size() < previousSize, "UserRepository delete did not delete.");
	}
}
