import { useEffect, useState, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosInstance";
import Navbar from "../components/Navbar";
import RatingModal from "../components/RatingModal";

const FLOW_STEPS = ["PLACED", "ACCEPTED", "PREPARING", "READY_FOR_PICKUP", "PICKED_UP", "ON_THE_WAY", "DELIVERED"];

function OrderDetail() {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [cancelling, setCancelling] = useState(false);
  const [ratingModal, setRatingModal] = useState(null); // { type, label, foodItemId }
  const autoPromptShown = useRef(false);
  const navigate = useNavigate();

  const loadOrder = () => {
    axiosInstance.get("/orders").then((res) => {
      const found = res.data.find((o) => o.id === Number(id));
      setOrder(found);
      setLoading(false);

      // Auto-prompt for a rating the first time we see this order as DELIVERED,
      // so the customer doesn't have to remember to come back and rate manually.
      if (found && found.status === "DELIVERED" && !autoPromptShown.current) {
        autoPromptShown.current = true;
        setRatingModal({ type: "restaurant", label: found.restaurantName });
      }
    });
  };

  useEffect(() => {
    loadOrder();
    const interval = setInterval(loadOrder, 5000);
    return () => clearInterval(interval);
  }, [id]);

  const handleCancel = async () => {
    if (!confirm("Cancel this order?")) return;
    setCancelling(true);
    try {
      await axiosInstance.patch(`/orders/${id}/cancel`);
      loadOrder();
    } catch (err) {
      alert(err.response?.data?.message || "Could not cancel order.");
    } finally {
      setCancelling(false);
    }
  };

  if (loading) {
    return (<><Navbar /><div className="page-container" style={{ paddingTop: "40px" }}><p style={{ color: "var(--color-text-light)" }}>Loading...</p></div></>);
  }
  if (!order) {
    return (<><Navbar /><div className="page-container" style={{ paddingTop: "40px" }}><p>Order not found.</p></div></>);
  }

  const isTerminal = ["CANCELLED", "REJECTED"].includes(order.status);
  const currentStepIndex = FLOW_STEPS.indexOf(order.status);
  const canCancel = order.status === "PLACED" || order.status === "ACCEPTED";
  const canRate = order.status === "DELIVERED";

  return (
    <>
      <Navbar />
      <div className="page-container" style={{ paddingTop: "40px", paddingBottom: "60px", maxWidth: "600px" }}>
        <button className="btn btn-secondary btn-small" onClick={() => navigate("/orders")} style={{ marginBottom: "20px" }}>
          ← Back to Orders
        </button>

        <h1 style={{ fontSize: "26px" }}>{order.restaurantName}</h1>
        <p style={{ color: "var(--color-text-light)", marginBottom: "24px" }}>Order #{order.id}</p>

        {isTerminal ? (
          <div className="error-text">This order was {order.status.toLowerCase()}.</div>
        ) : (
          <div className="tracking-timeline">
            {FLOW_STEPS.map((step, i) => (
              <div key={step} className={`tracking-step ${i <= currentStepIndex ? "done" : ""}`}>
                <div className="tracking-dot"></div>
                <span>{step.replace(/_/g, " ")}</span>
              </div>
            ))}
          </div>
        )}

        {order.etaMinutes && !isTerminal && order.status !== "DELIVERED" && (
          <p style={{ marginTop: "16px", color: "var(--color-text-light)" }}>
            Estimated delivery time: <strong style={{ color: "var(--color-text)" }}>{order.etaMinutes} minutes</strong>
          </p>
        )}
        {order.deliveryAgentName && (
          <p style={{ color: "var(--color-text-light)" }}>
            Delivery agent: <strong style={{ color: "var(--color-text)" }}>{order.deliveryAgentName}</strong>
          </p>
        )}

        <div className="card" style={{ marginTop: "24px" }}>
          <h3 style={{ marginBottom: "12px" }}>Order Summary</h3>
          {order.items.map((item, i) => (
            <div key={i} style={{ display: "flex", justifyContent: "space-between", marginBottom: "6px", fontSize: "14px" }}>
              <span>{item.quantity} × {item.foodName}</span>
              <span>₹{(item.price * item.quantity).toFixed(2)}</span>
            </div>
          ))}
          <div className="cart-total-row" style={{ fontSize: "14px", marginTop: "12px" }}>
            <span>Items Total</span><span>₹{order.itemsTotal}</span>
          </div>
          <div style={{ display: "flex", justifyContent: "space-between", fontSize: "14px", marginTop: "4px" }}>
            <span>Delivery Charge</span><span>₹{order.deliveryCharge}</span>
          </div>
          {order.discountAmount > 0 && (
            <div style={{ display: "flex", justifyContent: "space-between", fontSize: "14px", marginTop: "4px", color: "var(--color-success)" }}>
              <span>Discount ({order.appliedCouponCode})</span><span>−₹{order.discountAmount}</span>
            </div>
          )}
          <div className="cart-total-row">
            <span>Grand Total</span><strong>₹{order.grandTotal}</strong>
          </div>
        </div>

        <p style={{ marginTop: "16px", fontSize: "14px", color: "var(--color-text-light)" }}>
          Delivering to: {order.deliveryAddressLine}
        </p>

        {canCancel && (
          <button className="btn btn-secondary" style={{ marginTop: "20px" }} onClick={handleCancel} disabled={cancelling}>
            {cancelling ? "Cancelling..." : "Cancel Order"}
          </button>
        )}

        {canRate && (
          <div className="card" style={{ marginTop: "20px" }}>
            <h3 style={{ marginBottom: "12px" }}>Rate your experience</h3>
            <div style={{ display: "flex", flexDirection: "column", gap: "8px" }}>
              <button className="btn btn-secondary btn-small" onClick={() => setRatingModal({ type: "restaurant", label: order.restaurantName })}>
                Rate Restaurant
              </button>
              {order.deliveryAgentName && (
                <button className="btn btn-secondary btn-small" onClick={() => setRatingModal({ type: "delivery-agent", label: order.deliveryAgentName })}>
                  Rate Delivery Agent
                </button>
              )}
              {order.items.map((item, i) => (
                <button
                  key={i}
                  className="btn btn-secondary btn-small"
                  onClick={() => setRatingModal({ type: "food", label: item.foodName, foodItemId: item.foodItemId })}
                >
                  Rate {item.foodName}
                </button>
              ))}
            </div>
          </div>
        )}

        {ratingModal && (
          <RatingModal
            orderId={order.id}
            targetLabel={ratingModal.label}
            ratingType={ratingModal.type}
            foodItemId={ratingModal.foodItemId}
            onClose={() => setRatingModal(null)}
            onSubmitted={() => alert("Thanks for your rating!")}
          />
        )}
      </div>
    </>
  );
}

export default OrderDetail;