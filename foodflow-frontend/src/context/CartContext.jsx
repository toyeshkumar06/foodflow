import { createContext, useContext, useState, useCallback } from "react";
import axiosInstance from "../api/axiosInstance";
import { useAuth } from "./AuthContext";

const CartContext = createContext(null);

export function CartProvider({ children }) {
  const [cart, setCart] = useState({ items: [], itemsTotal: 0, restaurantId: null, restaurantName: null });
  const { user } = useAuth();

  const refreshCart = useCallback(async () => {
    if (!user) return;
    try {
      const response = await axiosInstance.get("/cart");
      setCart(response.data);
    } catch (err) {
      console.error("Failed to load cart", err);
    }
  }, [user]);

  const addToCart = async (foodItemId, quantity = 1) => {
    await axiosInstance.post("/cart/items", { foodItemId, quantity });
    await refreshCart();
  };

  const updateQuantity = async (cartItemId, quantity) => {
    await axiosInstance.patch(`/cart/items/${cartItemId}`, { quantity });
    await refreshCart();
  };

  const removeItem = async (cartItemId) => {
    await axiosInstance.delete(`/cart/items/${cartItemId}`);
    await refreshCart();
  };

  const clearCart = async () => {
    await axiosInstance.delete("/cart");
    setCart({ items: [], itemsTotal: 0, restaurantId: null, restaurantName: null });
  };

  const itemCount = cart.items?.reduce((sum, i) => sum + i.quantity, 0) || 0;

  return (
    <CartContext.Provider value={{ cart, itemCount, refreshCart, addToCart, updateQuantity, removeItem, clearCart }}>
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  return useContext(CartContext);
}