package com.example.recipebookbackend.controller;

import com.example.recipebookbackend.dto.DishCalculationResponse;
import com.example.recipebookbackend.dto.DishRequest;
import com.example.recipebookbackend.dto.DishResponse;
import com.example.recipebookbackend.enums.DishCategory;
import com.example.recipebookbackend.service.DishService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dishes")
@CrossOrigin(origins = "*")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @PostMapping
    public DishResponse create(@Valid @RequestBody DishRequest request) {
        return dishService.create(request);
    }

    @PutMapping("/{id}")
    public DishResponse update(@PathVariable Long id,
                               @Valid @RequestBody DishRequest request) {
        return dishService.update(id, request);
    }

    @GetMapping("/{id}")
    public DishResponse getById(@PathVariable Long id) {
        return dishService.getById(id);
    }

    @GetMapping
    public List<DishResponse> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) DishCategory category,
            @RequestParam(required = false) Boolean vegan,
            @RequestParam(required = false) Boolean glutenFree,
            @RequestParam(required = false) Boolean sugarFree
    ) {
        return dishService.getAll(search, category, vegan, glutenFree, sugarFree);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        dishService.delete(id);
    }

    @PostMapping("/calculate")
    public DishCalculationResponse calculate(@Valid @RequestBody DishRequest request) {
        return dishService.calculateDraft(request);
    }
}