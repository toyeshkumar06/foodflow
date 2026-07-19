import { useEffect, useState } from "react";
import axiosInstance from "../../api/axiosInstance";

function AnalyticsTab({ restaurantId }) {
  const [data, setData] = useState(null);

  useEffect(() => {
    axiosInstance.get(`/restaurant-owner/restaurants/${restaurantId}/analytics`).then((res) => setData(res.data));
  }, [restaurantId]);

  if (!data) return <p style={{ color: "var(--color-text-light)" }}>Loading analytics...</p>;

  return (
    <div>
      <div className="analytics-grid">
        <div className="card analytics-stat">
          <p className="analytics-stat-label">Total Revenue</p>
          <p className="analytics-stat-value">₹{data.totalRevenue}</p>
        </div>
        <div className="card analytics-stat">
          <p className="analytics-stat-label">Total Orders</p>
          <p className="analytics-stat-value">{data.totalOrders}</p>
        </div>
      </div>

      <div className="card" style={{ marginTop: "20px" }}>
        <h3 style={{ marginBottom: "12px" }}>Top Selling Items</h3>
        {data.topSellingItems.length === 0 && <p style={{ color: "var(--color-text-light)" }}>No sales yet.</p>}
        {data.topSellingItems.map((item, i) => (
          <div key={i} style={{ display: "flex", justifyContent: "space-between", padding: "8px 0", borderBottom: "1px solid var(--color-border)" }}>
            <span>{item.name}</span>
            <span style={{ color: "var(--color-text-light)" }}>{item.quantitySold} sold</span>
          </div>
        ))}
      </div>

      <div className="card" style={{ marginTop: "20px" }}>
        <h3 style={{ marginBottom: "12px" }}>Last 7 Days</h3>
        {data.last7Days.length === 0 && <p style={{ color: "var(--color-text-light)" }}>No orders in the last 7 days.</p>}
        {data.last7Days.map((day, i) => (
          <div key={i} style={{ display: "flex", justifyContent: "space-between", padding: "8px 0", borderBottom: "1px solid var(--color-border)" }}>
            <span>{day.date}</span>
            <span>{day.orderCount} orders · ₹{day.revenue}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default AnalyticsTab;