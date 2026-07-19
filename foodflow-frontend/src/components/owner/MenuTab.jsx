import { useEffect, useState } from "react";
import axiosInstance from "../../api/axiosInstance";

function MenuTab({ restaurantId }) {
  const [menu, setMenu] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCategoryForm, setShowCategoryForm] = useState(false);
  const [showItemForm, setShowItemForm] = useState(false);
  const [categoryName, setCategoryName] = useState("");
  const [itemForm, setItemForm] = useState({ name: "", description: "", price: "", veg: true, categoryId: "" });
  const [error, setError] = useState("");

  const loadMenu = () => {
    axiosInstance.get(`/restaurants/${restaurantId}/menu`).then((res) => setMenu(res.data));
  };

  const loadCategories = () => {
    axiosInstance.get(`/restaurants/${restaurantId}/categories`).then((res) => {
      setCategories(res.data);
      setLoading(false);
    });
  };

  useEffect(() => {
    loadMenu();
    loadCategories();
  }, [restaurantId]);

  const handleAddCategory = async (e) => {
    e.preventDefault();
    setError("");
    try {
      await axiosInstance.post(`/restaurant-owner/restaurants/${restaurantId}/categories`, { name: categoryName });
      setCategoryName("");
      setShowCategoryForm(false);
      loadCategories();
    } catch (err) {
      setError(err.response?.data?.message || "Could not add category.");
    }
  };

  const handleAddItem = async (e) => {
    e.preventDefault();
    setError("");
    try {
      await axiosInstance.post(`/restaurant-owner/restaurants/${restaurantId}/food-items`, {
        ...itemForm,
        price: Number(itemForm.price),
        categoryId: Number(itemForm.categoryId),
      });
      setItemForm({ name: "", description: "", price: "", veg: true, categoryId: "" });
      setShowItemForm(false);
      loadMenu();
    } catch (err) {
      setError(err.response?.data?.message || "Could not add item.");
    }
  };

  const toggleAvailability = async (item) => {
    await axiosInstance.patch(`/restaurant-owner/food-items/${item.id}/availability`, { available: !item.available });
    loadMenu();
  };

  const deleteItem = async (item) => {
    if (!confirm(`Delete ${item.name}?`)) return;
    await axiosInstance.delete(`/restaurant-owner/food-items/${item.id}`);
    loadMenu();
  };

  if (loading) return <p style={{ color: "var(--color-text-light)" }}>Loading menu...</p>;

  return (
    <div>
      <div style={{ display: "flex", gap: "10px", marginBottom: "20px" }}>
        <button className="btn btn-secondary btn-small" onClick={() => setShowCategoryForm(!showCategoryForm)}>+ Category</button>
        <button
          className="btn btn-primary btn-small"
          onClick={() => setShowItemForm(!showItemForm)}
          disabled={categories.length === 0}
          title={categories.length === 0 ? "Add a category first" : ""}
        >
          + Food Item
        </button>
      </div>

      {categories.length === 0 && (
        <p style={{ fontSize: "13px", color: "var(--color-text-light)", marginBottom: "16px" }}>
          Add at least one category before you can add food items.
        </p>
      )}

      {error && <p className="error-text">{error}</p>}

      {showCategoryForm && (
        <form onSubmit={handleAddCategory} className="card" style={{ marginBottom: "16px", maxWidth: "400px" }}>
          <div className="form-group">
            <label>Category Name</label>
            <input className="input-field" value={categoryName} onChange={(e) => setCategoryName(e.target.value)} required />
          </div>
          <button type="submit" className="btn btn-primary btn-small">Add Category</button>
        </form>
      )}

      {showItemForm && (
        <form onSubmit={handleAddItem} className="card" style={{ marginBottom: "20px", maxWidth: "400px" }}>
          <div className="form-group">
            <label>Item Name</label>
            <input className="input-field" value={itemForm.name} onChange={(e) => setItemForm({ ...itemForm, name: e.target.value })} required />
          </div>
          <div className="form-group">
            <label>Description</label>
            <input className="input-field" value={itemForm.description} onChange={(e) => setItemForm({ ...itemForm, description: e.target.value })} />
          </div>
          <div className="form-group">
            <label>Price (₹)</label>
            <input type="number" className="input-field" value={itemForm.price} onChange={(e) => setItemForm({ ...itemForm, price: e.target.value })} required />
          </div>
          <div className="form-group">
            <label>Veg / Non-Veg</label>
            <select className="input-field" value={itemForm.veg} onChange={(e) => setItemForm({ ...itemForm, veg: e.target.value === "true" })}>
              <option value="true">Veg</option>
              <option value="false">Non-Veg</option>
            </select>
          </div>
          <div className="form-group">
            <label>Category</label>
            <select
              className="input-field"
              value={itemForm.categoryId}
              onChange={(e) => setItemForm({ ...itemForm, categoryId: e.target.value })}
              required
            >
              <option value="">Select a category</option>
              {categories.map((c) => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </select>
          </div>
          <button type="submit" className="btn btn-primary btn-small">Add Item</button>
        </form>
      )}

      {menu.length === 0 && <p style={{ color: "var(--color-text-light)" }}>No menu items yet.</p>}

      <div className="menu-list">
        {menu.map((item) => (
          <div className="menu-item" key={item.id}>
            <div className="menu-item-info">
              <span className={`veg-dot ${item.veg ? "veg" : "non-veg"}`}></span>
              <div>
                <p className="menu-item-name">{item.name}</p>
                <p className="menu-item-desc">{item.categoryName} · {item.description}</p>
                <p className="menu-item-price">₹{item.price} {item.averageRating > 0 && `· ★ ${item.averageRating}`}</p>
              </div>
            </div>
            <div style={{ display: "flex", gap: "8px" }}>
              <button className="btn btn-secondary btn-small" onClick={() => toggleAvailability(item)}>
                {item.available ? "Mark Unavailable" : "Mark Available"}
              </button>
              <button className="remove-btn" onClick={() => deleteItem(item)}>✕</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default MenuTab;