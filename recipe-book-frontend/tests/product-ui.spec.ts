import { test } from "@playwright/test";

import {
  cleanDatabaseThroughApi,
  createPotato,
  createWater,
  createMeat
} from "./helpers";

import { ProductFormPage } from "./pages/product-form-page";
import { ProductsPage } from "./pages/products-page";

test.describe("UI: продукты", () => {
  test.beforeEach(async ({ request }) => {
    await cleanDatabaseThroughApi(request);
  });

  test("создание корректного продукта", async ({ page }) => {
    const productName = `Картофель тест ${Date.now()}`;

    const form = new ProductFormPage(page);
    const products = new ProductsPage(page);

    await form.openNew();

    await form.fill({
      name: productName,
      calories: "77",
      proteins: "2",
      fats: "0.4",
      carbs: "16.3",
      category: "VEGETABLES",
      readiness: "REQUIRES_COOKING"
    });

    await form.checkAllFlags();
    await form.submitCreate();
    await form.expectProductsPage();

    await products.expectProductVisible(productName);
  });

  test("пустое название продукта должно быть отклонено", async ({ page }) => {
    const form = new ProductFormPage(page);

    await form.openNew();

    await form.fill({
      name: "",
      calories: "100",
      proteins: "10",
      fats: "10",
      carbs: "10",
      category: "VEGETABLES",
      readiness: "READY_TO_EAT"
    });

    await form.submitCreate();
    await form.expectCreatePage();
  });

  test("название длиной 1 символ должно быть отклонено", async ({ page }) => {
    const form = new ProductFormPage(page);

    await form.openNew();

    await form.fill({
      name: "А",
      calories: "100",
      proteins: "10",
      fats: "10",
      carbs: "10",
      category: "VEGETABLES",
      readiness: "READY_TO_EAT"
    });

    await form.submitCreate();
    await form.expectCreatePage();
  });

  test("название длиной 2 символа должно быть принято", async ({ page }) => {
    const productName = `Ри ${Date.now()}`;

    const form = new ProductFormPage(page);
    const products = new ProductsPage(page);

    await form.openNew();

    await form.fill({
      name: productName,
      calories: "340",
      proteins: "6",
      fats: "1",
      carbs: "79",
      category: "GRAINS",
      readiness: "REQUIRES_COOKING"
    });

    await form.submitCreate();
    await form.expectProductsPage();

    await products.expectProductVisible(productName);
  });

  test("сумма БЖУ 99.9 должна быть принята", async ({ page }) => {
    const productName = `Продукт 99.9 ${Date.now()}`;

    const form = new ProductFormPage(page);
    const products = new ProductsPage(page);

    await form.openNew();

    await form.fill({
      name: productName,
      calories: "400",
      proteins: "50",
      fats: "30",
      carbs: "19.9",
      category: "VEGETABLES",
      readiness: "READY_TO_EAT"
    });

    await form.submitCreate();
    await form.expectProductsPage();

    await products.expectProductVisible(productName);
  });

  test("сумма БЖУ 100 должна быть принята", async ({ page }) => {
    const productName = `Продукт 100 ${Date.now()}`;

    const form = new ProductFormPage(page);
    const products = new ProductsPage(page);

    await form.openNew();

    await form.fill({
      name: productName,
      calories: "400",
      proteins: "50",
      fats: "30",
      carbs: "20",
      category: "VEGETABLES",
      readiness: "READY_TO_EAT"
    });

    await form.submitCreate();
    await form.expectProductsPage();

    await products.expectProductVisible(productName);
  });

  test("сумма БЖУ 100.1 должна быть отклонена", async ({ page }) => {
    const form = new ProductFormPage(page);

    await form.openNew();

    await form.fill({
      name: `Продукт 100.1 ${Date.now()}`,
      calories: "400",
      proteins: "50",
      fats: "30",
      carbs: "20.1",
      category: "VEGETABLES",
      readiness: "READY_TO_EAT"
    });

    await form.submitCreate();
    await form.expectCreatePage();
  });

  test("поиск продукта", async ({ page, request }) => {
    await createPotato(request);

    const products = new ProductsPage(page);

    await products.open();
    await products.search("кар");
    await products.expectProductVisible("Картофель");
  });

  test("фильтрация продуктов", async ({ page, request }) => {
    await createPotato(request);
    await createWater(request);
    await createMeat(request);

    const products = new ProductsPage(page);

    await products.open();
    await products.filterByCategory("MEAT");
    await products.expectProductVisible("Мясо");
  });

  test("сортировка продуктов по калорийности", async ({ page, request }) => {
    await createMeat(request);
    await createPotato(request);
    await createWater(request);

    const products = new ProductsPage(page);

    await products.open();
    await products.sortByCaloriesAsc();
    await products.expectFirstCardContains("Вода");
  });

  test("просмотр карточки продукта", async ({ page, request }) => {
    await createPotato(request);

    const products = new ProductsPage(page);

    await products.open();
    await products.openDetails("Картофель");
    await products.expectDetailsForPotato();
  });

  test("редактирование продукта", async ({ page }) => {
    const productName = `Картофель тест ${Date.now()}`;
    const updatedProductName = `${productName} молодой`;

    const form = new ProductFormPage(page);
    const products = new ProductsPage(page);

    await form.openNew();

    await form.fill({
      name: productName,
      calories: "77",
      proteins: "2",
      fats: "0.4",
      carbs: "16.3",
      category: "VEGETABLES",
      readiness: "REQUIRES_COOKING"
    });

    await form.checkAllFlags();
    await form.submitCreate();
    await form.expectProductsPage();

    await products.openEdit(productName);

    await form.expectEditPageOpened();
    await form.changeName(updatedProductName);
    await form.changeCalories("80");
    await form.submitSave();

    await products.open();
    await products.expectProductVisible(updatedProductName);
  });

  test("удаление продукта", async ({ page }) => {
    const productName = `Вода тест ${Date.now()}`;

    const form = new ProductFormPage(page);
    const products = new ProductsPage(page);

    await form.openNew();

    await form.fill({
      name: productName,
      calories: "0",
      proteins: "0",
      fats: "0",
      carbs: "0",
      category: "LIQUID",
      readiness: "READY_TO_EAT"
    });

    await form.checkAllFlags();
    await form.submitCreate();
    await form.expectProductsPage();

    await products.deleteProduct(productName);
  });
});