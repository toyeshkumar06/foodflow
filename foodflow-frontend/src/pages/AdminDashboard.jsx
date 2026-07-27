import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import axiosInstance from "../api/axiosInstance";
import Navbar from "../components/Navbar";

function AdminDashboard() {
  const [overview, setOverview] = useState(null);

  useEffect(() => {
    axiosInstance.get("/admin/analytics/overview").then((res) => setOverview(res.data));
  }, []);

  return (
    <>
      <Navbar />
      <div className="page-container" style={{ paddingTop: "40px", paddingBottom: "60px" }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "24px" }}>
          <h1 style={{ fontSize: "28px" }}>Admin Dashboard</h1>
          <Link to="/admin/coupons" className="navbar-link">Manage Coupons →</Link>
        </div>

        {!overview && <p style={{ color: "var(--color-text-light)" }}>Loading platform overview...</p>}

        {overview && (
          <>
            <div className="admin-stat-grid">
              <div className="card admin-stat-card">
                <div className="admin-stat-icon" style={{ background: "rgba(255, 107, 74, 0.15)" }}>💰</div>
                <div>
                  <p className="analytics-stat-label">Total Revenue</p>
                  <p className="analytics-stat-value">₹{overview.totalRevenue}</p>
                </div>
              </div>
              <div className="card admin-stat-card">
                <div className="admin-stat-icon" style={{ background: "rgba(74, 222, 158, 0.15)" }}>📦</div>
                <div>
                  <p className="analytics-stat-label">Total Orders</p>
                  <p className="analytics-stat-value">{overview.totalOrders}</p>
                </div>
              </div>
            </div>

            <div className="card" style={{ marginTop: "20px" }}>
              <h3 style={{ marginBottom: "16px" }}>Platform Highlights</h3>
              <div className="highlight-row">
                <span className="highlight-icon">🏆</span>
                <div>
                  <p className="stat-line" style={{ marginBottom: "2px" }}>Most Popular Restaurant</p>
                  <p style={{ fontWeight: 600 }}>{overview.mostPopularRestaurant}</p>
                </div>
              </div>
              <div className="highlight-row">
                <span className="highlight-icon">🍽️</span>
                <div>
                  <p className="stat-line" style={{ marginBottom: "2px" }}>Most Popular Food</p>
                  <p style={{ fontWeight: 600 }}>{overview.mostPopularFood}</p>
                </div>
              </div>
              <div className="highlight-row" style={{ borderBottom: "none" }}>
                <span className="highlight-icon">👤</span>
                <div>
                  <p className="stat-line" style={{ marginBottom: "2px" }}>Most Active Customer</p>
                  <p style={{ fontWeight: 600 }}>{overview.mostActiveCustomer}</p>
                </div>
              </div>
            </div>
          </>
        )}
      </div>
    </>
  );
}

export default AdminDashboard;