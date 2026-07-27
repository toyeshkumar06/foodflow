import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer } from "recharts";
import axiosInstance from "../api/axiosInstance";
import Navbar from "../components/Navbar";

function AgentHistory() {
  const [history, setHistory] = useState([]);
  const [earnings, setEarnings] = useState(0);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    Promise.all([
      axiosInstance.get("/delivery/orders/history"),
      axiosInstance.get("/delivery/earnings"),
    ]).then(([historyRes, earningsRes]) => {
      setHistory(historyRes.data);
      setEarnings(earningsRes.data);
      setLoading(false);
    });
  }, []);

  // Group deliveries by date for a simple bar chart of daily order counts
  const chartData = Object.entries(
    history.reduce((acc, order) => {
      const date = new Date(order.createdAt).toISOString().slice(5, 10);
      acc[date] = (acc[date] || 0) + 1;
      return acc;
    }, {})
  ).map(([date, count]) => ({ date, deliveries: count }));

  return (
    <>
      <Navbar />
      <div className="page-container" style={{ paddingTop: "40px", paddingBottom: "60px", maxWidth: "600px" }}>
        <button className="btn btn-secondary btn-small" onClick={() => navigate("/agent")} style={{ marginBottom: "20px" }}>
          ← Back to Dashboard
        </button>

        <h1 style={{ fontSize: "28px", marginBottom: "20px" }}>History & Earnings</h1>

        <div className="analytics-grid" style={{ marginBottom: "24px" }}>
          <div className="card analytics-stat">
            <p className="analytics-stat-label">Total Earnings</p>
            <p className="analytics-stat-value">₹{earnings}</p>
          </div>
          <div className="card analytics-stat">
            <p className="analytics-stat-label">Total Deliveries</p>
            <p className="analytics-stat-value">{history.length}</p>
          </div>
        </div>

        {chartData.length > 0 && (
          <div className="card" style={{ marginBottom: "24px" }}>
            <h3 style={{ marginBottom: "16px" }}>Deliveries by Day</h3>
            <ResponsiveContainer width="100%" height={200}>
              <BarChart data={chartData}>
                <XAxis dataKey="date" stroke="var(--color-text-light)" fontSize={12} />
                <YAxis stroke="var(--color-text-light)" fontSize={12} allowDecimals={false} />
                <Tooltip contentStyle={{ background: "var(--color-card)", border: "1px solid var(--color-border)", borderRadius: "8px" }} />
                <Bar dataKey="deliveries" fill="#4ADE9E" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        )}

        <h3 style={{ marginBottom: "12px" }}>Delivered Orders</h3>
        {loading && <p style={{ color: "var(--color-text-light)" }}>Loading...</p>}
        {!loading && history.length === 0 && <p style={{ color: "var(--color-text-light)" }}>No deliveries yet.</p>}

        <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
          {history.map((order) => (
            <div className="order-row-card" key={order.id} style={{ cursor: "default" }}>
              <div>
                <p style={{ fontWeight: 600 }}>{order.restaurantName}</p>
                <p style={{ fontSize: "13px", color: "var(--color-text-light)" }}>
                  Order #{order.id} · {new Date(order.createdAt).toLocaleDateString()}
                </p>
              </div>
              <span className="status-badge status-badge-success">Delivered</span>
            </div>
          ))}
        </div>
      </div>
    </>
  );
}

export default AgentHistory;