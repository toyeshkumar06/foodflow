import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import axiosInstance from "../api/axiosInstance";
import Navbar from "../components/Navbar";

function OwnerRestaurants() {
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState("");
  const [form, setForm] = useState({
    name: "", description: "", cuisineType: "", addressLine: "", city: "", pincode: "",
    openingTime: "09:00", closingTime: "23:00", latitude: 28.6139, longitude: 77.2090,
  });

  const loadRestaurants = () => {
    axiosInstance.get("/restaurant-owner/restaurants/mine")
      .then((res) => setRestaurants(res.data))
      .finally(() => setLoading(false));
  };

  useEffect(() => { loadRestaurants(); }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    setError("");
    try {
      await axiosInstance.post("/restaurant-owner/restaurants", form);
      setShowForm(false);
      loadRestaurants();
    } catch (err) {
      setError(err.response?.data?.message || "Could not create restaurant.");
    }
  };

  return (
    <>
      <Navbar />
      <div className="page-container" style={{ paddingTop: "40px", paddingBottom: "60px" }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "24px" }}>
          <h1 style={{ fontSize: "28px" }}>My Restaurants</h1>
          <button className="btn btn-primary btn-small" onClick={() => setShowForm(!showForm)}>
            {showForm ? "Cancel" : "+ New Restaurant"}
          </button>
        </div>

        {showForm && (
          <form onSubmit={handleCreate} className="card" style={{ marginBottom: "24px", maxWidth: "500px" }}>
            <div className="form-group">
              <label>Name</label>
              <input className="input-field" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
            </div>
            <div className="form-group">
              <label>Description</label>
              <input className="input-field" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
            </div>
            <div className="form-group">
              <label>Cuisine Type</label>
              <input className="input-field" value={form.cuisineType} onChange={(e) => setForm({ ...form, cuisineType: e.target.value })} />
            </div>
            <div className="form-group">
              <label>Address Line</label>
              <input className="input-field" value={form.addressLine} onChange={(e) => setForm({ ...form, addressLine: e.target.value })} required />
            </div>
            <div className="form-group">
              <label>City</label>
              <input className="input-field" value={form.city} onChange={(e) => setForm({ ...form, city: e.target.value })} required />
            </div>
            <div className="form-group">
              <label>Pincode</label>
              <input className="input-field" value={form.pincode} onChange={(e) => setForm({ ...form, pincode: e.target.value })} />
            </div>
            <p style={{ fontSize: "12px", color: "var(--color-text-light)", marginBottom: "12px" }}>
              Location is pre-filled with test coordinates (Delhi area) — leave as-is unless you know real coordinates.
            </p>
            {error && <p className="error-text">{error}</p>}
            <button type="submit" className="btn btn-primary btn-small">Create Restaurant</button>
          </form>
        )}

        {loading && <p style={{ color: "var(--color-text-light)" }}>Loading...</p>}
        {!loading && restaurants.length === 0 && !showForm && (
          <p style={{ color: "var(--color-text-light)" }}>You don't have any restaurants yet. Create one to get started.</p>
        )}

        <div className="restaurant-grid">
          {restaurants.map((r) => (
            <Link to={`/owner/restaurants/${r.id}`} key={r.id} className="restaurant-card">
              <div className="restaurant-card-image-placeholder">{r.name.charAt(0)}</div>
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

export default OwnerRestaurants;