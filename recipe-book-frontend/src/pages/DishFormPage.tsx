import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import {
  calculateDish,
  createDish,
  getDish,
  getErrorMessage,
  getProducts,
  updateDish
} from "../api/api";
import type {
  AdditionalFlag,
  DishCategory,
  DishIngredientRequest,
  DishRequest,
  ProductResponse
} from "../types/types";
import {
  dishCategories,
  dishCategoryLabels,
  flagLabels,
  flags,
  productCategoryLabels
} from "../utils/labels";

const emptyDish: DishRequest = {
  name: "",
  photos: [],
  calories: 0,
  proteins: 0,
  fats: 0,
  carbs: 0,
  portionSize: 0,
  category: null,
  ingredients: [],
  flags: []
};

export default function DishFormPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const isEdit = Boolean(id);

  const [products, setProducts] = useState<ProductResponse[]>([]);
  const [form, setForm] = useState<DishRequest>(emptyDish);
  const [photoText, setPhotoText] = useState("");
  const [error, setError] = useState("");
  const [availableFlags, setAvailableFlags] = useState<AdditionalFlag[]>([]);
  const [calculationInfo, setCalculationInfo] = useState("");

  useEffect(() => {
    getProducts()
      .then(setProducts)
      .catch((e) => setError(getErrorMessage(e)));

    if (id) {
      getDish(id)
        .then((dish) => {
          const request: DishRequest = {
            name: dish.name,
            photos: dish.photos,
            calories: dish.calories,
            proteins: dish.proteins,
            fats: dish.fats,
            carbs: dish.carbs,
            portionSize: dish.portionSize,
            category: dish.category,
            ingredients: dish.ingredients.map((item) => ({
              productId: item.productId,
              quantity: item.quantity
            })),
            flags: dish.flags
          };

          setForm(request);
          setPhotoText(dish.photos.join("\n"));
          setAvailableFlags(dish.availableFlags);
        })
        .catch((e) => setError(getErrorMessage(e)));
    }
  }, [id]);

  function updateField<K extends keyof DishRequest>(key: K, value: DishRequest[K]) {
    setForm((prev) => ({ ...prev, [key]: value }));
  }

  function addIngredient() {
    if (products.length === 0) {
      alert("Сначала создайте продукты");
      return;
    }

    const newIngredient: DishIngredientRequest = {
      productId: products[0].id,
      quantity: 100
    };

    setForm((prev) => ({
      ...prev,
      ingredients: [...prev.ingredients, newIngredient]
    }));
  }

  function updateIngredient(index: number, key: keyof DishIngredientRequest, value: number) {
    setForm((prev) => ({
      ...prev,
      ingredients: prev.ingredients.map((item, i) =>
        i === index ? { ...item, [key]: value } : item
      )
    }));
  }

  function removeIngredient(index: number) {
    setForm((prev) => ({
      ...prev,
      ingredients: prev.ingredients.filter((_, i) => i !== index)
    }));
  }

  function toggleFlag(flag: AdditionalFlag) {
    if (!availableFlags.includes(flag)) {
      alert(`Флаг "${flagLabels[flag]}" недоступен для этого состава блюда`);
      return;
    }

    setForm((prev) => {
      const exists = prev.flags.includes(flag);
      return {
        ...prev,
        flags: exists ? prev.flags.filter((f) => f !== flag) : [...prev.flags, flag]
      };
    });
  }

  async function handleCalculate() {
    const photos = photoText
      .split("\n")
      .map((item) => item.trim())
      .filter(Boolean);

    const request: DishRequest = {
      ...form,
      photos
    };

    try {
      setError("");
      const result = await calculateDish(request);

      setForm((prev) => ({
        ...prev,
        name: result.normalizedName || prev.name,
        category: prev.category || result.categoryFromMacro,
        calories: result.calories,
        proteins: result.proteins,
        fats: result.fats,
        carbs: result.carbs,
        flags: prev.flags.filter((flag) => result.availableFlags.includes(flag))
      }));

      setAvailableFlags(result.availableFlags);
      setCalculationInfo("КБЖУ рассчитаны автоматически. При необходимости значения можно изменить вручную.");
    } catch (e) {
      setError(getErrorMessage(e));
    }
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();

    const photos = photoText
      .split("\n")
      .map((item) => item.trim())
      .filter(Boolean);

    const request: DishRequest = {
      ...form,
      photos
    };

    try {
      setError("");

      if (isEdit && id) {
        await updateDish(id, request);
      } else {
        await createDish(request);
      }

      navigate("/dishes");
    } catch (e) {
      setError(getErrorMessage(e));
    }
  }

  const selectedProductIds = form.ingredients.map((item) => item.productId);

  return (
    <div>
      <div className="page-top">
        <div>
          <h1>{isEdit ? "Редактирование блюда" : "Создание блюда"}</h1>
          <p className="muted">
            Можно использовать макросы: !десерт, !первое, !второе, !напиток, !салат, !суп, !перекус.
          </p>
        </div>

        <Link to="/dishes" className="button">
          Назад
        </Link>
      </div>

      {error && <div className="alert error">{error}</div>}
      {calculationInfo && <div className="alert success">{calculationInfo}</div>}

      <form className="form panel" onSubmit={handleSubmit}>
        <label>
          Название
          <input value={form.name} onChange={(e) => updateField("name", e.target.value)} required minLength={2} />
        </label>

        <label>
          Фотографии, каждая ссылка с новой строки
          <textarea value={photoText} onChange={(e) => setPhotoText(e.target.value)} rows={4} />
        </label>

        <div className="form-grid">
          <label>
            Размер порции, г
            <input type="number" step="0.1" min="0.1" value={form.portionSize} onChange={(e) => updateField("portionSize", Number(e.target.value))} />
          </label>

          <label>
            Категория
            <select value={form.category || ""} onChange={(e) => updateField("category", e.target.value ? (e.target.value as DishCategory) : null)}>
              <option value="">Определить по макросу</option>
              {dishCategories.map((item) => (
                <option key={item} value={item}>
                  {dishCategoryLabels[item]}
                </option>
              ))}
            </select>
          </label>
        </div>

        <section className="sub-panel">
          <div className="section-top">
            <h2>Состав блюда</h2>
            <button type="button" className="button small" onClick={addIngredient}>
              Добавить продукт
            </button>
          </div>

          {form.ingredients.length === 0 && (
            <p className="muted">Добавьте минимум один продукт.</p>
          )}

          {form.ingredients.map((ingredient, index) => {
            const product = products.find((p) => p.id === ingredient.productId);

            return (
              <div key={index} className="ingredient-row">
                <label>
                  Продукт
                  <select
                    value={ingredient.productId}
                    onChange={(e) => updateIngredient(index, "productId", Number(e.target.value))}
                  >
                    {products.map((product) => (
                      <option key={product.id} value={product.id}>
                        {product.name} — {productCategoryLabels[product.category]}
                      </option>
                    ))}
                  </select>
                </label>

                <label>
                  Количество, г
                  <input
                    type="number"
                    step="0.1"
                    min="0.1"
                    value={ingredient.quantity}
                    onChange={(e) => updateIngredient(index, "quantity", Number(e.target.value))}
                  />
                </label>

                <button type="button" className="button small danger" onClick={() => removeIngredient(index)}>
                  Удалить
                </button>

                {product && (
                  <p className="muted ingredient-info">
                    {product.calories} ккал / Б {product.proteins} / Ж {product.fats} / У {product.carbs} на 100 г
                  </p>
                )}
              </div>
            );
          })}
        </section>

        <button type="button" className="button" onClick={handleCalculate}>
          Рассчитать КБЖУ и доступные флаги
        </button>

        <div className="form-grid">
          <label>
            Калорийность
            <input type="number" step="0.1" min="0" value={form.calories} onChange={(e) => updateField("calories", Number(e.target.value))} />
          </label>

          <label>
            Белки
            <input type="number" step="0.1" min="0" value={form.proteins} onChange={(e) => updateField("proteins", Number(e.target.value))} />
          </label>

          <label>
            Жиры
            <input type="number" step="0.1" min="0" value={form.fats} onChange={(e) => updateField("fats", Number(e.target.value))} />
          </label>

          <label>
            Углеводы
            <input type="number" step="0.1" min="0" value={form.carbs} onChange={(e) => updateField("carbs", Number(e.target.value))} />
          </label>
        </div>

        <fieldset>
          <legend>Дополнительные флаги блюда</legend>

          <p className="muted">
            Флаг можно установить только если все продукты в составе имеют такой же флаг.
          </p>

          <div className="checkbox-row">
            {flags.map((flag) => (
              <label key={flag} className={`checkbox-label ${availableFlags.includes(flag) ? "" : "disabled"}`}>
                <input
                  type="checkbox"
                  checked={form.flags.includes(flag)}
                  disabled={!availableFlags.includes(flag)}
                  onChange={() => toggleFlag(flag)}
                />
                {flagLabels[flag]}
              </label>
            ))}
          </div>
        </fieldset>

        {selectedProductIds.length === 0 && (
          <div className="alert error">У блюда должен быть минимум один продукт.</div>
        )}

        <button className="button primary" type="submit">
          {isEdit ? "Сохранить" : "Создать"}
        </button>
      </form>
    </div>
  );
}