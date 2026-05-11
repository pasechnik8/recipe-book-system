import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { deleteDish, getDishes, getErrorMessage } from "../api/api";
import type { DishCategory, DishResponse } from "../types/types";
import { dishCategories, dishCategoryLabels, flagLabels } from "../utils/labels";

export default function DishListPage() {
  const [dishes, setDishes] = useState<DishResponse[]>([]);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState<DishCategory | "">("");
  const [vegan, setVegan] = useState("");
  const [glutenFree, setGlutenFree] = useState("");
  const [sugarFree, setSugarFree] = useState("");

  async function loadDishes() {
    try {
      setError("");
      const data = await getDishes({
        search: search || undefined,
        category: category || undefined,
        vegan: vegan === "" ? undefined : vegan === "true",
        glutenFree: glutenFree === "" ? undefined : glutenFree === "true",
        sugarFree: sugarFree === "" ? undefined : sugarFree === "true"
      });
      setDishes(data);
    } catch (e) {
      setError(getErrorMessage(e));
    }
  }

  useEffect(() => {
    loadDishes();
  }, []);

  async function handleDelete(id: number) {
    if (!window.confirm("Удалить блюдо?")) return;

    try {
      await deleteDish(id);
      await loadDishes();
    } catch (e) {
      alert(getErrorMessage(e));
    }
  }

  return (
    <div>
      <div className="page-top">
        <div>
          <h1>Блюда</h1>
          <p className="muted">Создание, просмотр, поиск, фильтрация и удаление блюд.</p>
        </div>

        <Link to="/dishes/new" className="button primary">
          Создать блюдо
        </Link>
      </div>

      {error && <div className="alert error">{error}</div>}

      <section className="panel">
        <h2>Фильтры</h2>

        <div className="filters">
          <label>
            Поиск
            <input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Например: борщ" />
          </label>

          <label>
            Категория
            <select value={category} onChange={(e) => setCategory(e.target.value as DishCategory | "")}>
              <option value="">Все</option>
              {dishCategories.map((item) => (
                <option key={item} value={item}>
                  {dishCategoryLabels[item]}
                </option>
              ))}
            </select>
          </label>

          <label>
            Веган
            <select value={vegan} onChange={(e) => setVegan(e.target.value)}>
              <option value="">Не важно</option>
              <option value="true">Да</option>
              <option value="false">Нет</option>
            </select>
          </label>

          <label>
            Без глютена
            <select value={glutenFree} onChange={(e) => setGlutenFree(e.target.value)}>
              <option value="">Не важно</option>
              <option value="true">Да</option>
              <option value="false">Нет</option>
            </select>
          </label>

          <label>
            Без сахара
            <select value={sugarFree} onChange={(e) => setSugarFree(e.target.value)}>
              <option value="">Не важно</option>
              <option value="true">Да</option>
              <option value="false">Нет</option>
            </select>
          </label>
        </div>

        <button className="button" onClick={loadDishes}>
          Применить
        </button>
      </section>

      <div className="grid">
        {dishes.map((dish) => (
          <article key={dish.id} className="card">
            <img
              className="card-img"
              src={dish.photos[0] || "/placeholder.png"}
              alt={dish.name}
              onError={(e) => {
                e.currentTarget.style.display = "none";
              }}
            />

            <div className="card-body">
              <h3>{dish.name}</h3>

              <p>
                <b>Категория:</b> {dishCategoryLabels[dish.category]}
              </p>

              <p>
                <b>Порция:</b> {dish.portionSize} г
              </p>

              <p>
                <b>КБЖУ:</b> {dish.calories} ккал / Б {dish.proteins} / Ж {dish.fats} / У {dish.carbs}
              </p>

              <div className="chips">
                {dish.flags.map((flag) => (
                  <span className="chip" key={flag}>
                    {flagLabels[flag]}
                  </span>
                ))}
              </div>

              <div className="actions">
                <Link className="button small" to={`/dishes/${dish.id}`}>
                  Смотреть
                </Link>

                <Link className="button small" to={`/dishes/${dish.id}/edit`}>
                  Изменить
                </Link>

                <button className="button small danger" onClick={() => handleDelete(dish.id)}>
                  Удалить
                </button>
              </div>
            </div>
          </article>
        ))}
      </div>
    </div>
  );
}