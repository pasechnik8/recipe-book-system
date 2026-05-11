package com.example.recipebookbackend.config;

import com.example.recipebookbackend.entity.Product;
import com.example.recipebookbackend.enums.AdditionalFlag;
import com.example.recipebookbackend.enums.ProductCategory;
import com.example.recipebookbackend.enums.ProductReadiness;
import com.example.recipebookbackend.repository.ProductRepository;
import java.util.List;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() == 0) {
                Product potato = new Product();
                potato.setName("Картофель");
                potato.setPhotos(List.of(
                        "http://localhost:8080/kartoshqa.png"
                ));
                potato.setCalories(77.0);
                potato.setProteins(2.0);
                potato.setFats(0.4);
                potato.setCarbs(16.3);
                potato.setComposition("Картофель свежий");
                potato.setCategory(ProductCategory.VEGETABLES);
                potato.setReadiness(ProductReadiness.REQUIRES_COOKING);
                potato.setFlags(Set.of(
                        AdditionalFlag.VEGAN,
                        AdditionalFlag.GLUTEN_FREE,
                        AdditionalFlag.SUGAR_FREE
                ));

                Product water = new Product();
                water.setName("Вода");
                water.setPhotos(List.of(
                        "http://localhost:8080/water.png"
                ));
                water.setCalories(0.0);
                water.setProteins(0.0);
                water.setFats(0.0);
                water.setCarbs(0.0);
                water.setComposition("Питьевая вода");
                water.setCategory(ProductCategory.LIQUID);
                water.setReadiness(ProductReadiness.READY_TO_EAT);
                water.setFlags(Set.of(
                        AdditionalFlag.VEGAN,
                        AdditionalFlag.GLUTEN_FREE,
                        AdditionalFlag.SUGAR_FREE
                ));

                Product meat = new Product();
                meat.setName("Мясо");
                meat.setPhotos(List.of(
                        "http://localhost:8080/meat.jpeg",
                        "http://localhost:8080/meat2.jpeg"
                ));
                meat.setCalories(187.2);
                meat.setProteins(18.9);
                meat.setFats(12.4);
                meat.setCarbs(0.0);
                meat.setComposition("Мясо свежее");
                meat.setCategory(ProductCategory.MEAT);
                meat.setReadiness(ProductReadiness.REQUIRES_COOKING);
                meat.setFlags(Set.of(
                        AdditionalFlag.GLUTEN_FREE,
                        AdditionalFlag.SUGAR_FREE
                ));

                Product beet = new Product();
                beet.setName("Свёкла");
                beet.setPhotos(List.of(
                        "http://localhost:8080/svekla.png"
                ));
                beet.setCalories(43.0);
                beet.setProteins(1.6);
                beet.setFats(0.2);
                beet.setCarbs(9.6);
                beet.setComposition("Свёкла свежая");
                beet.setCategory(ProductCategory.VEGETABLES);
                beet.setReadiness(ProductReadiness.REQUIRES_COOKING);
                beet.setFlags(Set.of(
                        AdditionalFlag.VEGAN,
                        AdditionalFlag.GLUTEN_FREE,
                        AdditionalFlag.SUGAR_FREE
                ));

                Product carrot = new Product();
                carrot.setName("Морковь");
                carrot.setPhotos(List.of(
                        "http://localhost:8080/pumpkin.png"
                ));
                carrot.setCalories(41.0);
                carrot.setProteins(0.9);
                carrot.setFats(0.2);
                carrot.setCarbs(9.6);
                carrot.setComposition("Морковь свежая");
                carrot.setCategory(ProductCategory.VEGETABLES);
                carrot.setReadiness(ProductReadiness.REQUIRES_COOKING);
                carrot.setFlags(Set.of(
                        AdditionalFlag.VEGAN,
                        AdditionalFlag.GLUTEN_FREE,
                        AdditionalFlag.SUGAR_FREE
                ));

                Product rice = new Product();
                rice.setName("Рис");
                rice.setPhotos(List.of(
                        "http://localhost:8080/gemini-3.1-flash-image-preview (nano-banana-2)_a_bunch_of_brand-free_ (1).png"
                ));
                rice.setCalories(340.0);
                rice.setProteins(6.7);
                rice.setFats(0.7);
                rice.setCarbs(78.9);
                rice.setComposition("Крупа рисовая");
                rice.setCategory(ProductCategory.GRAINS);
                rice.setReadiness(ProductReadiness.REQUIRES_COOKING);
                rice.setFlags(Set.of(
                        AdditionalFlag.VEGAN,
                        AdditionalFlag.GLUTEN_FREE,
                        AdditionalFlag.SUGAR_FREE
                ));

                productRepository.saveAll(List.of(
                        potato,
                        water,
                        meat,
                        beet,
                        carrot,
                        rice
                ));
            }
        };
    }
}