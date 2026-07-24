import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const SLOGANS = [
  "Craving something today?",
  "How about some spicy masala tonight?",
  "Sushi cravings? We've got you.",
  "Comfort food, delivered fast.",
  "Your next favorite meal is one tap away.",
  "Hungry? Let's fix that.",
  "From dosas to burgers — all in one place.",
  "Good food, good mood.",
];

function Welcome() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [slogan] = useState(() => SLOGANS[Math.floor(Math.random() * SLOGANS.length)]);

  useEffect(() => {
    if (user?.role === "CUSTOMER") navigate("/restaurants");
    else if (user?.role === "RESTAURANT_OWNER") navigate("/owner/restaurants");
    else if (user?.role === "DELIVERY_AGENT") navigate("/agent");
    else if (user?.role === "ADMIN") navigate("/admin");
  }, [user]);

  if (user) {
    return (
      <div className="centered-page">
        <p style={{ color: "var(--color-text-light)" }}>Loading your dashboard...</p>
      </div>
    );
  }

  return (
    <div className="welcome-hero">
      <div className="welcome-content">
        <h1 className="welcome-logo">FoodFlow</h1>
        <p className="welcome-slogan">{slogan}</p>
        <div className="welcome-actions">
          <Link to="/login" className="btn btn-primary">Login</Link>
          <Link to="/register" className="btn btn-secondary welcome-btn-light">Create Account</Link>
        </div>
      </div>
      <p className="welcome-credit">Maintained by Toyesh Kumar</p>
    </div>
  );
}

export default Welcome;