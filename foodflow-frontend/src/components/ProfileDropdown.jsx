import { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import Avatar from "./Avatar";

function ProfileDropdown() {
  const { user, logout } = useAuth();
  const [open, setOpen] = useState(false);
  const wrapperRef = useRef(null);
  const navigate = useNavigate();

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
              <button className="profile-dropdown-item" onClick={() => alert("Coming soon — bookmark your favorite restaurants!")}>
                ⭐ My Collection
              </button>
              <button className="profile-dropdown-item" onClick={() => alert("Coming soon — dishes you rated 4+ stars will show here!")}>
                🍽️ Liked Dishes
              </button>
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