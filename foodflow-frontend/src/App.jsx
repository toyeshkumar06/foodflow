import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { useAuth } from "./context/AuthContext";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Home from "./pages/Home";
import Restaurants from "./pages/Restaurants";
import RestaurantMenu from "./pages/RestaurantMenu";
import Cart from "./pages/Cart";
import Checkout from "./pages/Checkout";
import Orders from "./pages/Orders";
import OrderDetail from "./pages/OrderDetail";
import OwnerRestaurants from "./pages/OwnerRestaurants";
import OwnerRestaurantDetail from "./pages/OwnerRestaurantDetail";

function ProtectedRoute({ children }) {
  const { user } = useAuth();
  return user ? children : <Navigate to="/login" />;
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/" element={<ProtectedRoute><Home /></ProtectedRoute>} />
        <Route path="/restaurants" element={<ProtectedRoute><Restaurants /></ProtectedRoute>} />
        <Route path="/restaurants/:id" element={<ProtectedRoute><RestaurantMenu /></ProtectedRoute>} />
        <Route path="/cart" element={<ProtectedRoute><Cart /></ProtectedRoute>} />
        <Route path="/checkout" element={<ProtectedRoute><Checkout /></ProtectedRoute>} />
        <Route path="/orders" element={<ProtectedRoute><Orders /></ProtectedRoute>} />
        <Route path="/orders/:id" element={<ProtectedRoute><OrderDetail /></ProtectedRoute>} />
        <Route path="/owner/restaurants" element={<ProtectedRoute><OwnerRestaurants /></ProtectedRoute>} />
        <Route path="/owner/restaurants/:id" element={<ProtectedRoute><OwnerRestaurantDetail /></ProtectedRoute>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;