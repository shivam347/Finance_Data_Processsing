import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute({ children, allowedRoles }) {
  const { token, user, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center">Loading...</div>;
  }

  if (!token) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // If specific roles are required and user doesn't have it, redirect
  if (allowedRoles && !allowedRoles.includes(user?.role)) {
    // Redirect to dashboard (they shouldn't be here)
    return <Navigate to="/dashboard" replace />;
  }

  return children;
}
