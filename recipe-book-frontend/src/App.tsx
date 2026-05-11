import { Link, Navigate, Route, Routes } from "react-router-dom";
import ProductListPage from "./pages/ProductListPage";
import ProductFormPage from "./pages/ProductFormPage";
import ProductDetailsPage from "./pages/ProductDetailsPage";
import DishListPage from "./pages/DishListPage";
import DishFormPage from "./pages/DishFormPage";
import DishDetailsPage from "./pages/DishDetailsPage";

export default function App() {
  return (
    <div>
      <header className="app-header">
        <Link to="/products" className="logo">
          Книга рецептов
        </Link>

        <nav className="nav">
          <Link to="/products">Продукты</Link>
          <Link to="/dishes">Блюда</Link>
        </nav>
      </header>

      <main className="container">
        <Routes>
          <Route path="/" element={<Navigate to="/products" />} />

          <Route path="/products" element={<ProductListPage />} />
          <Route path="/products/new" element={<ProductFormPage />} />
          <Route path="/products/:id" element={<ProductDetailsPage />} />
          <Route path="/products/:id/edit" element={<ProductFormPage />} />

          <Route path="/dishes" element={<DishListPage />} />
          <Route path="/dishes/new" element={<DishFormPage />} />
          <Route path="/dishes/:id" element={<DishDetailsPage />} />
          <Route path="/dishes/:id/edit" element={<DishFormPage />} />
        </Routes>
      </main>
    </div>
  );
}