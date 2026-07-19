import { useEffect, useState } from "react";
import axiosInstance from "../../api/axiosInstance";

const NEXT_STATUS_OPTIONS = {
  PLACED: ["ACCEPTED", "REJECTED", "CANCELLED"],
  ACCEPTED: ["PREPARING", "CANCELLED"],
  PREPARING: ["READY_FOR_PICKUP"],
};

function OrdersTab({ restaurantId }) {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [updatingId, setUpdatingId] = useState(null);

  const loadOrders = () => {
    axiosInstance.get(`/restaurant-owner/restaurants/${restaurantId}/orders`)
      .then((res) => setOrders(res.data))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadOrders();
    const interval = setInterval(loadOrders, 8000);
    return () => clearInterval(interval);
  }, [restaurantId]);

  const handleStatusChange = async (orderId, newStatus) => {
    setUpdatingId(orderId);
    try {
      await axiosInstance.patch(`/restaurant-owner/orders/${orderId}/status`, { status: newStatus });
      loadOrders();
    } catch (err) {
      alert(err.response?.data?.message || "Could not update order status.");
    } finally {
      setUpdatingId(null);
    }
  };

  if (loading) return <p style={{ color: "var(--color-text-light)" }}>Loading orders...</p>;
  if (orders.length === 0) return <p style={{ color: "var(--color-text-light)" }}>No orders yet.</p>;

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
      {orders.map((order) => {
        const options = NEXT_STATUS_OPTIONS[order.status] || [];
        return (
          <div className="card" key={order.id}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "10px" }}>
              <div>
                <p style={{ fontWeight: 600 }}>Order #{order.id}</p>
                <p style={{ fontSize: "13px", color: "var(--color-text-light)" }}>
                  {new Date(order.createdAt).toLocaleString()}
                </p>
              </div>
              <span className="status-badge status-badge-active">{order.status.replace(/_/g, " ")}</span>
            </div>

            {order.items.map((item, i) => (
              <p key={i} style={{ fontSize: "14px" }}>{item.quantity} × {item.foodName}</p>
            ))}

            <p style={{ fontWeight: 600, marginTop: "8px" }}>Total: ₹{order.grandTotal}</p>
            <p style={{ fontSize: "13px", color: "var(--color-text-light)" }}>Deliver to: {order.deliveryAddressLine}</p>

            {options.length > 0 && (
              <div style={{ display: "flex", gap: "8px", marginTop: "12px" }}>
                {options.map((status) => (
                  <button
                    key={status}
                    className="btn btn-secondary btn-small"
                    disabled={updatingId === order.id}
                    onClick={() => handleStatusChange(order.id, status)}
                  >
                    {status.replace(/_/g, " ")}
                  </button>
                ))}
              </div>
            )}
          </div>
        );
      })}
    </div>
  );
}

export default OrdersTab;