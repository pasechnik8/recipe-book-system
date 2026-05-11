import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getDish, getErrorMessage } from "../api/api";
import type { DishResponse } from "../types/types";
import { dishCategoryLabels, flagLabels, formatDate } from "../utils/labels";

export default function DishDetailsPage() {
  const { id } = useParams();
  const [dish, setDish] = useState<DishResponse | null>(null);
  const [error, setError] = useState("");

  useEffect(() => {
    if (id) {
      getDish(id)
        .then(setDish)
        .catch((e) => setError(getErrorMessage(e)));
    }
  }, [id]);

  if (error) return <div className="alert error">{error}</div>;
  if (!dish) return <div>Загрузка...</div>;

  return (
    <div>
      <div className="page-top">
        <div>
          <h1>{dish.name}</h1>
          <p className="muted">Карточка блюда</p>
        </div>

        <div className="actions">
          <Link className="button" to="/dishes">
            Назад
          </Link>
          <Link className="button primary" to={`/dishes/${dish.id}/edit`}>
            Редактировать
          </Link>
        </div>
      </div>

      <section className="details panel">
        <div className="photo-gallery">
          {dish.photos.map((photo) => (
            <img key={photo} src={photo} alt={dish.name} />
          ))}
        </div>

        <p>
          <b>Категория:</b> {dishCategoryLabels[dish.category]}
        </p>

        <p>
          <b>Размер порции:</b> {dish.portionSize} г
        </p>

        <p>
          <b>Калорийность:</b> {dish.calories} ккал / порция
        </p>

        <p>
          <b>Белки:</b> {dish.proteins} г / порция
        </p>

        <p>
          <b>Жиры:</b> {dish.fats} г / порция
        </p>

        <p>
          <b>Углеводы:</b> {dish.carbs} г / порция
        </p>

        <p>
          <b>Автоматический расчёт:</b> {dish.autoCalculatedCalories} ккал / Б {dish.autoCalculatedProteins} / Ж{" "}
          {dish.autoCalculatedFats} / У {dish.autoCalculatedCarbs}
        </p>

        <p>
          <b>Дата создания:</b> {formatDate(dish.createdAt)}
        </p>

        <p>
          <b>Дата редактирования:</b> {formatDate(dish.updatedAt)}
        </p>

        <h2>Флаги</h2>

        <div className="chips">
          {dish.flags.map((flag) => (
            <span className="chip" key={flag}>
              {flagLabels[flag]}
            </span>
          ))}
        </div>

        <h2>Доступные флаги по составу</h2>

        <div className="chips">
          {dish.availableFlags.map((flag) => (
            <span className="chip" key={flag}>
              {flagLabels[flag]}
            </span>
          ))}
        </div>

        <h2>Состав</h2>

        <table className="table">
          <thead>
            <tr>
              <th>Продукт</th>
              <th>Количество, г</th>
            </tr>
          </thead>
          <tbody>
            {dish.ingredients.map((item) => (
              <tr key={item.productId}>
                <td>{item.productName}</td>
                <td>{item.quantity}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </div>
  );
}