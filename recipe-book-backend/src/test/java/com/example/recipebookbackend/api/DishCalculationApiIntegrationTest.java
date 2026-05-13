package com.example.recipebookbackend.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API/интеграционные тесты расчёта КБЖУ блюда.
 *
 * Проверяются требования:
 * - автоматический расчёт КБЖУ;
 * - расчёт блюда из нескольких продуктов;
 * - обработка макроса категории;
 * - доступные флаги блюда;
 * - валидация количества ингредиента.
 *
 * Используются техники:
 * - эквивалентное разбиение;
 * - анализ граничных значений.
 */
class DishCalculationApiIntegrationTest extends BaseApiIntegrationTest {

    @Test
    @DisplayName("POST /api/dishes/calculate должен рассчитать калорийность блюда")
    void shouldCalculateDishNutrition() throws Exception {
        long beetId = createBeet();
        long potatoId = createPotato();
        long waterId = createWater();
        long meatId = createMeat();

        String request = dishJson(
                "!суп Борщ",
                List.of("http://localhost:8080/dishes/borsch.png"),
                0.0,
                0.0,
                0.0,
                0.0,
                670.0,
                null,
                List.of(
                        ingredientMap(beetId, 100.0),
                        ingredientMap(potatoId, 150.0),
                        ingredientMap(waterId, 300.0),
                        ingredientMap(meatId, 120.0)
                ),
                List.of("VEGAN", "GLUTEN_FREE", "SUGAR_FREE")
        );

        mockMvc.perform(post("/api/dishes/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.normalizedName").value("Борщ"))
                .andExpect(jsonPath("$.categoryFromMacro").value("SOUP"))
                .andExpect(jsonPath("$.calories").value(383.14))
                .andExpect(jsonPath("$.availableFlags", hasSize(2)));
    }

    @Test
    @DisplayName("POST /api/dishes/calculate должен отклонить количество продукта 0")
    void shouldRejectDishCalculationWithZeroQuantity() throws Exception {
        long potatoId = createPotato();

        String request = dishJson(
                "!суп Тест",
                List.of(),
                0.0,
                0.0,
                0.0,
                0.0,
                100.0,
                null,
                List.of(ingredientMap(potatoId, 0.0)),
                List.of()
        );

        mockMvc.perform(post("/api/dishes/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/dishes/calculate должен отклонить отрицательное количество продукта")
    void shouldRejectDishCalculationWithNegativeQuantity() throws Exception {
        long potatoId = createPotato();

        String request = dishJson(
                "!суп Тест",
                List.of(),
                0.0,
                0.0,
                0.0,
                0.0,
                100.0,
                null,
                List.of(ingredientMap(potatoId, -0.1)),
                List.of()
        );

        mockMvc.perform(post("/api/dishes/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/dishes/calculate должен принять минимальное положительное количество")
    void shouldAcceptDishCalculationWithSmallPositiveQuantity() throws Exception {
        long potatoId = createPotato();

        String request = dishJson(
                "!суп Тест",
                List.of(),
                0.0,
                0.0,
                0.0,
                0.0,
                1.0,
                null,
                List.of(ingredientMap(potatoId, 0.1)),
                List.of()
        );

        mockMvc.perform(post("/api/dishes/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calories").value(0.08));
    }
}