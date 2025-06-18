import React, { useState } from 'react';
import './App.css';
import LoginForm from './components/auth/LoginForm';
import Header from './components/layout/Header';
import Dashboard from './components/dashboard/Dashboard';

interface LoginResponse {
  success: boolean;
  token?: string;
  message: string;
  username?: string;
}

interface User {
  username: string;
  token: string;
}

function App() {
  const [user, setUser] = useState<User | null>(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async (username: string, password: string) => {
    setLoading(true);
    setError('');
    try {
      const response = await fetch('http://localhost:8080/api/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
      });
      const data: LoginResponse = await response.json();
      if (data.success && data.token && data.username) {
        const userData = { username: data.username, token: data.token };
        setUser(userData);
        localStorage.setItem('user', JSON.stringify(userData));
      } else {
        setError(data.message || 'Login failed');
      }
    } catch (error) {
      setError('Error connecting to server');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('user');
  };

  React.useEffect(() => {
    const savedUser = localStorage.getItem('user');
    if (savedUser) {
      try {
        const userData = JSON.parse(savedUser);
        setUser(userData);
      } catch (error) {
        localStorage.removeItem('user');
      }
    }
  }, []);

  return (
    <div className="dark min-h-screen bg-gray-950">
      {user ? (
        <>
          <Header username={user.username} onLogout={handleLogout} />
          <Dashboard user={user} />
        </>
      ) : (
        <LoginForm onLogin={handleLogin} loading={loading} error={error} />
      )}
    </div>
  );
}

export default App;
