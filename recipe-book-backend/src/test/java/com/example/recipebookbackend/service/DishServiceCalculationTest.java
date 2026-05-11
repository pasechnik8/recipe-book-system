package com.example.recipebookbackend.service;

import com.example.recipebookbackend.dto.DishCalculationResponse;
import com.example.recipebookbackend.dto.DishIngredientRequest;
import com.example.recipebookbackend.dto.DishRequest;
import com.example.recipebookbackend.entity.Product;
import com.example.recipebookbackend.enums.AdditionalFlag;
import com.example.recipebookbackend.enums.ProductCategory;
import com.example.recipebookbackend.enums.ProductReadiness;
import com.example.recipebookbackend.exception.BadRequestException;
import com.example.recipebookbackend.repository.DishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

/**
 * Unit-тесты автоматического расчёта калорийности блюда.
 *
 * Тестируемая функциональность:
 * - расчёт калорийности блюда по формуле из ТЗ:
 *   сумма(калорийность продукта на 100 г * количество продукта / 100).
 *
 * Используемые техники тест-дизайна:
 * 1. Эквивалентное разбиение:
 *    - обычное положительное количество продукта;
 *    - нулевое количество продукта;
 *    - отрицательное количество продукта;
 *    - несколько продуктов в составе;
 *    - продукт с нулевой калорийностью.
 *
 * 2. Анализ граничных значений:
 *    - количество продукта = 0;
 *    - количество продукта чуть больше 0;
 *    - количество продукта = 100 г.
 *
 * Используется мок ProductService, чтобы тестировать только DishService без базы данных.
 */
@ExtendWith(MockitoExtension.class)
class DishServiceCalculationTest {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private ProductService productService;

    private DishService dishService;

    private Product potato;
    private Product water;
    private Product meat;
    private Product beet;

    @BeforeEach
    void setUp() {
        dishService = new DishService(dishRepository, productService);

        potato = createProduct(
                1L,
                "Картофель",
                77.0,
                2.0,
                0.4,
                16.3,
                ProductCategory.VEGETABLES,
                ProductReadiness.REQUIRES_COOKING,
                Set.of(AdditionalFlag.VEGAN, AdditionalFlag.GLUTEN_FREE, AdditionalFlag.SUGAR_FREE)
        );

        water = createProduct(
                2L,
                "Вода",
                0.0,
                0.0,
                0.0,
                0.0,
                ProductCategory.LIQUID,
                ProductReadiness.READY_TO_EAT,
                Set.of(AdditionalFlag.VEGAN, AdditionalFlag.GLUTEN_FREE, AdditionalFlag.SUGAR_FREE)
        );

        meat = createProduct(
                3L,
                "Мясо",
                187.2,
                18.9,
                12.4,
                0.0,
                ProductCategory.MEAT,
                ProductReadiness.REQUIRES_COOKING,
                Set.of(AdditionalFlag.GLUTEN_FREE, AdditionalFlag.SUGAR_FREE)
        );

        beet = createProduct(
                4L,
                "Свёкла",
                43.0,
                1.6,
                0.2,
                9.6,
                ProductCategory.VEGETABLES,
                ProductReadiness.REQUIRES_COOKING,
                Set.of(AdditionalFlag.VEGAN, AdditionalFlag.GLUTEN_FREE, AdditionalFlag.SUGAR_FREE)
        );

        lenient().when(productService.getEntityById(1L)).thenReturn(potato);
        lenient().when(productService.getEntityById(2L)).thenReturn(water);
        lenient().when(productService.getEntityById(3L)).thenReturn(meat);
        lenient().when(productService.getEntityById(4L)).thenReturn(beet);
    }

    @Nested
    @DisplayName("Эквивалентное разбиение")
    class EquivalencePartitioningTests {

        /**
         * Класс эквивалентности: один продукт с корректным положительным количеством.
         *
         * Картофель:
         * 77 ккал на 100 г.
         *
         * При 200 г:
         * 77 * 200 / 100 = 154 ккал.
         */
        @Test
        @DisplayName("Должен корректно рассчитать калорийность блюда из одного продукта")
        void shouldCalculateCaloriesForSingleProduct() {
            DishRequest request = createDishRequest(
                    List.of(ingredient(1L, 200.0)),
                    200.0
            );

            DishCalculationResponse response = dishService.calculateDraft(request);

            assertEquals(154.0, response.getCalories());
        }

        /**
         * Класс эквивалентности: несколько продуктов с корректными количествами.
         *
         * Борщ:
         * - Свёкла 100 г: 43 * 100 / 100 = 43
         * - Картофель 150 г: 77 * 150 / 100 = 115.5
         * - Вода 300 г: 0 * 300 / 100 = 0
         * - Мясо 120 г: 187.2 * 120 / 100 = 224.64
         *
         * Итого:
         * 43 + 115.5 + 0 + 224.64 = 383.14 ккал.
         */
        @Test
        @DisplayName("Должен корректно рассчитать калорийность блюда из нескольких продуктов")
        void shouldCalculateCaloriesForMultipleProducts() {
            DishRequest request = createDishRequest(
                    List.of(
                            ingredient(4L, 100.0),
                            ingredient(1L, 150.0),
                            ingredient(2L, 300.0),
                            ingredient(3L, 120.0)
                    ),
                    670.0
            );

            DishCalculationResponse response = dishService.calculateDraft(request);

            assertEquals(383.14, response.getCalories());
        }

        /**
         * Класс эквивалентности: продукт с нулевой калорийностью.
         *
         * Вода:
         * 0 ккал на 100 г.
         *
         * При любом положительном количестве итоговая калорийность должна быть 0.
         */
        @Test
        @DisplayName("Должен вернуть 0 калорий для продукта с нулевой калорийностью")
        void shouldReturnZeroCaloriesForZeroCalorieProduct() {
            DishRequest request = createDishRequest(
                    List.of(ingredient(2L, 500.0)),
                    500.0
            );

            DishCalculationResponse response = dishService.calculateDraft(request);

            assertEquals(0.0, response.getCalories());
        }

        /**
         * Класс эквивалентности: некорректное отрицательное количество продукта.
         *
         * Ожидаемый результат:
         * система должна отклонить запрос.
         */
        @Test
        @DisplayName("Должен выбросить ошибку при отрицательном количестве продукта")
        void shouldThrowExceptionForNegativeQuantity() {
            DishRequest request = createDishRequest(
                    List.of(ingredient(1L, -1.0)),
                    100.0
            );

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> dishService.calculateDraft(request)
            );

            assertTrue(exception.getMessage().contains("Количество каждого продукта должно быть больше 0"));
        }
    }

    @Nested
    @DisplayName("Анализ граничных значений")
    class BoundaryValueAnalysisTests {

        /**
         * Граничное значение: количество продукта равно 0.
         *
         * По ТЗ количество продукта в составе блюда должно быть больше 0.
         * Поэтому значение 0 является недопустимой границей.
         */
        @Test
        @DisplayName("Должен выбросить ошибку при количестве продукта 0 г")
        void shouldThrowExceptionForZeroQuantity() {
            DishRequest request = createDishRequest(
                    List.of(ingredient(1L, 0.0)),
                    100.0
            );

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> dishService.calculateDraft(request)
            );

            assertTrue(exception.getMessage().contains("Количество каждого продукта должно быть больше 0"));
        }

        /**
         * Граничное значение: минимально допустимое положительное количество.
         *
         * Количество 0.1 г больше 0, поэтому должно быть принято.
         *
         * Картофель:
         * 77 * 0.1 / 100 = 0.077.
         *
         * После округления до двух знаков:
         * 0.08.
         */
        @Test
        @DisplayName("Должен принять минимальное положительное количество продукта")
        void shouldAcceptSmallPositiveQuantity() {
            DishRequest request = createDishRequest(
                    List.of(ingredient(1L, 0.1)),
                    1.0
            );

            DishCalculationResponse response = dishService.calculateDraft(request);

            assertEquals(0.08, response.getCalories());
        }

        /**
         * Граничное значение: 100 г продукта.
         *
         * Если количество равно 100 г, калорийность блюда должна быть равна
         * калорийности продукта на 100 г.
         */
        @ParameterizedTest(name = "{0}: {1} г продукта должны дать {2} ккал")
        @CsvSource({
                "Картофель, 100.0, 77.0",
                "Мясо, 100.0, 187.2",
                "Свёкла, 100.0, 43.0",
                "Вода, 100.0, 0.0"
        })
        @DisplayName("Должен корректно считать калории при количестве 100 г")
        void shouldCalculateCaloriesForHundredGrams(String productName, double quantity, double expectedCalories) {
            Long productId = switch (productName) {
                case "Картофель" -> 1L;
                case "Вода" -> 2L;
                case "Мясо" -> 3L;
                case "Свёкла" -> 4L;
                default -> throw new IllegalArgumentException("Неизвестный продукт: " + productName);
            };

            DishRequest request = createDishRequest(
                    List.of(ingredient(productId, quantity)),
                    quantity
            );

            DishCalculationResponse response = dishService.calculateDraft(request);

            assertEquals(expectedCalories, response.getCalories());
        }
    }

    @Nested
    @DisplayName("Проверка дополнительных расчётов")
    class AdditionalCalculationTests {

        /**
         * Проверка, что вместе с калорийностью корректно рассчитываются БЖУ.
         *
         * Картофель 200 г:
         * - калории: 77 * 200 / 100 = 154
         * - белки: 2 * 200 / 100 = 4
         * - жиры: 0.4 * 200 / 100 = 0.8
         * - углеводы: 16.3 * 200 / 100 = 32.6
         */
        @Test
        @DisplayName("Должен корректно рассчитать не только калории, но и БЖУ")
        void shouldCalculateCaloriesProteinsFatsAndCarbs() {
            DishRequest request = createDishRequest(
                    List.of(ingredient(1L, 200.0)),
                    200.0
            );

            DishCalculationResponse response = dishService.calculateDraft(request);

            assertEquals(154.0, response.getCalories());
            assertEquals(4.0, response.getProteins());
            assertEquals(0.8, response.getFats());
            assertEquals(32.6, response.getCarbs());
        }

        /**
         * Проверка округления результата до двух знаков.
         *
         * Мясо 33.33 г:
         * 187.2 * 33.33 / 100 = 62.39376
         *
         * После округления:
         * 62.39.
         */
        @Test
        @DisplayName("Должен округлять рассчитанную калорийность до двух знаков")
        void shouldRoundCaloriesToTwoDecimalPlaces() {
            DishRequest request = createDishRequest(
                    List.of(ingredient(3L, 33.33)),
                    33.33
            );

            DishCalculationResponse response = dishService.calculateDraft(request);

            assertEquals(62.39, response.getCalories());
        }
    }

    private Product createProduct(Long id,
                                  String name,
                                  Double calories,
                                  Double proteins,
                                  Double fats,
                                  Double carbs,
                                  ProductCategory category,
                                  ProductReadiness readiness,
                                  Set<AdditionalFlag> flags) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPhotos(List.of());
        product.setCalories(calories);
        product.setProteins(proteins);
        product.setFats(fats);
        product.setCarbs(carbs);
        product.setComposition(name);
        product.setCategory(category);
        product.setReadiness(readiness);
        product.setFlags(flags);
        return product;
    }

    private DishIngredientRequest ingredient(Long productId, Double quantity) {
        DishIngredientRequest ingredient = new DishIngredientRequest();
        ingredient.setProductId(productId);
        ingredient.setQuantity(quantity);
        return ingredient;
    }

    private DishRequest createDishRequest(List<DishIngredientRequest> ingredients, Double portionSize) {
        DishRequest request = new DishRequest();
        request.setName("!суп Тестовое блюдо");
        request.setPhotos(List.of());
        request.setCalories(0.0);
        request.setProteins(0.0);
        request.setFats(0.0);
        request.setCarbs(0.0);
        request.setPortionSize(portionSize);
        request.setCategory(null);
        request.setIngredients(ingredients);
        request.setFlags(Set.of());
        return request;
    }
}