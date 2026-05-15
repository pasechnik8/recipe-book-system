import { APIRequestContext, expect } from "@playwright/test";

export const API_URL = "http://localhost:8080/api";

type ProductPayload = {
  name: string;
  photos: string[];
  calories: number;
  proteins: number;
  fats: number;
  carbs: number;
  composition: string;
  category: string;
  readiness: string;
  flags: string[];
};

export async function cleanDatabaseThroughApi(request: APIRequestContext) {
  const dishesResponse = await request.get(`${API_URL}/dishes`);
  const dishes = await dishesResponse.json();

  for (const dish of dishes) {
    await request.delete(`${API_URL}/dishes/${dish.id}`);
  }

  const productsResponse = await request.get(`${API_URL}/products`);
  const products = await productsResponse.json();

  for (const product of products) {
    await request.delete(`${API_URL}/products/${product.id}`);
  }
}

export async function createProductViaApi(
  request: APIRequestContext,
  product: ProductPayload
): Promise<number> {
  const response = await request.post(`${API_URL}/products`, {
    data: product
  });

  expect(response.ok()).toBeTruthy();

  const body = await response.json();
  return body.id;
}

export async function createPotato(request: APIRequestContext): Promise<number> {
  return createProductViaApi(request, {
    name: "Картофель",
    photos: [],
    calories: 77,
    proteins: 2,
    fats: 0.4,
    carbs: 16.3,
    composition: "Картофель",
    category: "VEGETABLES",
    readiness: "REQUIRES_COOKING",
    flags: ["VEGAN", "GLUTEN_FREE", "SUGAR_FREE"]
  });
}

export async function createWater(request: APIRequestContext): Promise<number> {
  return createProductViaApi(request, {
    name: "Вода",
    photos: [],
    calories: 0,
    proteins: 0,
    fats: 0,
    carbs: 0,
    composition: "Вода",
    category: "LIQUID",
    readiness: "READY_TO_EAT",
    flags: ["VEGAN", "GLUTEN_FREE", "SUGAR_FREE"]
  });
}

export async function createMeat(request: APIRequestContext): Promise<number> {
  return createProductViaApi(request, {
    name: "Мясо",
    photos: [],
    calories: 187.2,
    proteins: 18.9,
    fats: 12.4,
    carbs: 0,
    composition: "Мясо",
    category: "MEAT",
    readiness: "REQUIRES_COOKING",
    flags: ["GLUTEN_FREE", "SUGAR_FREE"]
  });
}

export async function createBeet(request: APIRequestContext): Promise<number> {
  return createProductViaApi(request, {
    name: "Свёкла",
    photos: [],
    calories: 43,
    proteins: 1.6,
    fats: 0.2,
    carbs: 9.6,
    composition: "Свёкла",
    category: "VEGETABLES",
    readiness: "REQUIRES_COOKING",
    flags: ["VEGAN", "GLUTEN_FREE", "SUGAR_FREE"]
  });
}

export async function createBorschViaApi(request: APIRequestContext) {
  const beetId = await createBeet(request);
  const potatoId = await createPotato(request);
  const waterId = await createWater(request);
  const meatId = await createMeat(request);

  const response = await request.post(`${API_URL}/dishes`, {
    data: {
      name: "!суп Борщ",
      photos: [],
      calories: 383.1,
      proteins: 27.3,
      fats: 15.7,
      carbs: 34.1,
      portionSize: 670,
      category: "SOUP",
      ingredients: [
        { productId: beetId, quantity: 100 },
        { productId: potatoId, quantity: 150 },
        { productId: waterId, quantity: 300 },
        { productId: meatId, quantity: 120 }
      ],
      flags: []
    }
  });

  expect(response.ok()).toBeTruthy();
}

export async function createDessertViaApi(request: APIRequestContext) {
  const potatoId = await createPotato(request);

  const response = await request.post(`${API_URL}/dishes`, {
    data: {
      name: "!десерт Торт",
      photos: [],
      calories: 38.5,
      proteins: 1,
      fats: 0.2,
      carbs: 8.2,
      portionSize: 50,
      category: "DESSERT",
      ingredients: [{ productId: potatoId, quantity: 50 }],
      flags: ["VEGAN", "GLUTEN_FREE", "SUGAR_FREE"]
    }
  });

  expect(response.ok()).toBeTruthy();
}

export async function createDishWithProductViaApi(
  request: APIRequestContext,
  productId: number
) {
  const response = await request.post(`${API_URL}/dishes`, {
    data: {
      name: "!суп Тестовое блюдо",
      photos: [],
      calories: 77,
      proteins: 2,
      fats: 0.4,
      carbs: 16.3,
      portionSize: 100,
      category: "SOUP",
      ingredients: [{ productId, quantity: 100 }],
      flags: ["VEGAN", "GLUTEN_FREE", "SUGAR_FREE"]
    }
  });

  expect(response.ok()).toBeTruthy();
}