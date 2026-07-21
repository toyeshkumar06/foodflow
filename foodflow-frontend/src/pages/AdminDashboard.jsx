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
            <div className="analytics-grid">
              <div className="card analytics-stat">
                <p className="analytics-stat-label">Total Revenue (Delivered)</p>
                <p className="analytics-stat-value">₹{overview.totalRevenue}</p>
              </div>
              <div className="card analytics-stat">
                <p className="analytics-stat-label">Total Orders (Delivered)</p>
                <p className="analytics-stat-value">{overview.totalOrders}</p>
              </div>
            </div>

            <div className="card" style={{ marginTop: "20px" }}>
              <h3 style={{ marginBottom: "12px" }}>Platform Highlights</h3>
              <div style={{ display: "flex", justifyContent: "space-between", padding: "10px 0", borderBottom: "1px solid var(--color-border)" }}>
                <span style={{ color: "var(--color-text-light)" }}>Most Popular Restaurant</span>
                <strong>{overview.mostPopularRestaurant}</strong>
              </div>
              <div style={{ display: "flex", justifyContent: "space-between", padding: "10px 0", borderBottom: "1px solid var(--color-border)" }}>
                <span style={{ color: "var(--color-text-light)" }}>Most Popular Food</span>
                <strong>{overview.mostPopularFood}</strong>
              </div>
              <div style={{ display: "flex", justifyContent: "space-between", padding: "10px 0" }}>
                <span style={{ color: "var(--color-text-light)" }}>Most Active Customer</span>
                <strong>{overview.mostActiveCustomer}</strong>
              </div>
            </div>
          </>
        )}
      </div>
    </>
  );
}

export default AdminDashboard;