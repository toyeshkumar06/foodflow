import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosInstance";
import Navbar from "../components/Navbar";
import MenuTab from "../components/owner/MenuTab";
import OrdersTab from "../components/owner/OrdersTab";
import AnalyticsTab from "../components/owner/AnalyticsTab";

function OwnerRestaurantDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [restaurant, setRestaurant] = useState(null);
  const [activeTab, setActiveTab] = useState("menu");
  const [updatingStatus, setUpdatingStatus] = useState(false);

  const loadRestaurant = () => {
    axiosInstance.get(`/restaurants/${id}`).then((res) => setRestaurant(res.data));
  };

  useEffect(() => { loadRestaurant(); }, [id]);

  const handleStatusChange = async (newStatus) => {
    setUpdatingStatus(true);
    try {
      await axiosInstance.patch(`/restaurant-owner/restaurants/${id}/status`, { status: newStatus });
      loadRestaurant();
    } catch (err) {
      alert(err.response?.data?.message || "Could not update status.");
    } finally {
      setUpdatingStatus(false);
    }
  };

  if (!restaurant) {
    return (<><Navbar /><div className="page-container" style={{ paddingTop: "40px" }}><p style={{ color: "var(--color-text-light)" }}>Loading...</p></div></>);
  }

  return (
    <>
      <Navbar />
      <div className="page-container" style={{ paddingTop: "40px", paddingBottom: "60px" }}>
        <button className="btn btn-secondary btn-small" onClick={() => navigate("/owner/restaurants")} style={{ marginBottom: "20px" }}>
          ← Back to My Restaurants
        </button>

        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: "24px" }}>
          <div>
            <h1 style={{ fontSize: "28px" }}>{restaurant.name}</h1>
            <p style={{ color: "var(--color-text-light)" }}>{restaurant.addressLine}, {restaurant.city}</p>
          </div>
          <select
            className="input-field"
            style={{ width: "auto" }}
            value={restaurant.status}
            disabled={updatingStatus}
            onChange={(e) => handleStatusChange(e.target.value)}
          >
            <option value="OPEN">Open</option>
            <option value="CLOSED">Closed</option>
            <option value="BUSY">Busy</option>
            <option value="HOLIDAY">Holiday</option>
          </select>
        </div>

        <div className="owner-tabs">
          <button className={`owner-tab ${activeTab === "menu" ? "active" : ""}`} onClick={() => setActiveTab("menu")}>Menu</button>
          <button className={`owner-tab ${activeTab === "orders" ? "active" : ""}`} onClick={() => setActiveTab("orders")}>Orders</button>
          <button className={`owner-tab ${activeTab === "analytics" ? "active" : ""}`} onClick={() => setActiveTab("analytics")}>Analytics</button>
        </div>

        <div style={{ marginTop: "24px" }}>
          {activeTab === "menu" && <MenuTab restaurantId={id} />}
          {activeTab === "orders" && <OrdersTab restaurantId={id} />}
          {activeTab === "analytics" && <AnalyticsTab restaurantId={id} />}
        </div>
      </div>
    </>
  );
}

export default OwnerRestaurantDetail;