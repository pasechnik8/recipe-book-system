package com.example.recipebookbackend.dto;

import com.example.recipebookbackend.enums.AdditionalFlag;
import com.example.recipebookbackend.enums.ProductCategory;
import com.example.recipebookbackend.enums.ProductReadiness;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class ProductResponse {

    private Long id;
    private String name;
    private List<String> photos;
    private Double calories;
    private Double proteins;
    private Double fats;
    private Double carbs;
    private String composition;
    private ProductCategory category;
    private ProductReadiness readiness;
    private Set<AdditionalFlag> flags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductResponse() {
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}