import type {
  AdditionalFlag,
  DishCategory,
  ProductCategory,
  ProductReadiness
} from "../types/types";

export const productCategoryLabels: Record<ProductCategory, string> = {
  FROZEN: "Замороженный",
  MEAT: "Мясной",
  VEGETABLES: "Овощи",
  GREENS: "Зелень",
  SPICES: "Специи",
  GRAINS: "Крупы",
  CANNED: "Консервы",
  LIQUID: "Жидкость",
  SWEETS: "Сладости"
};

export const productReadinessLabels: Record<ProductReadiness, string> = {
  READY_TO_EAT: "Готовый к употреблению",
  SEMI_FINISHED: "Полуфабрикат",
  REQUIRES_COOKING: "Требует приготовления"
};

export const dishCategoryLabels: Record<DishCategory, string> = {
  DESSERT: "Десерт",
  FIRST_COURSE: "Первое",
  SECOND_COURSE: "Второе",
  DRINK: "Напиток",
  SALAD: "Салат",
  SOUP: "Суп",
  SNACK: "Перекус"
};

export const flagLabels: Record<AdditionalFlag, string> = {
  VEGAN: "Веган",
  GLUTEN_FREE: "Без глютена",
  SUGAR_FREE: "Без сахара"
};

export const productCategories = Object.keys(productCategoryLabels) as ProductCategory[];
export const productReadinessValues = Object.keys(productReadinessLabels) as ProductReadiness[];
export const dishCategories = Object.keys(dishCategoryLabels) as DishCategory[];
export const flags = Object.keys(flagLabels) as AdditionalFlag[];

export function formatDate(value: string | null): string {
  if (!value) return "—";
  return new Date(value).toLocaleString("ru-RU");
}