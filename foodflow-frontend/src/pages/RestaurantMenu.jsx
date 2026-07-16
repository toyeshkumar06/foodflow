import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axiosInstance from "../api/axiosInstance";
import Navbar from "../components/Navbar";
import { useCart } from "../context/CartContext";

function RestaurantMenu() {
  const { id } = useParams();
  const [restaurant, setRestaurant] = useState(null);
  const [menu, setMenu] = useState([]);
  const [loading, setLoading] = useState(true);
  const [addingId, setAddingId] = useState(null);
  const { addToCart, cart } = useCart();

  useEffect(() => {
    Promise.all([
      axiosInstance.get(`/restaurants/${id}`),
      axiosInstance.get(`/restaurants/${id}/menu`),
    ])
      .then(([restaurantRes, menuRes]) => {
        setRestaurant(restaurantRes.data);
        setMenu(menuRes.data);
      })
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, [id]);

  const handleAddToCart = async (foodItemId) => {
    setAddingId(foodItemId);
    try {
      await addToCart(foodItemId, 1);
    } catch (err) {
      alert(err.response?.data?.message || "Could not add item — check if your cart has items from a different restaurant.");
    } finally {
      setAddingId(null);
    }
  };

  // Group flat menu list into sections by category name
  const grouped = menu.reduce((acc, item) => {
    const cat = item.categoryName || "Other";
    if (!acc[cat]) acc[cat] = [];
    acc[cat].push(item);
    return acc;
  }, {});

  if (loading) {
    return (
      <>
        <Navbar />
        <div className="page-container" style={{ paddingTop: "40px" }}>
          <p style={{ color: "var(--color-text-light)" }}>Loading menu...</p>
        </div>
      </>
    );
  }

  return (
    <>
      <Navbar />
      <div className="page-container" style={{ paddingTop: "40px", paddingBottom: "80px" }}>
        <h1 style={{ fontSize: "28px" }}>{restaurant?.name}</h1>
        <p style={{ color: "var(--color-text-light)", marginBottom: "8px" }}>
          {restaurant?.cuisineType} · {restaurant?.addressLine}, {restaurant?.city}
        </p>
        <span className={`status-dot status-${restaurant?.status?.toLowerCase()}`}></span>{" "}
        <span style={{ fontSize: "14px", color: "var(--color-text-light)" }}>{restaurant?.status}</span>

        {restaurant?.status !== "OPEN" && (
          <p className="error-text" style={{ marginTop: "20px", maxWidth: "400px" }}>
            This restaurant isn't accepting orders right now.
          </p>
        )}

        {Object.entries(grouped).map(([category, items]) => (
          <div key={category} style={{ marginTop: "32px" }}>
            <h3 style={{ marginBottom: "12px" }}>{category}</h3>
            <div className="menu-list">
              {items.map((item) => (
                <div className="menu-item" key={item.id}>
                  <div className="menu-item-info">
                    <span className={`veg-dot ${item.veg ? "veg" : "non-veg"}`}></span>
                    <div>
                      <p className="menu-item-name">{item.name}</p>
                      <p className="menu-item-desc">{item.description}</p>
                      <p className="menu-item-price">₹{item.price}</p>
                    </div>
                  </div>
                  <button
                    className="btn btn-primary btn-small"
                    disabled={!item.available || restaurant?.status !== "OPEN" || addingId === item.id}
                    onClick={() => handleAddToCart(item.id)}
                  >
                    {!item.available ? "Unavailable" : addingId === item.id ? "Adding..." : "Add"}
                  </button>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </>
  );
}

export default RestaurantMenu;