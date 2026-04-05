import { Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/auth/Login';
import Register from './pages/auth/Register';
import DashboardView from './pages/dashboard/DashboardView';
import RecordsList from './pages/records/RecordsList';
import UsersList from './pages/users/UsersList';
import MainLayout from './components/layout/MainLayout';
import ProtectedRoute from './components/ProtectedRoute';

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      {/* Protected Routes wrapped in Layout */}
      <Route path="/" element={<ProtectedRoute><MainLayout /></ProtectedRoute>}>
        <Route index element={<Navigate to="/dashboard" replace />} />
        
        {/* All Auth'd Users */}
        <Route path="dashboard" element={<DashboardView />} />
        <Route path="records" element={<RecordsList />} />
        
        {/* Admin Only */}
        <Route path="users" element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <UsersList />
          </ProtectedRoute>
        } />
      </Route>

      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}

export default App;
