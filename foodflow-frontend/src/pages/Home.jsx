import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function Home() {
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (user?.role === "CUSTOMER") navigate("/restaurants");
    else if (user?.role === "RESTAURANT_OWNER") navigate("/owner/restaurants");
    else if (user?.role === "DELIVERY_AGENT") navigate("/agent");
    else if (user?.role === "ADMIN") navigate("/admin");
  }, [user]);

  return (
    <div className="centered-page">
      <p style={{ color: "var(--color-text-light)" }}>Loading your dashboard...</p>
    </div>
  );
}

export default Home;