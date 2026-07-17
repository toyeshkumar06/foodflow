import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosInstance";
import Navbar from "../components/Navbar";
import { useCart } from "../context/CartContext";

function Checkout() {
  const { cart, refreshCart } = useCart();
  const navigate = useNavigate();

  const [addresses, setAddresses] = useState([]);
  const [selectedAddressId, setSelectedAddressId] = useState(null);
  const [showNewAddressForm, setShowNewAddressForm] = useState(false);
  const [newAddress, setNewAddress] = useState({
    label: "Home", addressLine: "", city: "", pincode: "",
    latitude: 28.7041, longitude: 77.1025,
  });

  const [couponCode, setCouponCode] = useState("");
  const [paymentMethod, setPaymentMethod] = useState("UPI");
  const [placing, setPlacing] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!cart.items || cart.items.length === 0) {
      navigate("/restaurants");
      return;
    }
    axiosInstance.get("/addresses").then((res) => {
      setAddresses(res.data);
      if (res.data.length > 0) setSelectedAddressId(res.data[0].id);
      else setShowNewAddressForm(true);
    });
  }, []);

  const handleAddAddress = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const res = await axiosInstance.post("/addresses", { ...newAddress, isDefault: addresses.length === 0 });
      setAddresses([...addresses, res.data]);
      setSelectedAddressId(res.data.id);
      setShowNewAddressForm(false);
    } catch (err) {
      setError(err.response?.data?.message || "Could not save address.");
    }
  };

  const handlePlaceOrder = async () => {
    if (!selectedAddressId) {
      setError("Please select or add a delivery address.");
      return;
    }
    setError("");
    setPlacing(true);
    try {
      const orderBody = { addressId: selectedAddressId };
      if (couponCode.trim()) orderBody.couponCode = couponCode.trim();

      const orderRes = await axiosInstance.post("/orders", orderBody);
      const order = orderRes.data;

      await axiosInstance.post(`/payments/${order.id}`, { method: paymentMethod });

      await refreshCart();
      navigate(`/orders/${order.id}`);
    } catch (err) {
      setError(err.response?.data?.message || "Could not place order.");
    } finally {
      setPlacing(false);
    }
  };

  return (
    <>
      <Navbar />
      <div className="page-container" style={{ paddingTop: "40px", paddingBottom: "60px", maxWidth: "700px" }}>
        <h1 style={{ fontSize: "28px", marginBottom: "24px" }}>Checkout</h1>

        <div className="card" style={{ marginBottom: "20px" }}>
          <h3 style={{ marginBottom: "16px" }}>Delivery Address</h3>

          {addresses.map((addr) => (
            <label key={addr.id} className="address-option">
              <input
                type="radio"
                name="address"
                checked={selectedAddressId === addr.id}
                onChange={() => setSelectedAddressId(addr.id)}
              />
              <div>
                <strong>{addr.label}</strong>
                <p style={{ color: "var(--color-text-light)", fontSize: "14px" }}>
                  {addr.addressLine}, {addr.city} {addr.pincode}
                </p>
              </div>
            </label>
          ))}

          {!showNewAddressForm && (
            <button className="btn btn-secondary btn-small" style={{ marginTop: "12px" }} onClick={() => setShowNewAddressForm(true)}>
              + Add New Address
            </button>
          )}

          {showNewAddressForm && (
            <form onSubmit={handleAddAddress} style={{ marginTop: "16px", borderTop: "1px solid var(--color-border)", paddingTop: "16px" }}>
              <div className="form-group">
                <label>Label</label>
                <input className="input-field" value={newAddress.label} onChange={(e) => setNewAddress({ ...newAddress, label: e.target.value })} />
              </div>
              <div className="form-group">
                <label>Address Line</label>
                <input className="input-field" value={newAddress.addressLine} onChange={(e) => setNewAddress({ ...newAddress, addressLine: e.target.value })} required />
              </div>
              <div className="form-group">
                <label>City</label>
                <input className="input-field" value={newAddress.city} onChange={(e) => setNewAddress({ ...newAddress, city: e.target.value })} required />
              </div>
              <div className="form-group">
                <label>Pincode</label>
                <input className="input-field" value={newAddress.pincode} onChange={(e) => setNewAddress({ ...newAddress, pincode: e.target.value })} />
              </div>
              <p style={{ fontSize: "12px", color: "var(--color-text-light)", marginBottom: "12px" }}>
                Latitude/Longitude are pre-filled with test coordinates (Delhi area) since we don't have real map picking yet — leave as-is for testing.
              </p>
              <button type="submit" className="btn btn-primary btn-small">Save Address</button>
            </form>
          )}
        </div>

        <div className="card" style={{ marginBottom: "20px" }}>
          <h3 style={{ marginBottom: "12px" }}>Coupon Code</h3>
          <input
            className="input-field"
            placeholder="e.g. SAVE20 (optional)"
            value={couponCode}
            onChange={(e) => setCouponCode(e.target.value)}
          />
        </div>

        <div className="card" style={{ marginBottom: "20px" }}>
          <h3 style={{ marginBottom: "12px" }}>Payment Method</h3>
          <div className="payment-options">
            {["UPI", "CARD", "CASH_ON_DELIVERY", "WALLET"].map((method) => (
              <label key={method} className={`payment-option ${paymentMethod === method ? "selected" : ""}`}>
                <input type="radio" name="payment" checked={paymentMethod === method} onChange={() => setPaymentMethod(method)} />
                {method.replace("_", " ")}
              </label>
            ))}
          </div>
        </div>

        <div className="card">
          <div className="cart-total-row" style={{ borderTop: "none", paddingTop: 0, marginTop: 0 }}>
            <span>Items Total</span>
            <span>₹{cart.itemsTotal}</span>
          </div>
          <p style={{ fontSize: "13px", color: "var(--color-text-light)", marginTop: "8px" }}>
            Delivery charge, discount, and final total will be calculated when you place the order (surge pricing and coupon validation happen server-side).
          </p>
        </div>

        {error && <p className="error-text" style={{ marginTop: "16px" }}>{error}</p>}

        <button className="btn btn-primary" style={{ marginTop: "20px" }} onClick={handlePlaceOrder} disabled={placing}>
          {placing ? "Placing Order..." : "Place Order"}
        </button>
      </div>
    </>
  );
}

export default Checkout;