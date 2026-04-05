import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import api from '../../services/api';
import { Plus, Trash, Edit } from 'lucide-react';
import { useForm } from 'react-hook-form';
import { cn } from '../../utils/cn';

export default function RecordsList() {
  const { user } = useAuth();
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Modal state
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);
  
  const { register, handleSubmit, reset } = useForm();

  const fetchRecords = async () => {
    setLoading(true);
    try {
      const res = await api.get('/records');
      setRecords(res.data.data.content || []); // Pagination content array
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRecords();
  }, []);

  const openAddModal = () => {
    reset({ amount: '', type: 'INCOME', category: '', recordDate: new Date().toISOString().split('T')[0], notes: '' });
    setEditingId(null);
    setIsModalOpen(true);
  };

  const openEditModal = (record) => {
    reset({
      amount: record.amount,
      type: record.type,
      category: record.category,
      recordDate: record.recordDate,
      notes: record.notes || ''
    });
    setEditingId(record.id);
    setIsModalOpen(true);
  };

  const deleteRecord = async (id) => {
    if (window.confirm('Delete this record?')) {
      try {
        await api.delete(`/records/${id}`);
        fetchRecords();
      } catch (err) {
        alert('Failed to delete');
      }
    }
  };

  const onSubmit = async (data) => {
    try {
      if (editingId) {
        await api.put(`/records/${editingId}`, data);
      } else {
        await api.post('/records', data);
      }
      setIsModalOpen(false);
      fetchRecords();
    } catch (err) {
      alert('Operation failed');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center pb-4 border-b border-gray-200">
        <h2 className="text-2xl font-bold text-gray-900">Financial Records</h2>
        {/* Only Admin can add records */}
        {user.role === 'ADMIN' && (
          <button
            onClick={openAddModal}
            className="flex items-center space-x-2 bg-primary-600 text-white px-4 py-2 rounded shadow hover:bg-primary-700"
          >
            <Plus className="w-4 h-4" />
            <span>Add Record</span>
          </button>
        )}
      </div>

      <div className="bg-white shadow overflow-hidden sm:rounded-md">
        {loading ? (
          <div className="p-4 text-center text-gray-500">Loading records...</div>
        ) : records.length === 0 ? (
          <div className="p-4 text-center text-gray-500">No records found.</div>
        ) : (
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type / Category</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Amount</th>
                {user.role === 'ADMIN' && <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>}
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {records.map((r) => (
                <tr key={r.id}>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{r.recordDate}</td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={cn(
                      "px-2 inline-flex text-xs leading-5 font-semibold rounded-full",
                      r.type === 'INCOME' ? "bg-green-100 text-green-800" : "bg-red-100 text-red-800"
                    )}>
                      {r.type}
                    </span>
                    <div className="text-sm text-gray-900 mt-1">{r.category}</div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    ₹{r.amount.toFixed(2)}
                  </td>
                  {user.role === 'ADMIN' && (
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                      <button onClick={() => openEditModal(r)} className="text-indigo-600 hover:text-indigo-900 mr-4">
                        <Edit className="w-4 h-4 inline" />
                      </button>
                      <button onClick={() => deleteRecord(r.id)} className="text-red-600 hover:text-red-900">
                        <Trash className="w-4 h-4 inline" />
                      </button>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Add / Edit Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-75 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 className="text-lg font-bold mb-4">{editingId ? 'Edit Record' : 'Add Record'}</h3>
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
              <div>
                 <label className="block text-sm font-medium">Type</label>
                 <select {...register('type')} className="mt-1 block w-full border rounded-md p-2">
                   <option value="INCOME">Income</option>
                   <option value="EXPENSE">Expense</option>
                 </select>
              </div>
              <div>
                 <label className="block text-sm font-medium">Amount (₹)</label>
                 <input type="number" step="0.01" {...register('amount', { valueAsNumber: true, required: true })} className="mt-1 block w-full border rounded-md p-2" />
              </div>
              <div>
                 <label className="block text-sm font-medium">Category</label>
                 <input type="text" {...register('category', { required: true })} className="mt-1 block w-full border rounded-md p-2" />
              </div>
              <div>
                 <label className="block text-sm font-medium">Date</label>
                 <input type="date" {...register('recordDate', { required: true })} className="mt-1 block w-full border rounded-md p-2" />
              </div>
              <div>
                 <label className="block text-sm font-medium">Notes</label>
                 <input type="text" {...register('notes')} className="mt-1 block w-full border rounded-md p-2" />
              </div>
              <div className="flex justify-end space-x-3 mt-6">
                <button type="button" onClick={() => setIsModalOpen(false)} className="px-4 py-2 border rounded text-gray-600">Cancel</button>
                <button type="submit" className="px-4 py-2 bg-primary-600 text-white rounded">Save</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
