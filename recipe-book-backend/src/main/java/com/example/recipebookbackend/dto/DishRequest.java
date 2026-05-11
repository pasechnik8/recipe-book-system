package com.example.recipebookbackend.dto;

import com.example.recipebookbackend.enums.AdditionalFlag;
import com.example.recipebookbackend.enums.DishCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DishRequest {

    @NotBlank(message = "Название блюда обязательно")
    @Size(min = 2, message = "Название блюда должно содержать минимум 2 символа")
    private String name;

    @Size(max = 5, message = "Максимум 5 фотографий")
    private List<String> photos = new ArrayList<>();

    @NotNull(message = "Калорийность обязательна")
    @DecimalMin(value = "0.0", inclusive = true, message = "Калорийность не может быть отрицательной")
    private Double calories;

    @NotNull(message = "Белки обязательны")
    @DecimalMin(value = "0.0", inclusive = true, message = "Белки не могут быть отрицательными")
    private Double proteins;

    @NotNull(message = "Жиры обязательны")
    @DecimalMin(value = "0.0", inclusive = true, message = "Жиры не могут быть отрицательными")
    private Double fats;

    @NotNull(message = "Углеводы обязательны")
    @DecimalMin(value = "0.0", inclusive = true, message = "Углеводы не могут быть отрицательными")
    private Double carbs;

    @NotEmpty(message = "Состав блюда должен содержать минимум один продукт")
    @Valid
    private List<DishIngredientRequest> ingredients = new ArrayList<>();

    @NotNull(message = "Размер порции обязателен")
    @DecimalMin(value = "0.000001", inclusive = true, message = "Размер порции должен быть больше 0")
    private Double portionSize;

    private DishCategory category;

    private Set<AdditionalFlag> flags = new HashSet<>();

    public DishRequest() {
    }

    public String getName() {
        return name;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public Double getCalories() {
        return calories;
    }

    public Double getProteins() {
        return proteins;
    }

    public Double getFats() {
        return fats;
    }

    public Double getCarbs() {
        return carbs;
    }

    public List<DishIngredientRequest> getIngredients() {
        return ingredients;
    }

    public Double getPortionSize() {
        return portionSize;
    }

    public DishCategory getCategory() {
        return category;
    }

    public Set<AdditionalFlag> getFlags() {
        return flags;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public void setProteins(Double proteins) {
        this.proteins = proteins;
    }

    public void setFats(Double fats) {
        this.fats = fats;
    }

    public void setCarbs(Double carbs) {
        this.carbs = carbs;
    }

    public void setIngredients(List<DishIngredientRequest> ingredients) {
        this.ingredients = ingredients;
    }

    public void setPortionSize(Double portionSize) {
        this.portionSize = portionSize;
    }

    public void setCategory(DishCategory category) {
        this.category = category;
    }

    public void setFlags(Set<AdditionalFlag> flags) {
        this.flags = flags;
    }
}