package com.example.recipebookbackend.dto;

import com.example.recipebookbackend.enums.AdditionalFlag;
import com.example.recipebookbackend.enums.DishCategory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class DishResponse {

    private Long id;
    private String name;
    private List<String> photos;
    private Double calories;
    private Double proteins;
    private Double fats;
    private Double carbs;
    private Double portionSize;
    private DishCategory category;
    private Set<AdditionalFlag> flags;
    private Set<AdditionalFlag> availableFlags;
    private Double autoCalculatedCalories;
    private Double autoCalculatedProteins;
    private Double autoCalculatedFats;
    private Double autoCalculatedCarbs;
    private List<DishIngredientResponse> ingredients;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DishResponse() {
    }

    public Long getId() {
        return id;
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

    public Double getPortionSize() {
        return portionSize;
    }

    public DishCategory getCategory() {
        return category;
    }

    public Set<AdditionalFlag> getFlags() {
        return flags;
    }

    public Set<AdditionalFlag> getAvailableFlags() {
        return availableFlags;
    }

    public Double getAutoCalculatedCalories() {
        return autoCalculatedCalories;
    }

    public Double getAutoCalculatedProteins() {
        return autoCalculatedProteins;
    }

    public Double getAutoCalculatedFats() {
        return autoCalculatedFats;
    }

    public Double getAutoCalculatedCarbs() {
        return autoCalculatedCarbs;
    }

    public List<DishIngredientResponse> getIngredients() {
        return ingredients;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setPortionSize(Double portionSize) {
        this.portionSize = portionSize;
    }

    public void setCategory(DishCategory category) {
        this.category = category;
    }

    public void setFlags(Set<AdditionalFlag> flags) {
        this.flags = flags;
    }

    public void setAvailableFlags(Set<AdditionalFlag> availableFlags) {
        this.availableFlags = availableFlags;
    }

    public void setAutoCalculatedCalories(Double autoCalculatedCalories) {
        this.autoCalculatedCalories = autoCalculatedCalories;
    }

    public void setAutoCalculatedProteins(Double autoCalculatedProteins) {
        this.autoCalculatedProteins = autoCalculatedProteins;
    }

    public void setAutoCalculatedFats(Double autoCalculatedFats) {
        this.autoCalculatedFats = autoCalculatedFats;
    }

    public void setAutoCalculatedCarbs(Double autoCalculatedCarbs) {
        this.autoCalculatedCarbs = autoCalculatedCarbs;
    }

    public void setIngredients(List<DishIngredientResponse> ingredients) {
        this.ingredients = ingredients;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}