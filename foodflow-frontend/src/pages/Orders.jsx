import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import axiosInstance from "../api/axiosInstance";
import Navbar from "../components/Navbar";

function statusColor(status) {
  if (status === "DELIVERED") return "status-badge-success";
  if (status === "CANCELLED" || status === "REJECTED") return "status-badge-error";
  return "status-badge-active";
}

function Orders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axiosInstance.get("/orders")
      .then((res) => setOrders(res.data))
      .finally(() => setLoading(false));
  }, []);

  return (
    <>
      <Navbar />
      <div className="page-container" style={{ paddingTop: "40px", paddingBottom: "60px", maxWidth: "700px" }}>
        <h1 style={{ fontSize: "28px", marginBottom: "24px" }}>My Orders</h1>

        {loading && <p style={{ color: "var(--color-text-light)" }}>Loading...</p>}
        {!loading && orders.length === 0 && <p style={{ color: "var(--color-text-light)" }}>No orders yet.</p>}

        <div style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
          {orders.map((order) => (
            <Link to={`/orders/${order.id}`} key={order.id} className="order-row-card">
              <div>
                <p style={{ fontWeight: 600 }}>{order.restaurantName}</p>
                <p style={{ fontSize: "13px", color: "var(--color-text-light)" }}>
                  Order #{order.id} · {new Date(order.createdAt).toLocaleDateString()}
                </p>
              </div>
              <div style={{ textAlign: "right" }}>
                <p style={{ fontWeight: 600 }}>₹{order.grandTotal}</p>
                <span className={`status-badge ${statusColor(order.status)}`}>{order.status.replace(/_/g, " ")}</span>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </>
  );
}

export default Orders;