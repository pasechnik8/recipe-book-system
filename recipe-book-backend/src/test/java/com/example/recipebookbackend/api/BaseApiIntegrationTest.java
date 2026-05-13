package com.example.recipebookbackend.api;

import com.example.recipebookbackend.repository.DishIngredientRepository;
import com.example.recipebookbackend.repository.DishRepository;
import com.example.recipebookbackend.repository.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Базовый класс для API/интеграционных тестов.
 *
 * Здесь находится общая подготовка тестовой БД и методы создания тестовых данных.
 * Тесты выполняются без изоляции:
 * - поднимается Spring Boot context;
 * - используются реальные Controller, Service, Repository;
 * - используется тестовая H2 база данных;
 * - запросы отправляются через MockMvc как HTTP API.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class BaseApiIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() throws Exception {
        deleteAllDishesThroughApi();
        deleteAllProductsThroughApi();
    }

    private void deleteAllDishesThroughApi() throws Exception {
        String response = mockMvc.perform(get("/api/dishes"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode dishes = objectMapper.readTree(response);

        for (JsonNode dish : dishes) {
            long id = dish.get("id").asLong();

            mockMvc.perform(delete("/api/dishes/{id}", id))
                    .andExpect(status().isOk());
        }
    }

    private void deleteAllProductsThroughApi() throws Exception {
        String response = mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode products = objectMapper.readTree(response);

        for (JsonNode product : products) {
            long id = product.get("id").asLong();

            mockMvc.perform(delete("/api/products/{id}", id))
                    .andExpect(status().isOk());
        }
    }

    protected long createPotato() throws Exception {
        return createProductAndReturnId(productJson(
                "Картофель",
                77.0,
                2.0,
                0.4,
                16.3,
                "VEGETABLES",
                "REQUIRES_COOKING",
                List.of("VEGAN", "GLUTEN_FREE", "SUGAR_FREE")
        ));
    }

    protected long createWater() throws Exception {
        return createProductAndReturnId(productJson(
                "Вода",
                0.0,
                0.0,
                0.0,
                0.0,
                "LIQUID",
                "READY_TO_EAT",
                List.of("VEGAN", "GLUTEN_FREE", "SUGAR_FREE")
        ));
    }

    protected long createMeat() throws Exception {
        return createProductAndReturnId(productJson(
                "Мясо",
                187.2,
                18.9,
                12.4,
                0.0,
                "MEAT",
                "REQUIRES_COOKING",
                List.of("GLUTEN_FREE", "SUGAR_FREE")
        ));
    }

    protected long createBeet() throws Exception {
        return createProductAndReturnId(productJson(
                "Свёкла",
                43.0,
                1.6,
                0.2,
                9.6,
                "VEGETABLES",
                "REQUIRES_COOKING",
                List.of("VEGAN", "GLUTEN_FREE", "SUGAR_FREE")
        ));
    }

    protected List<Long> createBasicProducts() throws Exception {
        long beetId = createBeet();
        long potatoId = createPotato();
        long waterId = createWater();
        long meatId = createMeat();

        return List.of(beetId, potatoId, waterId, meatId);
    }

    protected long createBorsch() throws Exception {
        List<Long> ids = createBasicProducts();

        return createBorschUsingExistingProducts(
                ids.get(0),
                ids.get(1),
                ids.get(2),
                ids.get(3)
        );
    }

    protected long createBorschUsingExistingProducts(long beetId,
                                                     long potatoId,
                                                     long waterId,
                                                     long meatId) throws Exception {
        String request = dishJson(
                "!суп Борщ",
                List.of("http://localhost:8080/dishes/borsch.png"),
                383.14,
                26.76,
                15.16,
                33.45,
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

        return createDishAndReturnId(request);
    }

    protected long createVeganBorsch() throws Exception {
        long beetId = createBeet();
        long potatoId = createPotato();
        long waterId = createWater();

        String request = dishJson(
                "!суп Борщ веганский",
                List.of("http://localhost:8080/dishes/borsch-vegan.png"),
                158.5,
                4.6,
                0.8,
                34.05,
                550.0,
                null,
                List.of(
                        ingredientMap(beetId, 100.0),
                        ingredientMap(potatoId, 150.0),
                        ingredientMap(waterId, 300.0)
                ),
                List.of("VEGAN", "GLUTEN_FREE", "SUGAR_FREE")
        );

        return createDishAndReturnId(request);
    }

    protected long createProductAndReturnId(String json) throws Exception {
        String response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        return node.get("id").asLong();
    }

    protected long createDishAndReturnId(String json) throws Exception {
        String response = mockMvc.perform(post("/api/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        return node.get("id").asLong();
    }

    protected String productJson(String name,
                                 double calories,
                                 double proteins,
                                 double fats,
                                 double carbs,
                                 String category,
                                 String readiness,
                                 List<String> flags) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("photos", List.of("http://localhost:8080/images/test.png"));
        body.put("calories", calories);
        body.put("proteins", proteins);
        body.put("fats", fats);
        body.put("carbs", carbs);
        body.put("composition", name);
        body.put("category", category);
        body.put("readiness", readiness);
        body.put("flags", flags);

        return objectMapper.writeValueAsString(body);
    }

    protected String dishJson(String name,
                              List<String> photos,
                              double calories,
                              double proteins,
                              double fats,
                              double carbs,
                              double portionSize,
                              String category,
                              List<Map<String, Object>> ingredients,
                              List<String> flags) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("photos", photos);
        body.put("calories", calories);
        body.put("proteins", proteins);
        body.put("fats", fats);
        body.put("carbs", carbs);
        body.put("portionSize", portionSize);
        body.put("category", category);
        body.put("ingredients", ingredients);
        body.put("flags", flags);

        return objectMapper.writeValueAsString(body);
    }

    protected Map<String, Object> ingredientMap(long productId, double quantity) {
        Map<String, Object> ingredient = new HashMap<>();
        ingredient.put("productId", productId);
        ingredient.put("quantity", quantity);
        return ingredient;
    }
}