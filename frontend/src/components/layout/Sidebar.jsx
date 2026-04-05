import { NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { LayoutDashboard, ReceiptText, Users } from 'lucide-react';

export default function Sidebar() {
  const { user } = useAuth();

  const links = [
    { to: '/dashboard', label: 'Dashboard', icon: <LayoutDashboard className="w-5 h-5" />, roles: ['VIEWER', 'ANALYST', 'ADMIN'] },
    { to: '/records', label: 'Financial Records', icon: <ReceiptText className="w-5 h-5" />, roles: ['VIEWER', 'ANALYST', 'ADMIN'] },
    { to: '/users', label: 'Manage Users', icon: <Users className="w-5 h-5" />, roles: ['ADMIN'] },
  ];

  const visibleLinks = links.filter(link => link.roles.includes(user?.role));

  return (
    <aside className="w-64 bg-white border-r border-gray-200 hidden md:flex flex-col">
      <div className="h-16 flex items-center px-6 border-b border-gray-200">
        <h1 className="text-xl font-bold text-primary-600">Finance Dashboard</h1>
      </div>
      <nav className="flex-1 px-4 py-6 space-y-1">
        {visibleLinks.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) =>
              `flex items-center px-4 py-3 rounded-lg text-sm font-medium transition-colors ${
                isActive
                  ? 'bg-primary-50 text-primary-600'
                  : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
              }`
            }
          >
            {link.icon}
            <span className="ml-3">{link.label}</span>
          </NavLink>
        ))}
      </nav>
      <div className="p-4 border-t border-gray-200 text-xs text-gray-500">
        Role: <span className="font-semibold text-gray-700">{user?.role}</span>
      </div>
    </aside>
  );
}
