import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";
import { useNavigate, Link } from "react-router-dom";
import NotificationBell from "./NotificationBell";

function Navbar() {
  const { user, logout } = useAuth();
  const { itemCount } = useCart();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const formatRole = (role) => {
    if (!role) return "";
    return role.replace("_", " ").toLowerCase();
  };

  const logoLink = user?.role === "RESTAURANT_OWNER" ? "/owner/restaurants" : "/restaurants";

  return (
    <nav className="navbar">
      <div className="navbar-inner">
        <Link to={logoLink} className="navbar-logo">FoodFlow</Link>
        {user && (
          <div className="navbar-right">
            {user.role === "CUSTOMER" && (
              <>
                <Link to="/orders" className="navbar-link">My Orders</Link>
                <Link to="/cart" className="cart-icon-link">
                  🛒
                  {itemCount > 0 && <span className="cart-badge">{itemCount}</span>}
                </Link>
                <NotificationBell />
              </>
            )}
            {user.role === "RESTAURANT_OWNER" && (
              <Link to="/owner/restaurants" className="navbar-link">My Restaurants</Link>
            )}
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