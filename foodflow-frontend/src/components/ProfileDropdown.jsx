import { useState, useRef, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import axiosInstance from "../api/axiosInstance";
import Avatar from "./Avatar";

function ProfileDropdown() {
  const { user, logout } = useAuth();
  const [open, setOpen] = useState(false);
  const [stats, setStats] = useState(null);
  const wrapperRef = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (user.role === "RESTAURANT_OWNER") {
      axiosInstance.get("/restaurant-owner/quick-stats").then((res) => setStats(res.data)).catch(() => {});
    } else if (user.role === "DELIVERY_AGENT") {
      axiosInstance.get("/delivery/quick-stats").then((res) => setStats(res.data)).catch(() => {});
    }
  }, [user.role]);

  useEffect(() => {
    function handleClickOutside(e) {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target)) setOpen(false);
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const formatRole = (role) => (role ? role.replace("_", " ").toLowerCase() : "");

  return (
    <div
      className="profile-dropdown-wrapper"
      ref={wrapperRef}
      onMouseEnter={() => setOpen(true)}
      onMouseLeave={() => setOpen(false)}
    >
      <button className="profile-trigger" onClick={() => setOpen(!open)}>
        <Avatar name={user.name} size={34} />
      </button>

      {open && (
        <div className="profile-dropdown">
          <div className="profile-dropdown-header">
            <Avatar name={user.name} size={40} />
            <div>
              <p className="navbar-name">{user.name}</p>
              <span className="navbar-role-badge">{formatRole(user.role)}</span>
            </div>
          </div>

          {user.role === "CUSTOMER" && (
            <>
              <div className="profile-dropdown-divider"></div>
              <Link to="/collection?tab=favorites" className="profile-dropdown-item" onClick={() => setOpen(false)}>
                ⭐ My Collection
              </Link>
              <Link to="/collection?tab=dishes" className="profile-dropdown-item" onClick={() => setOpen(false)}>
                🍽️ Liked Dishes
              </Link>
            </>
          )}

          {user.role === "RESTAURANT_OWNER" && stats && (
            <>
              <div className="profile-dropdown-divider"></div>
              <div className="profile-dropdown-stats">
                <p className="stat-line"><strong>Today:</strong> {stats.todayOrders} orders · ₹{stats.todayRevenue}</p>
                {stats.topRatedItemName && (
                  <p className="stat-line"><strong>Top Rated:</strong> {stats.topRatedItemName} ★{stats.topRatedItemRating}</p>
                )}
              </div>
            </>
          )}

          {user.role === "DELIVERY_AGENT" && stats && (
            <>
              <div className="profile-dropdown-divider"></div>
              <div className="profile-dropdown-stats">
                <p className="stat-line"><strong>Today:</strong> {stats.deliveriesToday} deliveries · ₹{stats.todayEarnings} earned</p>
                <p className="stat-line"><strong>Total Deliveries:</strong> {stats.totalDeliveries}</p>
              </div>
            </>
          )}

          <div className="profile-dropdown-divider"></div>
          <button className="profile-dropdown-item profile-dropdown-logout" onClick={handleLogout}>
            ↪ Logout
          </button>
        </div>
      )}
    </div>
  );
}

export default ProfileDropdown;