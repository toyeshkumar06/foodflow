import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import axiosInstance from "../api/axiosInstance";
import Navbar from "../components/Navbar";

const NEXT_ACTION = {
  PICKED_UP: { next: "ON_THE_WAY", label: "Mark On The Way" },
  ON_THE_WAY: { next: "DELIVERED", label: "Mark Delivered" },
};

function AgentDashboard() {
  const [profile, setProfile] = useState(null);
  const [currentOrder, setCurrentOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [toggling, setToggling] = useState(false);
  const [updating, setUpdating] = useState(false);
  const [error, setError] = useState("");

  const TEST_LAT = 28.62;
  const TEST_LNG = 77.21;

  const loadData = async () => {
    try {
      const profileRes = await axiosInstance.get("/delivery/profile");
      setProfile(profileRes.data);
    } catch (err) {
      console.error("Failed to load profile", err);
    }
    try {
      const orderRes = await axiosInstance.get("/delivery/orders/current");
      setCurrentOrder(orderRes.status === 204 ? null : orderRes.data);
    } catch (err) {
      setCurrentOrder(null);
    }
    setLoading(false);
  };

  useEffect(() => {
    loadData();
    const interval = setInterval(loadData, 6000);
    return () => clearInterval(interval);
  }, []);

  const handleGoOnline = async () => {
    setToggling(true);
    setError("");
    try {
      const res = await axiosInstance.post("/delivery/go-online", { latitude: TEST_LAT, longitude: TEST_LNG });
      setProfile(res.data);
    } catch (err) {
      setError(err.response?.data?.message || "Could not go online.");
    } finally {
      setToggling(false);
    }
  };

  const handleGoOffline = async () => {
    setToggling(true);
    setError("");
    try {
      await axiosInstance.post("/delivery/go-offline");
      setProfile((prev) => (prev ? { ...prev, online: false } : null));
    } catch (err) {
      setError(err.response?.data?.message || "Could not go offline — you may have an active delivery.");
    } finally {
      setToggling(false);
    }
  };

  const handleAccept = async () => {
    setUpdating(true);
    try {
      await axiosInstance.post(`/delivery/orders/${currentOrder.id}/accept`);
      loadData();
    } catch (err) {
      alert(err.response?.data?.message || "Could not accept.");
    } finally {
      setUpdating(false);
    }
  };

  const handleReject = async () => {
    if (!confirm("Reject this delivery? It will be reassigned to another agent.")) return;
    setUpdating(true);
    try {
      await axiosInstance.post(`/delivery/orders/${currentOrder.id}/reject`);
      loadData();
    } catch (err) {
      alert(err.response?.data?.message || "Could not reject.");
    } finally {
      setUpdating(false);
    }
  };

  const handleStatusUpdate = async (newStatus) => {
    setUpdating(true);
    try {
      await axiosInstance.patch(`/delivery/orders/${currentOrder.id}/status`, { status: newStatus });
      loadData();
    } catch (err) {
      alert(err.response?.data?.message || "Could not update status.");
    } finally {
      setUpdating(false);
    }
  };

  if (loading) {
    return (<><Navbar /><div className="page-container" style={{ paddingTop: "40px" }}><p style={{ color: "var(--color-text-light)" }}>Loading...</p></div></>);
  }

  const isOnline = profile?.online ?? false;
  // Accept/Reject should be available any time there's an active, unconfirmed order —
  // not just during PREPARING, since polling delays could mean we see it later than that.
  const needsConfirmation = currentOrder && !currentOrder.agentConfirmed && currentOrder.status !== "DELIVERED";

  return (
    <>
      <Navbar />
      <div className="page-container" style={{ paddingTop: "40px", paddingBottom: "60px", maxWidth: "600px" }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "24px" }}>
          <h1 style={{ fontSize: "28px" }}>Delivery Dashboard</h1>
          <Link to="/agent/history" className="navbar-link">History & Earnings →</Link>
        </div>

        <div className="card" style={{ marginBottom: "20px", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <div>
            <p style={{ fontWeight: 600 }}>{isOnline ? "You're Online" : "You're Offline"}</p>
            <p style={{ fontSize: "13px", color: "var(--color-text-light)" }}>
              {currentOrder ? "You have an active delivery — finish it before going offline" : isOnline ? "Waiting for order assignments" : "Go online to start receiving orders"}
            </p>
          </div>
          <button
            className={`btn ${isOnline ? "btn-secondary" : "btn-primary"} btn-small`}
            onClick={isOnline ? handleGoOffline : handleGoOnline}
            disabled={toggling}
          >
            {toggling ? "..." : isOnline ? "Go Offline" : "Go Online"}
          </button>
        </div>

        {error && <p className="error-text">{error}</p>}

        {!currentOrder && (
          <div className="card" style={{ textAlign: "center", padding: "40px" }}>
            <p style={{ color: "var(--color-text-light)" }}>No active delivery right now.</p>
          </div>
        )}

        {currentOrder && (
          <div className="card">
            <h3 style={{ marginBottom: "8px" }}>Order #{currentOrder.id}</h3>
            <p style={{ color: "var(--color-text-light)", marginBottom: "4px" }}>
              From: <strong style={{ color: "var(--color-text)" }}>{currentOrder.restaurantName}</strong>
            </p>
            <p style={{ color: "var(--color-text-light)", marginBottom: "12px" }}>
              Deliver to: {currentOrder.deliveryAddressLine}
            </p>
            <span className="status-badge status-badge-active">{currentOrder.status.replace(/_/g, " ")}</span>

            {needsConfirmation && (
              <div style={{ display: "flex", gap: "10px", marginTop: "16px" }}>
                <button className="btn btn-primary btn-small" onClick={handleAccept} disabled={updating}>Accept</button>
                <button className="btn btn-secondary btn-small" onClick={handleReject} disabled={updating}>Reject</button>
              </div>
            )}

            {!needsConfirmation && currentOrder.status === "READY_FOR_PICKUP" && (
              <button className="btn btn-primary btn-small" style={{ marginTop: "16px" }} onClick={() => handleStatusUpdate("PICKED_UP")} disabled={updating}>
                Mark Picked Up
              </button>
            )}

            {!needsConfirmation && NEXT_ACTION[currentOrder.status] && (
              <button className="btn btn-primary btn-small" style={{ marginTop: "16px" }} onClick={() => handleStatusUpdate(NEXT_ACTION[currentOrder.status].next)} disabled={updating}>
                {NEXT_ACTION[currentOrder.status].label}
              </button>
            )}
          </div>
        )}
      </div>
    </>
  );
}

export default AgentDashboard;