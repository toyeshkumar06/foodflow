import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

function Home() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div style={{ maxWidth: "600px", margin: "50px auto", padding: "20px" }}>
      <h2>Welcome to FoodFlow</h2>
      <p>Logged in as: <strong>{user?.email}</strong></p>
      <p>Role: <strong>{user?.role}</strong></p>
      <button onClick={handleLogout} style={{ padding: "10px 20px" }}>Logout</button>
    </div>
  );
}

export default Home;