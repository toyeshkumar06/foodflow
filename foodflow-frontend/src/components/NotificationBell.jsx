import { useEffect, useState, useRef } from "react";
import axiosInstance from "../api/axiosInstance";

function NotificationBell() {
  const [notifications, setNotifications] = useState([]);
  const [open, setOpen] = useState(false);
  const dropdownRef = useRef(null);

  const loadNotifications = () => {
    axiosInstance.get("/notifications").then((res) => setNotifications(res.data));
  };

  useEffect(() => {
    loadNotifications();
    const interval = setInterval(loadNotifications, 15000); // poll every 15s
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    function handleClickOutside(e) {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const unreadCount = notifications.filter((n) => !n.read).length;

  const handleMarkRead = async (id) => {
    try {
      await axiosInstance.patch(`/notifications/${id}/read`);
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, read: true } : n))
      );
    } catch (err) {
      console.error("Failed to mark notification as read", err);
    }
  };

  return (
    <div className="notification-bell-wrapper" ref={dropdownRef}>
      <button className="cart-icon-link" onClick={() => setOpen(!open)}>
        🔔
        {unreadCount > 0 && <span className="cart-badge">{unreadCount}</span>}
      </button>

      {open && (
        <div className="notification-dropdown">
          <h4 style={{ padding: "12px 16px", borderBottom: "1px solid var(--color-border)" }}>Notifications</h4>
          {notifications.length === 0 && (
            <p style={{ padding: "16px", color: "var(--color-text-light)", fontSize: "14px" }}>No notifications yet.</p>
          )}
          {notifications.map((n) => (
            <div
              key={n.id}
              className={`notification-item ${!n.read ? "unread" : ""}`}
              onClick={() => !n.read && handleMarkRead(n.id)}
            >
              <p style={{ fontWeight: 600, fontSize: "13px" }}>{n.title}</p>
              <p style={{ fontSize: "12px", color: "var(--color-text-light)" }}>{n.message}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default NotificationBell;