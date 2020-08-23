package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class OrderControllerTest {

    UserRepository userRepository = mock(UserRepository.class);
    OrderRepository orderRepository = mock(OrderRepository.class);

    private OrderController orderController;

    @Before
    public void setup() {
        UserOrder userOrder = getUserOrder();
        User user = getUser();
        given(orderRepository.findByUser(user)).willReturn(Collections.singletonList(userOrder));
        given(userRepository.findByUsername("newUser")).willReturn(user);
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submit() {
        ResponseEntity<UserOrder> userOrder = orderController.submit("newUser");

        assertNotNull(userOrder);
        assertEquals(200, userOrder.getStatusCodeValue());

        UserOrder order = userOrder.getBody();
        assertNotNull(order);
        assertEquals(BigDecimal.valueOf(20), order.getTotal());
        assertEquals(1, order.getItems().size());
    }

    @Test
    public void getOrdersForUser() {
        ResponseEntity<List<UserOrder>> userOrders = orderController.getOrdersForUser("newUser");

        List<UserOrder> orders = userOrders.getBody();

        assertEquals(HttpStatus.OK, userOrders.getStatusCode());
        assertEquals(1,orders.size());
    }

    @Test
    public void noOrdersForUser() {
        ResponseEntity<List<UserOrder>> userOrders = orderController.getOrdersForUser("noUser");

        List<UserOrder> orders = userOrders.getBody();

        assertEquals(HttpStatus.NOT_FOUND, userOrders.getStatusCode());
        assertNull(orders);

    }



    private UserOrder getUserOrder() {
        UserOrder item = new UserOrder();
        item.setId(1L);
        item.setItems(Collections.singletonList(getItem(BigDecimal.valueOf(20))));
        item.setUser(getUser());
        item.setTotal(BigDecimal.valueOf(20));
        return item;
    }

    private User getUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("newUser");
        user.setPassword("newPassword");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(Collections.singletonList(getItem(BigDecimal.valueOf(20))));
        cart.setTotal(BigDecimal.valueOf(20));

        user.setCart(cart);
        return user;
    }

    private Item getItem(BigDecimal price) {
        Item item = new Item();
        item.setId(1L);
        item.setName("testItem");
        item.setDescription("This is a test item");
        item.setPrice(price);
        return item;
    }
    
}