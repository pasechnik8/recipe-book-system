import { test } from "@playwright/test";

import {
  cleanDatabaseThroughApi,
  createPotato,
  createWater,
  createMeat,
  createBeet,
  createBorschViaApi,
  createDessertViaApi
} from "./helpers";

import { DishFormPage } from "./pages/dish-form-page";
import { DishesPage } from "./pages/dishes-page";

test.describe("UI: блюда", () => {
  test.beforeEach(async ({ request }) => {
    await cleanDatabaseThroughApi(request);
  });

  test("создание блюда", async ({ page, request }) => {
    await createBeet(request);
    await createPotato(request);
    await createWater(request);
    await createMeat(request);

    const form = new DishFormPage(page);
    const dishes = new DishesPage(page);

    await form.openNew();

    await form.fillMainInfo({
      name: "!суп Борщ",
      portionSize: "670",
      category: "SOUP"
    });

    await form.addIngredient("Свёкла — Овощи", "100");
    await form.addIngredient("Картофель — Овощи", "150");
    await form.addIngredient("Вода — Жидкость", "300");
    await form.addIngredient("Мясо — Мясной", "120");

    await form.fillNutrition("383.1", "27.3", "15.7", "34.1");

    await form.submitCreate();
    await form.expectDishesPage();

    await dishes.expectDishVisible("Борщ");
  });

  test("количество ингредиента -0.1 должно быть отклонено", async ({ page, request }) => {
    await createPotato(request);

    const form = new DishFormPage(page);

    await form.openNew();

    await form.fillMainInfo({
      name: "!суп Ошибка",
      portionSize: "100",
      category: "SOUP"
    });

    await form.addIngredient("Картофель — Овощи", "-0.1");

    await form.submitCreate();
    await form.expectNewDishPage();
  });

  test("количество ингредиента 0 должно быть отклонено", async ({ page, request }) => {
    await createPotato(request);

    const form = new DishFormPage(page);

    await form.openNew();

    await form.fillMainInfo({
      name: "!суп Ошибка",
      portionSize: "100",
      category: "SOUP"
    });

    await form.addIngredient("Картофель — Овощи", "0");

    await form.submitCreate();
    await form.expectNewDishPage();
  });

  test("количество ингредиента 0.1 должно быть принято", async ({ page, request }) => {
    await createPotato(request);

    const form = new DishFormPage(page);
    const dishes = new DishesPage(page);

    await form.openNew();

    await form.fillMainInfo({
      name: "!суп Минимум",
      portionSize: "1",
      category: "SOUP"
    });

    await form.addIngredient("Картофель — Овощи", "0.1");

    await form.calculateNutrition();
    await form.expectCaloriesCalculated();
    await form.roundNutritionToOneDecimal();
    await form.checkAllAvailableFlags();

    await form.submitCreate();
    await form.expectDishesPage();

    await dishes.expectDishVisible("Минимум");
  });

  test("создание веганского борща", async ({ page, request }) => {
    await createBeet(request);
    await createPotato(request);
    await createWater(request);

    const form = new DishFormPage(page);
    const dishes = new DishesPage(page);

    await form.openNew();

    await form.fillMainInfo({
      name: "!суп Борщ веганский",
      portionSize: "550",
      category: "SOUP"
    });

    await form.addIngredient("Свёкла — Овощи", "100");
    await form.addIngredient("Картофель — Овощи", "150");
    await form.addIngredient("Вода — Жидкость", "300");

    await form.calculateNutrition();
    await form.roundNutritionToOneDecimal();
    await form.checkAllAvailableFlags();

    await form.submitCreate();
    await form.expectDishesPage();

    await dishes.expectDishVisible("Борщ веганский");
  });

  test("просмотр карточки блюда", async ({ page, request }) => {
    await createBorschViaApi(request);

    const dishes = new DishesPage(page);

    await dishes.open();
    await dishes.openDetails("Борщ");
    await dishes.expectBorschDetails();
  });

  test("поиск блюда", async ({ page, request }) => {
    await createBorschViaApi(request);

    const dishes = new DishesPage(page);

    await dishes.open();
    await dishes.search("борщ");
    await dishes.expectDishVisible("Борщ");
  });

  test("фильтрация блюд", async ({ page, request }) => {
    await createBorschViaApi(request);
    await createDessertViaApi(request);

    const dishes = new DishesPage(page);

    await dishes.open();
    await dishes.filterByCategory("SOUP");
    await dishes.expectDishVisible("Борщ");
  });

  test("удаление блюда", async ({ page, request }) => {
    await createBorschViaApi(request);

    const dishes = new DishesPage(page);

    await dishes.open();
    await dishes.deleteDishAndExpectCountDecreased("Борщ");
  });
});