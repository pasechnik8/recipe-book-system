import { test } from "@playwright/test";

import {
  cleanDatabaseThroughApi,
  createWater,
  createPotato,
  createDishWithProductViaApi
} from "./helpers";

import { ProductsPage } from "./pages/products-page";

test.describe("UI: связи продуктов и блюд", () => {
  test.beforeEach(async ({ request }) => {
    await cleanDatabaseThroughApi(request);
  });

  test("неиспользуемый продукт можно удалить", async ({ page, request }) => {
    await createWater(request);

    const products = new ProductsPage(page);

    await products.open();
    await products.deleteOneProductAndExpectCountDecreased("Вода");
  });

  test("используемый продукт нельзя удалить", async ({ page, request }) => {
    const potatoId = await createPotato(request);

    await createDishWithProductViaApi(request, potatoId);

    const products = new ProductsPage(page);

    await products.open();
    await products.tryDeleteProductAndExpectStillVisible("Картофель");
  });
});