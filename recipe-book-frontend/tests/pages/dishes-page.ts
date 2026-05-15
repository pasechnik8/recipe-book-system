import { expect, Page } from "@playwright/test";

export class DishesPage {
  constructor(private readonly page: Page) {}

  private dishCard(name: string) {
    return this.page.locator(".card").filter({ hasText: name });
  }

  async open() {
    await this.page.goto("/dishes");
  }

  async expectDishVisible(name: string) {
    await expect(this.dishCard(name).first()).toBeVisible();
  }

  async search(value: string) {
    await this.page.getByLabel("Поиск").fill(value);
    await this.page.getByRole("button", { name: "Применить" }).click();
  }

  async filterByCategory(category: string) {
    await this.page.getByLabel("Категория").selectOption(category);
    await this.page.getByRole("button", { name: "Применить" }).click();
  }

  async openDetails(name: string) {
    await this.dishCard(name)
      .first()
      .getByRole("link", { name: "Смотреть" })
      .click();
  }

  async expectBorschDetails() {
    await expect(this.page.getByRole("heading", { name: "Борщ" }))
      .toBeVisible();

    await expect(this.page.getByText("Свёкла"))
      .toBeVisible();

    await expect(this.page.getByText("Картофель"))
      .toBeVisible();

    await expect(this.page.getByText("Мясо"))
      .toBeVisible();
  }

  async deleteDishAndExpectCountDecreased(name: string) {
    const cards = this.dishCard(name);

    await expect(cards.first()).toBeVisible();

    const beforeCount = await cards.count();

    this.page.once("dialog", async dialog => {
      await dialog.accept();
    });

    await cards
      .first()
      .getByRole("button", { name: "Удалить" })
      .click();

    await expect(cards).toHaveCount(beforeCount - 1);
  }
}