package com.example.recipebookbackend.dto;

import com.example.recipebookbackend.enums.AdditionalFlag;
import com.example.recipebookbackend.enums.ProductCategory;
import com.example.recipebookbackend.enums.ProductReadiness;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductRequest {

    @NotBlank(message = "Название продукта обязательно")
    @Size(min = 2, message = "Название продукта должно содержать минимум 2 символа")
    private String name;

    @Size(max = 5, message = "Максимум 5 фотографий")
    private List<String> photos = new ArrayList<>();

    @NotNull(message = "Калорийность обязательна")
    @DecimalMin(value = "0.0", inclusive = true, message = "Калорийность не может быть отрицательной")
    private Double calories;

    @NotNull(message = "Белки обязательны")
    @DecimalMin(value = "0.0", inclusive = true, message = "Белки не могут быть отрицательными")
    @DecimalMax(value = "100.0", inclusive = true, message = "Белки не могут быть больше 100")
    private Double proteins;

    @NotNull(message = "Жиры обязательны")
    @DecimalMin(value = "0.0", inclusive = true, message = "Жиры не могут быть отрицательными")
    @DecimalMax(value = "100.0", inclusive = true, message = "Жиры не могут быть больше 100")
    private Double fats;

    @NotNull(message = "Углеводы обязательны")
    @DecimalMin(value = "0.0", inclusive = true, message = "Углеводы не могут быть отрицательными")
    @DecimalMax(value = "100.0", inclusive = true, message = "Углеводы не могут быть больше 100")
    private Double carbs;

    private String composition;

    @NotNull(message = "Категория продукта обязательна")
    private ProductCategory category;

    @NotNull(message = "Необходимость готовки обязательна")
    private ProductReadiness readiness;

    private Set<AdditionalFlag> flags = new HashSet<>();

    public ProductRequest() {
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

    public String getComposition() {
        return composition;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public ProductReadiness getReadiness() {
        return readiness;
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

    public void setComposition(String composition) {
        this.composition = composition;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public void setReadiness(ProductReadiness readiness) {
        this.readiness = readiness;
    }

    public void setFlags(Set<AdditionalFlag> flags) {
        this.flags = flags;
    }
}