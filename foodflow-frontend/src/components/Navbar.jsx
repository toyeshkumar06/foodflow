import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const formatRole = (role) => {
    if (!role) return "";
    return role.replace("_", " ").toLowerCase();
  };

  return (
    <nav className="navbar">
      <div className="navbar-inner">
        <span className="navbar-logo">FoodFlow</span>
        {user && (
          <div className="navbar-right">
            <span className="navbar-role-badge">{formatRole(user.role)}</span>
            <span className="navbar-email">{user.email}</span>
            <button className="btn btn-secondary" onClick={handleLogout}>Logout</button>
          </div>
        )}
      </div>
    </nav>
  );
}

export default Navbar;