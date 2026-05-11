package com.example.recipebookbackend.entity;

import com.example.recipebookbackend.enums.AdditionalFlag;
import com.example.recipebookbackend.enums.ProductCategory;
import com.example.recipebookbackend.enums.ProductReadiness;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ElementCollection
    @CollectionTable(name = "product_photos", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "photo")
    private List<String> photos = new ArrayList<>();

    @Column(nullable = false)
    private Double calories;

    @Column(nullable = false)
    private Double proteins;

    @Column(nullable = false)
    private Double fats;

    @Column(nullable = false)
    private Double carbs;

    @Column(length = 5000)
    private String composition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductReadiness readiness;

    @ElementCollection(targetClass = AdditionalFlag.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "product_flags", joinColumns = @JoinColumn(name = "product_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "flag")
    private Set<AdditionalFlag> flags = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Product() {
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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