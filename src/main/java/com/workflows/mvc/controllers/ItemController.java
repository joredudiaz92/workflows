package com.workflows.mvc.controllers;

import com.workflows.mvc.models.ItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public Object getAll(
            @RequestParam(name = "format", required = false, defaultValue = "html") String format,
            Model model
    ) {
        var items = itemRepository.findAll();

        if ("json".equalsIgnoreCase(format)) {
            return ResponseEntity.ok(items);
        }

        model.addAttribute("items", items);
        return "items";
    }
}
