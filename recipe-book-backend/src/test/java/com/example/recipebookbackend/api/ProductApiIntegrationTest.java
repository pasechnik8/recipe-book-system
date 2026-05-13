package com.example.recipebookbackend.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API/интеграционные тесты продуктов.
 *
 * Проверяются требования:
 * - создание продукта;
 * - просмотр продукта;
 * - редактирование продукта;
 * - удаление продукта;
 * - поиск;
 * - фильтрация;
 * - сортировка;
 * - валидация названия;
 * - валидация суммы БЖУ.
 *
 * Используются техники:
 * - эквивалентное разбиение;
 * - анализ граничных значений.
 */
class ProductApiIntegrationTest extends BaseApiIntegrationTest {

    @Test
    @DisplayName("POST /api/products должен создать корректный продукт")
    void shouldCreateValidProduct() throws Exception {
        String request = productJson(
                "Картофель",
                77.0,
                2.0,
                0.4,
                16.3,
                "VEGETABLES",
                "REQUIRES_COOKING",
                List.of("VEGAN", "GLUTEN_FREE", "SUGAR_FREE")
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Картофель"))
                .andExpect(jsonPath("$.calories").value(77.0))
                .andExpect(jsonPath("$.proteins").value(2.0))
                .andExpect(jsonPath("$.fats").value(0.4))
                .andExpect(jsonPath("$.carbs").value(16.3))
                .andExpect(jsonPath("$.category").value("VEGETABLES"))
                .andExpect(jsonPath("$.readiness").value("REQUIRES_COOKING"))
                .andExpect(jsonPath("$.flags", hasSize(3)));
    }

    @Test
    @DisplayName("POST /api/products должен принять название длиной 2 символа")
    void shouldAcceptProductNameWithTwoCharacters() throws Exception {
        String request = productJson(
                "Ри",
                340.0,
                6.7,
                0.7,
                78.9,
                "GRAINS",
                "REQUIRES_COOKING",
                List.of("VEGAN", "GLUTEN_FREE", "SUGAR_FREE")
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ри"));
    }

    @Test
    @DisplayName("POST /api/products должен отклонить пустое название")
    void shouldRejectBlankProductName() throws Exception {
        String request = productJson(
                "",
                100.0,
                10.0,
                10.0,
                10.0,
                "VEGETABLES",
                "READY_TO_EAT",
                List.of()
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/products должен отклонить название длиной 1 символ")
    void shouldRejectProductNameWithOneCharacter() throws Exception {
        String request = productJson(
                "A",
                100.0,
                10.0,
                10.0,
                10.0,
                "VEGETABLES",
                "READY_TO_EAT",
                List.of()
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/products должен принять продукт с суммой БЖУ ровно 100")
    void shouldAcceptProductWithBjuSumEquals100() throws Exception {
        String request = productJson(
                "Тестовый продукт",
                400.0,
                50.0,
                30.0,
                20.0,
                "VEGETABLES",
                "READY_TO_EAT",
                List.of()
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.proteins").value(50.0))
                .andExpect(jsonPath("$.fats").value(30.0))
                .andExpect(jsonPath("$.carbs").value(20.0));
    }

    @Test
    @DisplayName("POST /api/products должен отклонить продукт с суммой БЖУ больше 100")
    void shouldRejectProductWithBjuSumGreaterThan100() throws Exception {
        String request = productJson(
                "Некорректный продукт",
                300.0,
                60.0,
                30.0,
                20.0,
                "VEGETABLES",
                "READY_TO_EAT",
                List.of()
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0]").value(containsStringIgnoringCase("БЖУ")));
    }

    @Test
    @DisplayName("GET /api/products/{id} должен вернуть карточку продукта")
    void shouldGetProductById() throws Exception {
        long potatoId = createPotato();

        mockMvc.perform(get("/api/products/{id}", potatoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(potatoId))
                .andExpect(jsonPath("$.name").value("Картофель"))
                .andExpect(jsonPath("$.category").value("VEGETABLES"));
    }

    @Test
    @DisplayName("GET /api/products должен искать продукты по подстроке без учёта регистра")
    void shouldSearchProductsBySubstringIgnoreCase() throws Exception {
        createPotato();
        createWater();
        createMeat();

        mockMvc.perform(get("/api/products")
                        .param("search", "кар"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Картофель"));
    }

    @Test
    @DisplayName("GET /api/products должен фильтровать продукты по категории и флагу")
    void shouldFilterProductsByCategoryAndFlag() throws Exception {
        createPotato();
        createWater();
        createMeat();

        mockMvc.perform(get("/api/products")
                        .param("category", "VEGETABLES")
                        .param("vegan", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Картофель"));
    }

    @Test
    @DisplayName("GET /api/products должен сортировать продукты по калорийности")
    void shouldSortProductsByCaloriesAscending() throws Exception {
        createMeat();
        createPotato();
        createWater();

        mockMvc.perform(get("/api/products")
                        .param("sortBy", "calories")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("Вода"))
                .andExpect(jsonPath("$[1].name").value("Картофель"))
                .andExpect(jsonPath("$[2].name").value("Мясо"));
    }

    @Test
    @DisplayName("PUT /api/products/{id} должен обновить продукт")
    void shouldUpdateProduct() throws Exception {
        long potatoId = createPotato();

        String updateRequest = productJson(
                "Картофель молодой",
                80.0,
                2.1,
                0.5,
                16.8,
                "VEGETABLES",
                "REQUIRES_COOKING",
                List.of("VEGAN", "GLUTEN_FREE", "SUGAR_FREE")
        );

        mockMvc.perform(put("/api/products/{id}", potatoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Картофель молодой"))
                .andExpect(jsonPath("$.calories").value(80.0));

        mockMvc.perform(get("/api/products/{id}", potatoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Картофель молодой"));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} должен удалить неиспользуемый продукт")
    void shouldDeleteUnusedProduct() throws Exception {
        long waterId = createWater();

        mockMvc.perform(delete("/api/products/{id}", waterId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/products/{id}", waterId))
                .andExpect(status().isNotFound());
    }
}