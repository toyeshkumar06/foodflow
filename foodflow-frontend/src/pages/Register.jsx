import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import axiosInstance from "../api/axiosInstance";
import { useAuth } from "../context/AuthContext";

function Register() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("CUSTOMER");
  const [error, setError] = useState("");
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const response = await axiosInstance.post("/auth/register", { name, email, password, role });
      login(response.data);
      navigate("/");
    } catch (err) {
      setError(err.response?.data?.message || "Registration failed.");
    }
  };

  return (
    <div style={{ maxWidth: "400px", margin: "50px auto", padding: "20px" }}>
      <h2>Create a FoodFlow Account</h2>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: "10px" }}>
          <label>Name</label><br />
          <input value={name} onChange={(e) => setName(e.target.value)} required style={{ width: "100%", padding: "8px" }} />
        </div>
        <div style={{ marginBottom: "10px" }}>
          <label>Email</label><br />
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required style={{ width: "100%", padding: "8px" }} />
        </div>
        <div style={{ marginBottom: "10px" }}>
          <label>Password</label><br />
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required style={{ width: "100%", padding: "8px" }} />
        </div>
        <div style={{ marginBottom: "10px" }}>
          <label>I am a</label><br />
          <select value={role} onChange={(e) => setRole(e.target.value)} style={{ width: "100%", padding: "8px" }}>
            <option value="CUSTOMER">Customer</option>
            <option value="RESTAURANT_OWNER">Restaurant Owner</option>
            <option value="DELIVERY_AGENT">Delivery Agent</option>
          </select>
        </div>
        {error && <p style={{ color: "red" }}>{error}</p>}
        <button type="submit" style={{ padding: "10px 20px" }}>Register</button>
      </form>
      <p>Already have an account? <Link to="/login">Login here</Link></p>
    </div>
  );
}

export default Register;