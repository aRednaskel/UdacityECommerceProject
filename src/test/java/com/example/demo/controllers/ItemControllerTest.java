package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItemControllerTest {

    ItemRepository itemRepository = mock(ItemRepository.class);

    private ItemController itemController;

    @Before
    public void setup() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
        Item item = getItem();
        item.setId(1L);
        given(itemRepository.findById(any())).willReturn(Optional.of(item));
        given(itemRepository.findAll()).willReturn(Collections.singletonList(item));
        given(itemRepository.findByName("testItem")).willReturn(Collections.singletonList(item));
    }

    @Test
    public void testGetItems() throws Exception {
        final ResponseEntity<List<Item>> responseEntity = itemController.getItems();

        List<Item> items = responseEntity.getBody();
        assertNotNull(items);
        assertEquals(1, items.size());

    }

    @Test
    public void testGetItemById() {
        final ResponseEntity<Item> responseEntity = itemController.getItemById(1L);

        Item item = responseEntity.getBody();
        assertNotNull(item);
        assertEquals("testItem", item.getName());
        assertEquals("This is a test item", item.getDescription());
        assertEquals(BigDecimal.TEN, item.getPrice());
    }

    @Test
    public void testGetItemsByName() {
        final ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("testItem");
        List<Item> items = responseEntity.getBody();
        assertNotNull(items);
        assertEquals(1, items.size());
    }

    @Test
    public void testGetItemsByNameNotFound() {
        final ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("tesItem");
        List<Item> items = responseEntity.getBody();
        assertNull(items);
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