import { expect, Page } from "@playwright/test";

type DishFormData = {
  name: string;
  portionSize: string;
  category: string;
};

export class DishFormPage {
  constructor(private readonly page: Page) {}

  async openNew() {
    await this.page.goto("/dishes/new");
  }

  async fillMainInfo(data: DishFormData) {
    await this.page.getByLabel("Название").fill(data.name);
    await this.page.getByLabel("Размер порции, г").fill(data.portionSize);
    await this.page.getByLabel("Категория").selectOption(data.category);
  }

  async addIngredient(productLabel: string, quantity: string) {
    await this.page.getByRole("button", { name: "Добавить продукт" }).click();

    const row = this.page.locator(".ingredient-row").last();

    await row.getByLabel("Продукт").selectOption({ label: productLabel });
    await row.getByLabel("Количество, г").fill(quantity);
  }

  async calculateNutrition() {
    await this.page.getByRole("button", { name: /Рассчитать/ }).click();
  }

  async fillNutrition(calories: string, proteins: string, fats: string, carbs: string) {
    await this.page.getByLabel("Калорийность").fill(calories);
    await this.page.getByLabel("Белки").fill(proteins);
    await this.page.getByLabel("Жиры").fill(fats);
    await this.page.getByLabel("Углеводы").fill(carbs);
  }

  async roundNutritionToOneDecimal() {
    const calories = Number(await this.page.getByLabel("Калорийность").inputValue());
    const proteins = Number(await this.page.getByLabel("Белки").inputValue());
    const fats = Number(await this.page.getByLabel("Жиры").inputValue());
    const carbs = Number(await this.page.getByLabel("Углеводы").inputValue());

    await this.fillNutrition(
      calories.toFixed(1),
      proteins.toFixed(1),
      fats.toFixed(1),
      carbs.toFixed(1)
    );
  }

  async expectCaloriesCalculated() {
    await expect(this.page.getByLabel("Калорийность")).not.toHaveValue("0");
  }

  async checkAllAvailableFlags() {
    await this.page.getByLabel("Веган").check();
    await this.page.getByLabel("Без глютена").check();
    await this.page.getByLabel("Без сахара").check();
  }

  async submitCreate() {
    await this.page.getByRole("button", { name: "Создать" }).click();
  }

  async expectDishesPage() {
    await expect(this.page).toHaveURL(/\/dishes$/);
  }

  async expectNewDishPage() {
    await expect(this.page).toHaveURL(/\/dishes\/new$/);
  }
}