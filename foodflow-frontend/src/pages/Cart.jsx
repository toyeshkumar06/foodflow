import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import { useCart } from "../context/CartContext";

function Cart() {
  const { cart, refreshCart, updateQuantity, removeItem, clearCart } = useCart();
  const navigate = useNavigate();

  useEffect(() => {
    refreshCart();
  }, []);

  const handleCheckout = () => {
    alert("Checkout page is coming in the next phase!");
  };

  return (
    <>
      <Navbar />
      <div className="page-container" style={{ paddingTop: "40px", paddingBottom: "60px", maxWidth: "700px" }}>
        <h1 style={{ fontSize: "28px", marginBottom: "24px" }}>Your Cart</h1>

        {(!cart.items || cart.items.length === 0) && (
          <div className="card" style={{ textAlign: "center", padding: "48px" }}>
            <p style={{ color: "var(--color-text-light)", marginBottom: "16px" }}>Your cart is empty.</p>
            <button className="btn btn-secondary" onClick={() => navigate("/restaurants")}>
              Browse Restaurants
            </button>
          </div>
        )}

        {cart.items && cart.items.length > 0 && (
          <>
            <p style={{ color: "var(--color-text-light)", marginBottom: "16px" }}>
              Ordering from <strong style={{ color: "var(--color-text)" }}>{cart.restaurantName}</strong>
            </p>

            <div className="card">
              {cart.items.map((item) => (
                <div className="cart-row" key={item.cartItemId}>
                  <div>
                    <p className="menu-item-name">{item.foodName}</p>
                    <p className="menu-item-price">₹{item.price} each</p>
                  </div>
                  <div className="cart-qty-controls">
                    <button className="qty-btn" onClick={() => updateQuantity(item.cartItemId, Math.max(1, item.quantity - 1))}>−</button>
                    <span>{item.quantity}</span>
                    <button className="qty-btn" onClick={() => updateQuantity(item.cartItemId, item.quantity + 1)}>+</button>
                  </div>
                  <p className="cart-subtotal">₹{item.subtotal}</p>
                  <button className="remove-btn" onClick={() => removeItem(item.cartItemId)}>✕</button>
                </div>
              ))}

              <div className="cart-total-row">
                <span>Items Total</span>
                <strong>₹{cart.itemsTotal}</strong>
              </div>
            </div>

            <div style={{ display: "flex", gap: "12px", marginTop: "20px" }}>
              <button className="btn btn-secondary" onClick={clearCart}>Clear Cart</button>
              <button className="btn btn-primary" onClick={handleCheckout}>Proceed to Checkout</button>
            </div>
          </>
        )}
      </div>
    </>
  );
}

export default Cart;