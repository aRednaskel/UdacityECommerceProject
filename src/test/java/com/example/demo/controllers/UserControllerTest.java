package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    UserRepository userRepository = mock(UserRepository.class);
    CartRepository cartRepository = mock(CartRepository.class);
    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    private UserController userController;

    @Before
    public void setup() {
        User user = getUser();
        given(userRepository.findByUsername("newUser")).willReturn(user);
        given(userRepository.findById(1l)).willReturn(Optional.of(user));
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void createUser() {
        when(encoder.encode("newPassword")).thenReturn("thisIsHashed");
        CreateUserRequest userRequest = getUserRequest();

        final ResponseEntity<User> responseEntity = userController.createUser(userRequest);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        User user = responseEntity.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("newUser", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    @Test
    public void findById() {
        final ResponseEntity<User> responseEntity = userController.findById(1L);

        User user = responseEntity.getBody();
        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("newUser", user.getUsername());
        assertEquals("newPassword", user.getPassword());
    }

    @Test
    public void findByUserName() throws Exception {
        final ResponseEntity<User> responseEntity = userController.findByUserName("newUser");

        User user = responseEntity.getBody();
        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("newUser", user.getUsername());
        assertEquals("newPassword", user.getPassword());
    }

    private CreateUserRequest getUserRequest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("newUser");
        createUserRequest.setPassword("newPassword");
        createUserRequest.setConfirmPassword("newPassword");
        return createUserRequest;
    }

    private User getUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("newUser");
        user.setPassword("newPassword");
        return user;
    }
}