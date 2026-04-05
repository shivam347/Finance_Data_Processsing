import { useState, useEffect } from 'react';
import api from '../../services/api';

export default function UsersList() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const res = await api.get('/users');
      setUsers(res.data.data.content);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleRoleChange = async (id, newRole) => {
    try {
      await api.put(`/users/${id}`, { role: newRole });
      fetchUsers();
    } catch (err) {
      alert('Failed to update role');
    }
  };

  const handleStatusChange = async (id, newStatus) => {
    try {
      await api.put(`/users/${id}`, { status: newStatus });
      fetchUsers();
    } catch (err) {
      alert('Failed to update status');
    }
  };

  return (
    <div className="space-y-6">
      <div className="pb-4 border-b border-gray-200">
        <h2 className="text-2xl font-bold text-gray-900">User Management</h2>
        <p className="mt-1 text-sm text-gray-500">Admin only zone: Manage access levels system-wide.</p>
      </div>

      <div className="bg-white shadow overflow-hidden sm:rounded-md">
        {loading ? (
          <div className="p-4 text-center">Loading users...</div>
        ) : (
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">User</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Role</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {users.map((u) => (
                <tr key={u.id}>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm font-medium text-gray-900">{u.name}</div>
                    <div className="text-sm text-gray-500">{u.email}</div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <select 
                      value={u.role}
                      onChange={(e) => handleRoleChange(u.id, e.target.value)}
                      className="border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 sm:text-sm p-1 disabled:opacity-50 disabled:bg-gray-100"
                      disabled={u.role === 'ADMIN'}
                    >
                      <option value="VIEWER">Viewer</option>
                      <option value="ANALYST">Analyst</option>
                      {u.role === 'ADMIN' && <option value="ADMIN">Admin</option>}
                    </select>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                     <select 
                      value={u.status}
                      onChange={(e) => handleStatusChange(u.id, e.target.value)}
                      className="border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 sm:text-sm p-1 disabled:opacity-50 disabled:bg-gray-100"
                      disabled={u.role === 'ADMIN'}
                    >
                      <option value="ACTIVE">Active</option>
                      <option value="INACTIVE">Inactive</option>
                    </select>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
