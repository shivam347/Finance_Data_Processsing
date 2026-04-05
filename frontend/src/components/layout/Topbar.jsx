import { LogOut, User } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export default function Topbar() {
  const { user, logout } = useAuth();

  return (
    <header className="h-16 bg-white border-b border-gray-200 flex items-center justify-between px-6">
      <div className="md:hidden">
        <h1 className="text-xl font-bold text-primary-600">Finance</h1>
      </div>
      <div className="hidden md:block"></div> {/* Spacer */}
      
      <div className="flex items-center space-x-4">
        <div className="flex items-center space-x-2 text-sm text-gray-600">
          <User className="w-4 h-4" />
          <span>{user?.email}</span>
        </div>
        <button
          onClick={logout}
          className="p-2 text-gray-500 hover:text-red-600 transition-colors rounded-lg hover:bg-gray-50"
          title="Logout"
        >
          <LogOut className="w-5 h-5" />
        </button>
      </div>
    </header>
  );
}
