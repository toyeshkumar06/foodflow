import { useEffect, useState } from "react";
import { useSearchParams, Link } from "react-router-dom";
import axiosInstance from "../api/axiosInstance";
import Navbar from "../components/Navbar";

function MyCollection() {
  const [searchParams] = useSearchParams();
  const [tab, setTab] = useState(searchParams.get("tab") === "dishes" ? "dishes" : "favorites");
  const [favorites, setFavorites] = useState([]);
  const [likedDishes, setLikedDishes] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      axiosInstance.get("/collection/favorites"),
      axiosInstance.get("/collection/liked-dishes"),
    ]).then(([favRes, dishRes]) => {
      setFavorites(favRes.data);
      setLikedDishes(dishRes.data);
      setLoading(false);
    });
  }, []);

  const removeFavorite = async (restaurantId) => {
    await axiosInstance.delete(`/collection/favorites/${restaurantId}`);
    setFavorites(favorites.filter((f) => f.restaurantId !== restaurantId));
  };

  return (
    <>
      <Navbar />
      <div className="page-container" style={{ paddingTop: "40px", paddingBottom: "60px" }}>
        <h1 style={{ fontSize: "28px", marginBottom: "20px" }}>My Collection</h1>

        <div className="owner-tabs" style={{ marginBottom: "24px" }}>
          <button className={`owner-tab ${tab === "favorites" ? "active" : ""}`} onClick={() => setTab("favorites")}>
            ⭐ Favorite Restaurants
          </button>
          <button className={`owner-tab ${tab === "dishes" ? "active" : ""}`} onClick={() => setTab("dishes")}>
            🍽️ Liked Dishes
          </button>
        </div>

        {loading && <p style={{ color: "var(--color-text-light)" }}>Loading...</p>}

        {!loading && tab === "favorites" && (
          <>
            {favorites.length === 0 && (
              <p style={{ color: "var(--color-text-light)" }}>
                No favorites yet — tap the heart icon on any restaurant to save it here.
              </p>
            )}
            <div className="restaurant-grid">
              {favorites.map((f) => (
                <div className="restaurant-card" key={f.restaurantId} style={{ position: "relative" }}>
                  <button className="favorite-remove-btn" onClick={() => removeFavorite(f.restaurantId)}>✕</button>
                  <Link to={`/restaurants/${f.restaurantId}`}>
                    {f.imageUrl ? (
                      <img src={f.imageUrl} alt={f.restaurantName} className="restaurant-card-image" />
                    ) : (
                      <div className="restaurant-card-image-placeholder">{f.restaurantName.charAt(0)}</div>
                    )}
                    <div className="restaurant-card-body">
                      <h3>{f.restaurantName}</h3>
                      <p className="restaurant-card-cuisine">{f.cuisineType}</p>
                      <span className="restaurant-card-rating">★ {f.averageRating?.toFixed(1) || "New"}</span>
                    </div>
                  </Link>
                </div>
              ))}
            </div>
          </>
        )}

        {!loading && tab === "dishes" && (
          <>
            {likedDishes.length === 0 && (
              <p style={{ color: "var(--color-text-light)" }}>
                No liked dishes yet — rate a dish 4 stars or higher after your order is delivered, and it'll show up here.
              </p>
            )}
            <div className="menu-list">
              {likedDishes.map((d) => (
                <Link to={`/restaurants/${d.restaurantId}`} key={d.foodItemId} className="menu-item" style={{ textDecoration: "none", color: "inherit" }}>
                  {d.imageUrl && <img src={d.imageUrl} alt={d.foodItemName} className="menu-item-image" />}
                  <div className="menu-item-info">
                    <div>
                      <p className="menu-item-name">{d.foodItemName}</p>
                      <p className="menu-item-desc">{d.restaurantName}</p>
                      <p className="menu-item-price">₹{d.price} · You rated ★{d.stars}</p>
                    </div>
                  </div>
                </Link>
              ))}
            </div>
          </>
        )}
      </div>
    </>
  );
}

export default MyCollection;