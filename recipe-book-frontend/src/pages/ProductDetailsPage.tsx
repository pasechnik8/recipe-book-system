import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getErrorMessage, getProduct } from "../api/api";
import type { ProductResponse } from "../types/types";
import { flagLabels, formatDate, productCategoryLabels, productReadinessLabels } from "../utils/labels";

export default function ProductDetailsPage() {
  const { id } = useParams();
  const [product, setProduct] = useState<ProductResponse | null>(null);
  const [error, setError] = useState("");

  useEffect(() => {
    if (id) {
      getProduct(id)
        .then(setProduct)
        .catch((e) => setError(getErrorMessage(e)));
    }
  }, [id]);

  if (error) return <div className="alert error">{error}</div>;
  if (!product) return <div>Загрузка...</div>;

  return (
    <div>
      <div className="page-top">
        <div>
          <h1>{product.name}</h1>
          <p className="muted">Карточка продукта</p>
        </div>

        <div className="actions">
          <Link className="button" to="/products">
            Назад
          </Link>
          <Link className="button primary" to={`/products/${product.id}/edit`}>
            Редактировать
          </Link>
        </div>
      </div>

      <section className="details panel">
        <div className="photo-gallery">
          {product.photos.map((photo) => (
            <img key={photo} src={photo} alt={product.name} />
          ))}
        </div>

        <p>
          <b>Калорийность:</b> {product.calories} ккал / 100 г
        </p>

        <p>
          <b>Белки:</b> {product.proteins} г / 100 г
        </p>

        <p>
          <b>Жиры:</b> {product.fats} г / 100 г
        </p>

        <p>
          <b>Углеводы:</b> {product.carbs} г / 100 г
        </p>

        <p>
          <b>Состав:</b> {product.composition || "—"}
        </p>

        <p>
          <b>Категория:</b> {productCategoryLabels[product.category]}
        </p>

        <p>
          <b>Готовность:</b> {productReadinessLabels[product.readiness]}
        </p>

        <p>
          <b>Дата создания:</b> {formatDate(product.createdAt)}
        </p>

        <p>
          <b>Дата редактирования:</b> {formatDate(product.updatedAt)}
        </p>

        <div className="chips">
          {product.flags.map((flag) => (
            <span className="chip" key={flag}>
              {flagLabels[flag]}
            </span>
          ))}
        </div>
      </section>
    </div>
  );
}