import { useState, useEffect } from 'react';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { ArrowDownRight, ArrowUpRight, DollarSign } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';

export default function DashboardView() {
  const { user } = useAuth();
  const [summary, setSummary] = useState(null);
  const [trends, setTrends] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);

  // Colors for Recharts
  const COLORS = ['#10b981', '#3b82f6', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899'];

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const [sumRes, catRes] = await Promise.all([
          api.get('/dashboard/summary'),
          api.get('/dashboard/by-category')
        ]);
        
        setSummary(sumRes.data.data);
        
        // Transform category data for PieChart
        const catData = sumRes.data.data.totalExpense > 0 || sumRes.data.data.totalIncome > 0
          ? catRes.data.data.map(c => ({ name: `${c.category} (${c.type})`, value: c.total }))
          : [];
        setCategories(catData.sort((a,b) => b.value - a.value).slice(0, 10)); // Top 10

        // Only Analyst and Admin get Trends endpoint per RBAC rules
        if (user.role === 'ANALYST' || user.role === 'ADMIN') {
          const trendRes = await api.get('/dashboard/trends');
          setTrends(trendRes.data.data);
        }
      } catch (err) {
        console.error("Failed to fetch dashboard data", err);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, [user.role]);

  if (loading) return <div>Loading dashboard...</div>;

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-gray-900">Dashboard Overview</h2>
        <p className="mt-1 text-sm text-gray-500">
          {user.role === 'VIEWER' ? 'Your personal financial summary.' : 'System-wide financial aggregated summary.'}
        </p>
      </div>

      {/* Summary Stat Cards */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-3">
        <div className="bg-white overflow-hidden shadow rounded-lg px-4 py-5 sm:p-6 border-l-4 border-green-500">
          <dt className="truncate text-sm font-medium text-gray-500 flex items-center">
            <ArrowUpRight className="w-4 h-4 text-green-500 mr-2" />
            Total Income
          </dt>
          <dd className="mt-1 text-3xl font-semibold text-gray-900">₹{summary?.totalIncome?.toFixed(2) || '0.00'}</dd>
        </div>
        <div className="bg-white overflow-hidden shadow rounded-lg px-4 py-5 sm:p-6 border-l-4 border-red-500">
          <dt className="truncate text-sm font-medium text-gray-500 flex items-center">
            <ArrowDownRight className="w-4 h-4 text-red-500 mr-2" />
            Total Expense
          </dt>
          <dd className="mt-1 text-3xl font-semibold text-gray-900">₹{summary?.totalExpense?.toFixed(2) || '0.00'}</dd>
        </div>
        <div className="bg-white overflow-hidden shadow rounded-lg px-4 py-5 sm:p-6 border-l-4 border-blue-500">
          <dt className="truncate text-sm font-medium text-gray-500 flex items-center">
            <DollarSign className="w-4 h-4 text-blue-500 mr-2" />
            Net Balance
          </dt>
          <dd className="mt-1 text-3xl font-semibold text-gray-900">₹{summary?.netBalance?.toFixed(2) || '0.00'}</dd>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-5">
        {/* Category Breakdown (Everyone) */}
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Top Categories</h3>
          <div className="h-72">
            {categories.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={categories}
                    cx="50%"
                    cy="50%"
                    innerRadius={60}
                    outerRadius={90}
                    paddingAngle={5}
                    dataKey="value"
                  >
                    {categories.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip formatter={(value) => `₹${value.toFixed(2)}`} />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            ) : (
              <div className="h-full flex items-center justify-center text-gray-400">No data available</div>
            )}
          </div>
        </div>

        {/* Monthly Trends (ANALYST + ADMIN only) */}
        {(user.role === 'ANALYST' || user.role === 'ADMIN') && (
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-medium text-gray-900 mb-4">Monthly Trends (Overview)</h3>
            <div className="h-72">
              {trends.length > 0 ? (
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={trends} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} />
                    <XAxis dataKey="month" />
                    <YAxis />
                    <Tooltip formatter={(value) => `₹${value}`} />
                    <Legend />
                    <Bar dataKey="income" name="Income" fill="#10b981" radius={[4, 4, 0, 0]} />
                    <Bar dataKey="expense" name="Expense" fill="#f43f5e" radius={[4, 4, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              ) : (
                <div className="h-full flex items-center justify-center text-gray-400">No data available</div>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
