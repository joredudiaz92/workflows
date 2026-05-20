package com.workflows.mvc.controllers;

import com.workflows.mvc.models.Item;
import com.workflows.mvc.models.ItemRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class) // Solo carga la capa web
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemRepository itemRepository;

    @Test
    void shouldReturnHtmlViewByDefault() throws Exception {
        Mockito.when(itemRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attributeExists("items"));
    }

    @Test
    void shouldReturnJsonResponseWhenFormatIsJson() throws Exception {
        var items = List.of(new Item(1L, "Test Item"));
        Mockito.when(itemRepository.findAll()).thenReturn(items);

        mockMvc.perform(get("/items").param("format", "json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Item"));
    }
}
