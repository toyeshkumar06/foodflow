import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
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

  return (
    <>
      <Navbar />
      <div className="page-container" style={{ paddingTop: "40px", paddingBottom: "60px", maxWidth: "600px" }}>
        <button className="btn btn-secondary btn-small" onClick={() => navigate("/agent")} style={{ marginBottom: "20px" }}>
          ← Back to Dashboard
        </button>

        <h1 style={{ fontSize: "28px", marginBottom: "20px" }}>History & Earnings</h1>

        <div className="card analytics-stat" style={{ marginBottom: "24px" }}>
          <p className="analytics-stat-label">Total Earnings</p>
          <p className="analytics-stat-value">₹{earnings}</p>
        </div>

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