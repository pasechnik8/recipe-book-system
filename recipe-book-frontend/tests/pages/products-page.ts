import { expect, Page } from "@playwright/test";

export class ProductsPage {
  constructor(private readonly page: Page) {}

  private productCard(name: string) {
    return this.page.locator(".card").filter({ hasText: name });
  }

  async open() {
    await this.page.goto("/products");
  }

  async expectProductVisible(name: string) {
    await expect(this.productCard(name).first()).toBeVisible();
  }

  async search(value: string) {
    await this.page.getByLabel("Поиск").fill(value);
    await this.page.getByRole("button", { name: "Применить" }).click();
  }

  async filterByCategory(category: string) {
    await this.page.getByLabel("Категория").selectOption(category);
    await this.page.getByRole("button", { name: "Применить" }).click();
  }

  async sortByCaloriesAsc() {
    await this.page.getByLabel("Сортировать по").selectOption("calories");
    await this.page.getByLabel("Направление").selectOption("asc");
    await this.page.getByRole("button", { name: "Применить" }).click();
  }

  async expectFirstCardContains(text: string) {
    await expect(this.page.locator(".card").first()).toContainText(text);
  }

  async openDetails(name: string) {
    await this.productCard(name)
      .first()
      .getByRole("link", { name: "Смотреть" })
      .click();
  }

  async openEdit(name: string) {
    await this.productCard(name)
      .first()
      .getByRole("link", { name: "Изменить" })
      .click();
  }

  async expectDetailsForPotato() {
    await expect(this.page.getByRole("heading", { name: "Картофель" }))
      .toBeVisible();

    await expect(this.page.getByText("77 ккал / 100 г"))
      .toBeVisible();
  }

  async deleteProduct(name: string) {
    const card = this.productCard(name).first();

    await expect(card).toBeVisible();

    this.page.once("dialog", async dialog => {
      await dialog.accept();
    });

    await card.getByRole("button", { name: "Удалить" }).click();

    await expect(card).toHaveCount(0);
  }

  async deleteOneProductAndExpectCountDecreased(name: string) {
    const cards = this.productCard(name);

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

  async tryDeleteProductAndExpectStillVisible(name: string) {
    const cards = this.productCard(name);

    await expect(cards.first()).toBeVisible();

    const beforeCount = await cards.count();

    this.page.once("dialog", async dialog => {
      await dialog.accept();
    });

    await cards
      .first()
      .getByRole("button", { name: "Удалить" })
      .click();

    await expect(cards).toHaveCount(beforeCount);
  }
}