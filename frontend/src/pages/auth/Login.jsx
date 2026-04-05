import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import { LogIn } from 'lucide-react';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { cn } from '../../utils/cn';

export default function Login() {
  const { register, handleSubmit, formState: { errors } } = useForm();
  const [errorMsg, setErrorMsg] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { login } = useAuth();

  const onSubmit = async (data) => {
    setLoading(true);
    setErrorMsg('');
    try {
      const response = await api.post('/auth/login', {
        email: data.email,
        password: data.password
      });
      if (response.data.success) {
        login(response.data.data.token);
        navigate('/dashboard');
      }
    } catch (err) {
      setErrorMsg(err.response?.data?.error || 'Failed to login. Please check credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <div className="flex justify-center text-primary-600">
          <LogIn className="w-12 h-12" />
        </div>
        <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
          Sign in to your account
        </h2>
        <p className="mt-2 text-center text-sm text-gray-600">
          Or{' '}
          <Link to="/register" className="font-medium text-primary-600 hover:text-primary-500">
            register to become a viewer
          </Link>
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
          <form className="space-y-6" onSubmit={handleSubmit(onSubmit)}>
            {errorMsg && (
              <div className="bg-red-50 text-red-500 p-3 rounded text-sm text-center">
                {errorMsg}
              </div>
            )}
            <div>
              <label className="block text-sm font-medium text-gray-700">Email address</label>
              <div className="mt-1">
                <input
                  type="email"
                  {...register('email', { required: 'Email is required' })}
                  className={cn(
                    "appearance-none block w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm",
                    errors.email ? "border-red-300" : "border-gray-300"
                  )}
                />
                {errors.email && <span className="text-red-500 text-xs mt-1 block">{errors.email.message}</span>}
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">Password</label>
              <div className="mt-1">
                <input
                  type="password"
                  {...register('password', { required: 'Password is required' })}
                  className={cn(
                    "appearance-none block w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm",
                    errors.password ? "border-red-300" : "border-gray-300"
                  )}
                />
                {errors.password && <span className="text-red-500 text-xs mt-1 block">{errors.password.message}</span>}
              </div>
            </div>

            <div>
              <button
                type="submit"
                disabled={loading}
                className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50"
              >
                {loading ? 'Signing in...' : 'Sign in'}
              </button>
            </div>
            
            <div className="mt-4 text-xs text-center text-gray-400">
              Admin? use <b>admin@dashboard.com</b> / <b>admin123</b>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
