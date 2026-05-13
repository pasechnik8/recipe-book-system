package com.example.recipebookbackend.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API/интеграционные тесты связи продуктов и блюд.
 *
 * Проверяется требование:
 * - нельзя удалить продукт, который используется хотя бы в одном блюде;
 * - после удаления блюда продукт можно удалить.
 */
class ProductDishRelationApiIntegrationTest extends BaseApiIntegrationTest {

    @Test
    @DisplayName("DELETE /api/products/{id} должен запретить удаление продукта, используемого в блюде")
    void shouldRejectDeletingProductUsedInDish() throws Exception {
        List<Long> ids = createBasicProducts();
        long beetId = ids.get(0);
        long potatoId = ids.get(1);
        long waterId = ids.get(2);
        long meatId = ids.get(3);

        createBorschUsingExistingProducts(beetId, potatoId, waterId, meatId);

        mockMvc.perform(delete("/api/products/{id}", potatoId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsStringIgnoringCase("Удаление невозможно")))
                .andExpect(jsonPath("$.dishNames", hasSize(1)))
                .andExpect(jsonPath("$.dishNames[0]").value("Борщ"));
    }

    @Test
    @DisplayName("После удаления блюда продукт из его состава можно удалить")
    void shouldDeleteProductAfterDishDeletion() throws Exception {
        List<Long> ids = createBasicProducts();
        long beetId = ids.get(0);
        long potatoId = ids.get(1);
        long waterId = ids.get(2);
        long meatId = ids.get(3);

        long dishId = createBorschUsingExistingProducts(beetId, potatoId, waterId, meatId);

        mockMvc.perform(delete("/api/products/{id}", potatoId))
                .andExpect(status().isConflict());

        mockMvc.perform(delete("/api/dishes/{id}", dishId))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/products/{id}", potatoId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/products/{id}", potatoId))
                .andExpect(status().isNotFound());
    }
}