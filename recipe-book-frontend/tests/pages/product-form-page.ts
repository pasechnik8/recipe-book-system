import { expect, Page } from "@playwright/test";

type ProductFormData = {
  name: string;
  calories: string;
  proteins: string;
  fats: string;
  carbs: string;
  category: string;
  readiness: string;
};

export class ProductFormPage {
  constructor(private readonly page: Page) {}

  async openNew() {
    await this.page.goto("/products/new");
  }

  async fill(data: ProductFormData) {
    await this.page.getByLabel("Название").fill(data.name);
    await this.page.getByLabel("Калорийность").fill(data.calories);
    await this.page.getByLabel("Белки").fill(data.proteins);
    await this.page.getByLabel("Жиры").fill(data.fats);
    await this.page.getByLabel("Углеводы").fill(data.carbs);
    await this.page.getByLabel("Категория").selectOption(data.category);
    await this.page.getByLabel("Необходимость готовки").selectOption(data.readiness);
  }

  async checkAllFlags() {
    await this.page.getByLabel("Веган").check();
    await this.page.getByLabel("Без глютена").check();
    await this.page.getByLabel("Без сахара").check();
  }

  async submitCreate() {
    await this.page.getByRole("button", { name: "Создать" }).click();
  }

  async submitSave() {
    await this.page.getByRole("button", { name: "Сохранить" }).click();
  }

  async changeName(name: string) {
    await this.page.getByLabel("Название").fill(name);
  }

  async changeCalories(calories: string) {
    await this.page.getByLabel("Калорийность").fill(calories);
  }

  async expectCreatePage() {
    await expect(this.page).toHaveURL(/\/products\/new$/);
  }

  async expectProductsPage() {
    await expect(this.page).toHaveURL(/\/products$/);
  }

  async expectEditPageOpened() {
    await expect(this.page.getByRole("heading", { name: "Редактирование продукта" }))
      .toBeVisible();
  }
}