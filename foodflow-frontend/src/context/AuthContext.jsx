import { createContext, useContext, useState } from "react";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem("user");
    return savedUser ? JSON.parse(savedUser) : null;
  });

  const login = (authResponse) => {
    const userData = {
      token: authResponse.token,
      email: authResponse.email,
      role: authResponse.role,
      userId: authResponse.userId,
      name: authResponse.name || authResponse.email.split("@")[0],
    };
    localStorage.setItem("token", userData.token);
    localStorage.setItem("user", JSON.stringify(userData));
    setUser(userData);
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

// A shortcut so pages can just write: const { user, login, logout } = useAuth();
export function useAuth() {
  return useContext(AuthContext);
}