import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { deleteProduct, getErrorMessage, getProducts } from "../api/api";
import type { ProductCategory, ProductReadiness, ProductResponse } from "../types/types";
import {
  flagLabels,
  productCategories,
  productCategoryLabels,
  productReadinessLabels,
  productReadinessValues
} from "../utils/labels";

export default function ProductListPage() {
  const [products, setProducts] = useState<ProductResponse[]>([]);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState<ProductCategory | "">("");
  const [readiness, setReadiness] = useState<ProductReadiness | "">("");
  const [vegan, setVegan] = useState("");
  const [glutenFree, setGlutenFree] = useState("");
  const [sugarFree, setSugarFree] = useState("");
  const [sortBy, setSortBy] = useState("name");
  const [sortDir, setSortDir] = useState("asc");

  async function loadProducts() {
    try {
      setError("");
      const data = await getProducts({
        search: search || undefined,
        category: category || undefined,
        readiness: readiness || undefined,
        vegan: vegan === "" ? undefined : vegan === "true",
        glutenFree: glutenFree === "" ? undefined : glutenFree === "true",
        sugarFree: sugarFree === "" ? undefined : sugarFree === "true",
        sortBy,
        sortDir
      });
      setProducts(data);
    } catch (e) {
      setError(getErrorMessage(e));
    }
  }

  useEffect(() => {
    loadProducts();
  }, []);

  async function handleDelete(id: number) {
    if (!window.confirm("Удалить продукт?")) return;

    try {
      await deleteProduct(id);
      await loadProducts();
    } catch (e) {
      alert(getErrorMessage(e));
    }
  }

  return (
    <div>
      <div className="page-top">
        <div>
          <h1>Продукты</h1>
          <p className="muted">Создание, поиск, фильтрация, сортировка и удаление продуктов.</p>
        </div>

        <Link to="/products/new" className="button primary">
          Создать продукт
        </Link>
      </div>

      {error && <div className="alert error">{error}</div>}

      <section className="panel">
        <h2>Фильтры</h2>

        <div className="filters">
          <label>
            Поиск
            <input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Например: картофель" />
          </label>

          <label>
            Категория
            <select value={category} onChange={(e) => setCategory(e.target.value as ProductCategory | "")}>
              <option value="">Все</option>
              {productCategories.map((item) => (
                <option key={item} value={item}>
                  {productCategoryLabels[item]}
                </option>
              ))}
            </select>
          </label>

          <label>
            Готовность
            <select value={readiness} onChange={(e) => setReadiness(e.target.value as ProductReadiness | "")}>
              <option value="">Все</option>
              {productReadinessValues.map((item) => (
                <option key={item} value={item}>
                  {productReadinessLabels[item]}
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

          <label>
            Сортировать по
            <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
              <option value="name">Названию</option>
              <option value="calories">Калорийности</option>
              <option value="proteins">Белкам</option>
              <option value="fats">Жирам</option>
              <option value="carbs">Углеводам</option>
            </select>
          </label>

          <label>
            Направление
            <select value={sortDir} onChange={(e) => setSortDir(e.target.value)}>
              <option value="asc">По возрастанию</option>
              <option value="desc">По убыванию</option>
            </select>
          </label>
        </div>

        <button className="button" onClick={loadProducts}>
          Применить
        </button>
      </section>

      <div className="grid">
        {products.map((product) => (
          <article key={product.id} className="card">
            <img
              className="card-img"
              src={product.photos[0] || "/placeholder.png"}
              alt={product.name}
              onError={(e) => {
                e.currentTarget.style.display = "none";
              }}
            />

            <div className="card-body">
              <h3>{product.name}</h3>

              <p>
                <b>Категория:</b> {productCategoryLabels[product.category]}
              </p>

              <p>
                <b>Готовность:</b> {productReadinessLabels[product.readiness]}
              </p>

              <p>
                <b>КБЖУ:</b> {product.calories} ккал / Б {product.proteins} / Ж {product.fats} / У {product.carbs}
              </p>

              <div className="chips">
                {product.flags.map((flag) => (
                  <span className="chip" key={flag}>
                    {flagLabels[flag]}
                  </span>
                ))}
              </div>

              <div className="actions">
                <Link className="button small" to={`/products/${product.id}`}>
                  Смотреть
                </Link>

                <Link className="button small" to={`/products/${product.id}/edit`}>
                  Изменить
                </Link>

                <button className="button small danger" onClick={() => handleDelete(product.id)}>
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