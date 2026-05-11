import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { createProduct, getErrorMessage, getProduct, updateProduct } from "../api/api";
import type { AdditionalFlag, ProductCategory, ProductReadiness, ProductRequest } from "../types/types";
import {
  flagLabels,
  flags,
  productCategories,
  productCategoryLabels,
  productReadinessLabels,
  productReadinessValues
} from "../utils/labels";

const emptyProduct: ProductRequest = {
  name: "",
  photos: [],
  calories: 0,
  proteins: 0,
  fats: 0,
  carbs: 0,
  composition: "",
  category: "VEGETABLES",
  readiness: "READY_TO_EAT",
  flags: []
};

export default function ProductFormPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const isEdit = Boolean(id);

  const [form, setForm] = useState<ProductRequest>(emptyProduct);
  const [photoText, setPhotoText] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    if (id) {
      getProduct(id)
        .then((product) => {
          setForm({
            name: product.name,
            photos: product.photos,
            calories: product.calories,
            proteins: product.proteins,
            fats: product.fats,
            carbs: product.carbs,
            composition: product.composition,
            category: product.category,
            readiness: product.readiness,
            flags: product.flags
          });
          setPhotoText(product.photos.join("\n"));
        })
        .catch((e) => setError(getErrorMessage(e)));
    }
  }, [id]);

  function updateField<K extends keyof ProductRequest>(key: K, value: ProductRequest[K]) {
    setForm((prev) => ({ ...prev, [key]: value }));
  }

  function toggleFlag(flag: AdditionalFlag) {
    setForm((prev) => {
      const exists = prev.flags.includes(flag);
      return {
        ...prev,
        flags: exists ? prev.flags.filter((f) => f !== flag) : [...prev.flags, flag]
      };
    });
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();

    const photos = photoText
      .split("\n")
      .map((item) => item.trim())
      .filter(Boolean);

    const request: ProductRequest = {
      ...form,
      photos,
      composition: form.composition || null
    };

    try {
      setError("");

      if (isEdit && id) {
        await updateProduct(id, request);
      } else {
        await createProduct(request);
      }

      navigate("/products");
    } catch (e) {
      setError(getErrorMessage(e));
    }
  }

  return (
    <div>
      <div className="page-top">
        <div>
          <h1>{isEdit ? "Редактирование продукта" : "Создание продукта"}</h1>
          <p className="muted">Сумма белков, жиров и углеводов на 100 г не должна превышать 100.</p>
        </div>

        <Link to="/products" className="button">
          Назад
        </Link>
      </div>

      {error && <div className="alert error">{error}</div>}

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
            Калорийность
            <input type="number" step="0.1" min="0" value={form.calories} onChange={(e) => updateField("calories", Number(e.target.value))} />
          </label>

          <label>
            Белки
            <input type="number" step="0.1" min="0" max="100" value={form.proteins} onChange={(e) => updateField("proteins", Number(e.target.value))} />
          </label>

          <label>
            Жиры
            <input type="number" step="0.1" min="0" max="100" value={form.fats} onChange={(e) => updateField("fats", Number(e.target.value))} />
          </label>

          <label>
            Углеводы
            <input type="number" step="0.1" min="0" max="100" value={form.carbs} onChange={(e) => updateField("carbs", Number(e.target.value))} />
          </label>
        </div>

        <p className={form.proteins + form.fats + form.carbs > 100 ? "danger-text" : "muted"}>
          Сумма БЖУ: {(form.proteins + form.fats + form.carbs).toFixed(1)}
        </p>

        <label>
          Состав
          <textarea value={form.composition || ""} onChange={(e) => updateField("composition", e.target.value)} rows={3} />
        </label>

        <div className="form-grid">
          <label>
            Категория
            <select value={form.category} onChange={(e) => updateField("category", e.target.value as ProductCategory)}>
              {productCategories.map((item) => (
                <option key={item} value={item}>
                  {productCategoryLabels[item]}
                </option>
              ))}
            </select>
          </label>

          <label>
            Необходимость готовки
            <select value={form.readiness} onChange={(e) => updateField("readiness", e.target.value as ProductReadiness)}>
              {productReadinessValues.map((item) => (
                <option key={item} value={item}>
                  {productReadinessLabels[item]}
                </option>
              ))}
            </select>
          </label>
        </div>

        <fieldset>
          <legend>Дополнительные флаги</legend>

          <div className="checkbox-row">
            {flags.map((flag) => (
              <label key={flag} className="checkbox-label">
                <input type="checkbox" checked={form.flags.includes(flag)} onChange={() => toggleFlag(flag)} />
                {flagLabels[flag]}
              </label>
            ))}
          </div>
        </fieldset>

        <button className="button primary" type="submit">
          {isEdit ? "Сохранить" : "Создать"}
        </button>
      </form>
    </div>
  );
}