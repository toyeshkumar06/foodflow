import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import axiosInstance from "../api/axiosInstance";
import Navbar from "../components/Navbar";

function Restaurants() {
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axiosInstance.get("/restaurants")
      .then((res) => setRestaurants(res.data))
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  const visibleRestaurants = restaurants.filter((r) => r.status === "OPEN");

  return (
    <>
      <Navbar />
      <div className="page-container" style={{ paddingTop: "40px", paddingBottom: "60px" }}>
        <h1 style={{ fontSize: "28px", marginBottom: "24px" }}>Restaurants near you</h1>

        {loading && <p style={{ color: "var(--color-text-light)" }}>Loading restaurants...</p>}

        {!loading && visibleRestaurants.length === 0 && (
          <p style={{ color: "var(--color-text-light)" }}>No restaurants open right now.</p>
        )}

        <div className="restaurant-grid">
          {visibleRestaurants.map((r) => (
            <Link to={`/restaurants/${r.id}`} key={r.id} className="restaurant-card">
              {r.imageUrl ? (
                <img src={r.imageUrl} alt={r.name} className="restaurant-card-image" />
              ) : (
                <div className="restaurant-card-image-placeholder">{r.name.charAt(0)}</div>
              )}
              <div className="restaurant-card-body">
                <h3>{r.name}</h3>
                <p className="restaurant-card-cuisine">{r.cuisineType || "Multi-cuisine"}</p>
                <div className="restaurant-card-meta">
                  <span className={`status-dot status-${r.status?.toLowerCase()}`}></span>
                  <span>{r.status}</span>
                  <span className="restaurant-card-rating">★ {r.averageRating?.toFixed(1) || "New"}</span>
                </div>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </>
  );
}

export default Restaurants;