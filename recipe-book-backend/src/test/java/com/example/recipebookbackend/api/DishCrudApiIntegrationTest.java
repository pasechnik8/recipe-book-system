package com.example.recipebookbackend.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API/интеграционные тесты управления блюдами.
 *
 * Проверяются требования:
 * - создание блюда;
 * - просмотр блюда;
 * - редактирование блюда;
 * - удаление блюда;
 * - поиск;
 * - фильтрация;
 * - макрос категории;
 * - приоритет категории из поля формы;
 * - ограничение флагов блюда по составу.
 */
class DishCrudApiIntegrationTest extends BaseApiIntegrationTest {

    @Test
    @DisplayName("POST /api/dishes должен создать обычный борщ и снять недоступный vegan-флаг")
    void shouldCreateBorschAndRemoveUnavailableVeganFlag() throws Exception {
        long dishId = createBorsch();

        mockMvc.perform(get("/api/dishes/{id}", dishId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Борщ"))
                .andExpect(jsonPath("$.category").value("SOUP"))
                .andExpect(jsonPath("$.flags", hasSize(2)))
                .andExpect(jsonPath("$.ingredients", hasSize(4)));
    }

    @Test
    @DisplayName("POST /api/dishes должен создать веганский борщ со всеми флагами")
    void shouldCreateVeganBorschWithAllFlags() throws Exception {
        long dishId = createVeganBorsch();

        mockMvc.perform(get("/api/dishes/{id}", dishId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Борщ веганский"))
                .andExpect(jsonPath("$.category").value("SOUP"))
                .andExpect(jsonPath("$.flags", hasSize(3)))
                .andExpect(jsonPath("$.ingredients", hasSize(3)));
    }

    @Test
    @DisplayName("POST /api/dishes должен использовать категорию из поля формы, если она задана")
    void shouldUseExplicitCategoryInsteadOfMacro() throws Exception {
        long waterId = createWater();

        String request = dishJson(
                "!суп Вода питьевая",
                List.of(),
                0.0,
                0.0,
                0.0,
                0.0,
                200.0,
                "DRINK",
                List.of(ingredientMap(waterId, 200.0)),
                List.of("VEGAN", "GLUTEN_FREE", "SUGAR_FREE")
        );

        mockMvc.perform(post("/api/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Вода питьевая"))
                .andExpect(jsonPath("$.category").value("DRINK"));
    }

    @Test
    @DisplayName("GET /api/dishes/{id} должен вернуть карточку блюда с составом")
    void shouldGetDishByIdWithIngredients() throws Exception {
        long dishId = createBorsch();

        mockMvc.perform(get("/api/dishes/{id}", dishId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dishId))
                .andExpect(jsonPath("$.name").value("Борщ"))
                .andExpect(jsonPath("$.ingredients", hasSize(4)))
                .andExpect(jsonPath("$.autoCalculatedCalories").value(383.14));
    }

    @Test
    @DisplayName("GET /api/dishes должен искать блюда по подстроке")
    void shouldSearchDishesBySubstring() throws Exception {
        createBorsch();
        createVeganBorsch();

        mockMvc.perform(get("/api/dishes")
                        .param("search", "борщ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /api/dishes должен фильтровать блюда по категории")
    void shouldFilterDishesByCategory() throws Exception {
        createBorsch();
        createVeganBorsch();

        mockMvc.perform(get("/api/dishes")
                        .param("category", "SOUP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /api/dishes должен фильтровать блюда по vegan-флагу")
    void shouldFilterDishesByVeganFlag() throws Exception {
        createBorsch();
        createVeganBorsch();

        mockMvc.perform(get("/api/dishes")
                        .param("vegan", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Борщ веганский"));
    }

    @Test
    @DisplayName("PUT /api/dishes/{id} должен обновить состав блюда и пересчитать доступные флаги")
    void shouldUpdateDishIngredientsAndFlags() throws Exception {
        long dishId = createBorsch();

        long beetId = createBeet();
        long potatoId = createPotato();
        long waterId = createWater();

        String updateRequest = dishJson(
                "!суп Борщ обновлённый",
                List.of("http://localhost:8080/dishes/borsch-vegan.png"),
                158.5,
                4.0,
                0.7,
                26.0,
                550.0,
                null,
                List.of(
                        ingredientMap(beetId, 100.0),
                        ingredientMap(potatoId, 150.0),
                        ingredientMap(waterId, 300.0)
                ),
                List.of("VEGAN", "GLUTEN_FREE", "SUGAR_FREE")
        );

        mockMvc.perform(put("/api/dishes/{id}", dishId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Борщ обновлённый"))
                .andExpect(jsonPath("$.flags", hasSize(3)))
                .andExpect(jsonPath("$.ingredients", hasSize(3)));
    }

    @Test
    @DisplayName("DELETE /api/dishes/{id} должен удалить блюдо")
    void shouldDeleteDish() throws Exception {
        long dishId = createBorsch();

        mockMvc.perform(delete("/api/dishes/{id}", dishId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/dishes/{id}", dishId))
                .andExpect(status().isNotFound());
    }
}