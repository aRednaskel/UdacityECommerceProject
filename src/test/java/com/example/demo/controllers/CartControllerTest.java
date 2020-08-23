package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CartControllerTest {

    UserRepository userRepository = mock(UserRepository.class);
    CartRepository cartRepository = mock(CartRepository.class);
    ItemRepository itemRepository = mock(ItemRepository.class);

    private CartController cartController;

    @Before
    public void setup() {

        User user = getUser();
        given(userRepository.findByUsername("newUser")).willReturn(user);
        Item item = getItem();
        given(itemRepository.findById(1L)).willReturn(Optional.of(item));
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void testAddTocart() {
        ModifyCartRequest modifyCartRequest = getCartRequest();
        ResponseEntity<Cart> cartResponse = cartController.addTocart(modifyCartRequest);

        assertNotNull(cartResponse);
        assertEquals(200, cartResponse.getStatusCodeValue());

        Cart cart = cartResponse.getBody();

        assertNotNull(cart);
        assertEquals(BigDecimal.valueOf(50), cart.getTotal());
        assertEquals(5, cart.getItems().size());
    }

    @Test
    public void testRemoveFromcart() {
        ModifyCartRequest modifyCartRequest = getCartRequest();
        cartController.addTocart(modifyCartRequest);

        modifyCartRequest.setQuantity(3);

        ResponseEntity<Cart> cartResponse = cartController.removeFromcart(modifyCartRequest);

        assertNotNull(cartResponse);
        assertEquals(200, cartResponse.getStatusCodeValue());

        Cart cart = cartResponse.getBody();

        assertNotNull(cart);
        assertEquals(2, cart.getItems().size());
        assertEquals(BigDecimal.valueOf(20), cart.getTotal());
    }

    @Test
    public void testAddToCartNullValues() {
        ModifyCartRequest createUserRequest = getCartRequest();
        createUserRequest.setUsername("not_found");

        ResponseEntity<Cart> cartResponse = cartController.addTocart(createUserRequest);
        assertNull(cartResponse.getBody());
        assertEquals(404, cartResponse.getStatusCodeValue());

        createUserRequest.setUsername("newUser");
        createUserRequest.setItemId(0L);

        assertNull(cartResponse.getBody());
        assertEquals(404, cartResponse.getStatusCodeValue());
    }

    @Test
    public void testRemoveToCartNullValues() {
        ModifyCartRequest createUserRequest = getCartRequest();
        createUserRequest.setUsername("not_found");

        ResponseEntity<Cart> cartResponse = cartController.addTocart(createUserRequest);
        assertNull(cartResponse.getBody());
        assertEquals(404, cartResponse.getStatusCodeValue());

        createUserRequest.setUsername("newUser");
        createUserRequest.setItemId(0L);

        assertNull(cartResponse.getBody());
        assertEquals(404, cartResponse.getStatusCodeValue());
    }

    private ModifyCartRequest getCartRequest() {
        ModifyCartRequest createUserRequest = new ModifyCartRequest();
        createUserRequest.setUsername("newUser");
        createUserRequest.setItemId(1L);
        createUserRequest.setQuantity(5);
        return createUserRequest;
    }

    private User getUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("newUser");
        user.setPassword("newPassword");

        Cart cart = new Cart();
        cart.setId(1L);
        user.setCart(cart);

        return user;
    }

    private Item getItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("testItem");
        item.setDescription("This is a test item");
        item.setPrice(BigDecimal.TEN);
        return item;
    }
}