import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import axiosInstance from "../api/axiosInstance";
import Navbar from "../components/Navbar";

const FILTERS = [
  { key: "all", label: "All" },
  { key: "new", label: "New to You" },
  { key: "reordered", label: "Highly Reordered" },
  { key: "topRated", label: "Rated 4+" },
  { key: "budget", label: "Under ₹300" },
];

function Restaurants() {
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeFilter, setActiveFilter] = useState("all");
  const [sortBy, setSortBy] = useState("");

  useEffect(() => {
    async function loadData() {
      try {
        const [restaurantsRes, ordersRes] = await Promise.all([
          axiosInstance.get("/restaurants"),
          axiosInstance.get("/orders"),
        ]);

        const openRestaurants = restaurantsRes.data.filter((r) => r.status === "OPEN");

        // Count how many times the customer has ordered from each restaurant
        const orderCounts = {};
        ordersRes.data.forEach((o) => {
          const key = o.restaurantName;
          orderCounts[key] = (orderCounts[key] || 0) + 1;
        });

        // Fetch each restaurant's menu once to compute a "starting from" price —
        // only 6 restaurants at this scale, so N+1 calls here is totally fine.
        const enriched = await Promise.all(
          openRestaurants.map(async (r) => {
            let startingPrice = null;
            try {
              const menuRes = await axiosInstance.get(`/restaurants/${r.id}/menu`);
              if (menuRes.data.length > 0) {
                startingPrice = Math.min(...menuRes.data.map((i) => i.price));
              }
            } catch (e) { /* ignore, just won't have a price badge */ }

            return {
              ...r,
              orderCount: orderCounts[r.name] || 0,
              startingPrice,
            };
          })
        );

        setRestaurants(enriched);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  let filtered = restaurants.filter((r) => {
    if (activeFilter === "new") return r.orderCount === 0;
    if (activeFilter === "reordered") return r.orderCount >= 2;
    if (activeFilter === "topRated") return r.averageRating >= 4;
    if (activeFilter === "budget") return r.startingPrice !== null && r.startingPrice <= 300;
    return true;
  });

  if (sortBy === "priceLowHigh") {
    filtered = [...filtered].sort((a, b) => (a.startingPrice ?? Infinity) - (b.startingPrice ?? Infinity));
  } else if (sortBy === "priceHighLow") {
    filtered = [...filtered].sort((a, b) => (b.startingPrice ?? -1) - (a.startingPrice ?? -1));
  } else if (sortBy === "ratingHighLow") {
    filtered = [...filtered].sort((a, b) => (b.averageRating ?? 0) - (a.averageRating ?? 0));
  }

  return (
    <>
      <Navbar />
      <div className="page-container" style={{ paddingTop: "40px", paddingBottom: "60px" }}>
        <h1 style={{ fontSize: "28px", marginBottom: "20px" }}>Restaurants near you</h1>

        {loading && <p style={{ color: "var(--color-text-light)" }}>Loading restaurants...</p>}

        {!loading && (
          <>
            <div className="filter-bar">
              <div className="filter-chips">
                {FILTERS.map((f) => (
                  <button
                    key={f.key}
                    className={`filter-chip ${activeFilter === f.key ? "active" : ""}`}
                    onClick={() => setActiveFilter(f.key)}
                  >
                    {f.label}
                  </button>
                ))}
              </div>
              <select className="input-field sort-select" value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
                <option value="">Sort by</option>
                <option value="priceLowHigh">Price: Low to High</option>
                <option value="priceHighLow">Price: High to Low</option>
                <option value="ratingHighLow">Rating: High to Low</option>
              </select>
            </div>

            {filtered.length === 0 && (
              <p style={{ color: "var(--color-text-light)", marginTop: "20px" }}>
                No restaurants match this filter right now.
              </p>
            )}

            <div className="restaurant-grid">
              {filtered.map((r) => (
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
                    <div className="restaurant-card-tags">
                      {r.orderCount === 0 && <span className="tag-badge tag-new">New to You</span>}
                      {r.orderCount >= 2 && <span className="tag-badge tag-reordered">Reordered {r.orderCount}×</span>}
                      {r.startingPrice !== null && <span className="tag-badge tag-price">From ₹{r.startingPrice}</span>}
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

export default Restaurants;