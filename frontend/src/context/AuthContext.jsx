import { createContext, useContext, useState, useEffect } from 'react';
import {jwtDecode} from 'jwt-decode'; // Fix import issues usually happening without brackets

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem('token') || null);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      try {
        const decoded = jwtDecode(token);
        // Spring Boot sets 'sub' (subject) to email conventionally, and we added 'role' to claims
        setUser({
          email: decoded.sub,
          role: decoded.role || 'VIEWER' // Default if parsing fails
        });
        localStorage.setItem('token', token);
      } catch (err) {
        console.error("Failed to decode token", err);
        logout();
      }
    } else {
      setUser(null);
      localStorage.removeItem('token');
    }
    setLoading(false);
  }, [token]);

  const login = (newToken) => {
    setToken(newToken);
  };

  const logout = () => {
    setToken(null);
  };

  return (
    <AuthContext.Provider value={{ token, user, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
