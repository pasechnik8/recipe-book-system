package com.example.recipebookbackend.dto;

import com.example.recipebookbackend.enums.AdditionalFlag;
import com.example.recipebookbackend.enums.DishCategory;
import java.util.Set;

public class DishCalculationResponse {

    private String normalizedName;
    private DishCategory categoryFromMacro;
    private Double calories;
    private Double proteins;
    private Double fats;
    private Double carbs;
    private Set<AdditionalFlag> availableFlags;

    public DishCalculationResponse() {
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public DishCategory getCategoryFromMacro() {
        return categoryFromMacro;
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

    public Set<AdditionalFlag> getAvailableFlags() {
        return availableFlags;
    }

    public void setNormalizedName(String normalizedName) {
        this.normalizedName = normalizedName;
    }

    public void setCategoryFromMacro(DishCategory categoryFromMacro) {
        this.categoryFromMacro = categoryFromMacro;
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

    public void setAvailableFlags(Set<AdditionalFlag> availableFlags) {
        this.availableFlags = availableFlags;
    }
}