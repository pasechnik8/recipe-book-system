import axios from "axios";
import type {
  DishCalculationResponse,
  DishRequest,
  DishResponse,
  ProductRequest,
  ProductResponse
} from "../types/types";

const api = axios.create({
  baseURL: "http://localhost:8080/api"
});

export async function getProducts(params?: Record<string, string | boolean | undefined>) {
  const response = await api.get<ProductResponse[]>("/products", { params });
  return response.data;
}

export async function getProduct(id: string | number) {
  const response = await api.get<ProductResponse>(`/products/${id}`);
  return response.data;
}

export async function createProduct(data: ProductRequest) {
  const response = await api.post<ProductResponse>("/products", data);
  return response.data;
}

export async function updateProduct(id: string | number, data: ProductRequest) {
  const response = await api.put<ProductResponse>(`/products/${id}`, data);
  return response.data;
}

export async function deleteProduct(id: string | number) {
  await api.delete(`/products/${id}`);
}

export async function getDishes(params?: Record<string, string | boolean | undefined>) {
  const response = await api.get<DishResponse[]>("/dishes", { params });
  return response.data;
}

export async function getDish(id: string | number) {
  const response = await api.get<DishResponse>(`/dishes/${id}`);
  return response.data;
}

export async function createDish(data: DishRequest) {
  const response = await api.post<DishResponse>("/dishes", data);
  return response.data;
}

export async function updateDish(id: string | number, data: DishRequest) {
  const response = await api.put<DishResponse>(`/dishes/${id}`, data);
  return response.data;
}

export async function deleteDish(id: string | number) {
  await api.delete(`/dishes/${id}`);
}

export async function calculateDish(data: DishRequest) {
  const response = await api.post<DishCalculationResponse>("/dishes/calculate", data);
  return response.data;
}

export function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data;

    if (data?.message && data?.dishNames) {
      return `${data.message} Блюда: ${data.dishNames.join(", ")}`;
    }

    if (Array.isArray(data?.details)) {
      return data.details.join("\n");
    }

    if (typeof data?.error === "string") {
      return data.error;
    }

    if (typeof data?.message === "string") {
      return data.message;
    }
  }

  return "Произошла ошибка";
}