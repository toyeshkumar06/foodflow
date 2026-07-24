import { useCart } from "../context/CartContext";
import { useAuth } from "../context/AuthContext";
import { Link } from "react-router-dom";
import NotificationBell from "./NotificationBell";
import ProfileDropdown from "./ProfileDropdown";

function Navbar() {
  const { user } = useAuth();
  const { itemCount } = useCart();

  const logoLink =
    user?.role === "RESTAURANT_OWNER" ? "/owner/restaurants" :
    user?.role === "DELIVERY_AGENT" ? "/agent" :
    user?.role === "ADMIN" ? "/admin" :
    "/restaurants";

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
              <>
                <Link to="/owner/restaurants" className="navbar-link">My Restaurants</Link>
                <NotificationBell />
              </>
            )}
            {user.role === "DELIVERY_AGENT" && (
              <Link to="/agent" className="navbar-link">Dashboard</Link>
            )}
            {user.role === "ADMIN" && (
              <Link to="/admin" className="navbar-link">Dashboard</Link>
            )}
            <ProfileDropdown />
          </div>
        )}
      </div>
    </nav>
  );
}

export default Navbar;