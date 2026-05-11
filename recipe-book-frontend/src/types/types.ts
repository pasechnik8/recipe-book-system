export type AdditionalFlag = "VEGAN" | "GLUTEN_FREE" | "SUGAR_FREE";

export type ProductCategory =
  | "FROZEN"
  | "MEAT"
  | "VEGETABLES"
  | "GREENS"
  | "SPICES"
  | "GRAINS"
  | "CANNED"
  | "LIQUID"
  | "SWEETS";

export type ProductReadiness =
  | "READY_TO_EAT"
  | "SEMI_FINISHED"
  | "REQUIRES_COOKING";

export type DishCategory =
  | "DESSERT"
  | "FIRST_COURSE"
  | "SECOND_COURSE"
  | "DRINK"
  | "SALAD"
  | "SOUP"
  | "SNACK";

export interface ProductRequest {
  name: string;
  photos: string[];
  calories: number;
  proteins: number;
  fats: number;
  carbs: number;
  composition: string | null;
  category: ProductCategory;
  readiness: ProductReadiness;
  flags: AdditionalFlag[];
}

export interface ProductResponse extends ProductRequest {
  id: number;
  createdAt: string;
  updatedAt: string | null;
}

export interface DishIngredientRequest {
  productId: number;
  quantity: number;
}

export interface DishIngredientResponse {
  productId: number;
  productName: string;
  quantity: number;
}

export interface DishRequest {
  name: string;
  photos: string[];
  calories: number;
  proteins: number;
  fats: number;
  carbs: number;
  portionSize: number;
  category: DishCategory | null;
  ingredients: DishIngredientRequest[];
  flags: AdditionalFlag[];
}

export interface DishResponse {
  id: number;
  name: string;
  photos: string[];
  calories: number;
  proteins: number;
  fats: number;
  carbs: number;
  portionSize: number;
  category: DishCategory;
  flags: AdditionalFlag[];
  availableFlags: AdditionalFlag[];
  autoCalculatedCalories: number;
  autoCalculatedProteins: number;
  autoCalculatedFats: number;
  autoCalculatedCarbs: number;
  ingredients: DishIngredientResponse[];
  createdAt: string;
  updatedAt: string | null;
}

export interface DishCalculationResponse {
  normalizedName: string;
  categoryFromMacro: DishCategory | null;
  calories: number;
  proteins: number;
  fats: number;
  carbs: number;
  availableFlags: AdditionalFlag[];
}