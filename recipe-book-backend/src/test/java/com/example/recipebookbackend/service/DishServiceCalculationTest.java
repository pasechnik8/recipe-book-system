package com.example.recipebookbackend.service;

import com.example.recipebookbackend.dto.DishCalculationResponse;
import com.example.recipebookbackend.dto.DishRequest;
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

import static com.example.recipebookbackend.service.DishTestDataFactory.beet;
import static com.example.recipebookbackend.service.DishTestDataFactory.createDishRequest;
import static com.example.recipebookbackend.service.DishTestDataFactory.ingredient;
import static com.example.recipebookbackend.service.DishTestDataFactory.meat;
import static com.example.recipebookbackend.service.DishTestDataFactory.potato;
import static com.example.recipebookbackend.service.DishTestDataFactory.water;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @BeforeEach
    void setUp() {
        dishService = new DishService(dishRepository, productService);

        lenient().when(productService.getEntityById(1L)).thenReturn(potato());
        lenient().when(productService.getEntityById(2L)).thenReturn(water());
        lenient().when(productService.getEntityById(3L)).thenReturn(meat());
        lenient().when(productService.getEntityById(4L)).thenReturn(beet());
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
}