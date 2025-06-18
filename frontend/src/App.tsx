import React, { useState } from 'react';
import './App.css';

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
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
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
        setUsername('');
        setPassword('');
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

  // Check for existing login on component mount
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

  // If user is logged in, show the main app
  if (user) {
    return (
      <div className="App">
        <header className="App-header">
          <div style={{ position: 'absolute', top: '20px', right: '20px' }}>
            <span style={{ marginRight: '15px', color: '#61dafb' }}>
              Welcome, {user.username}!
            </span>
            <button 
              onClick={handleLogout}
              style={{ 
                padding: '8px 16px', 
                fontSize: '14px',
                backgroundColor: '#ff6b6b',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer'
              }}
            >
              Logout
            </button>
          </div>
          <h1>Study Tool Dashboard</h1>
          <p>You are successfully logged in!</p>
          <p style={{ fontSize: '16px', color: '#61dafb' }}>
            This is where the main application content will go.
          </p>
        </header>
      </div>
    );
  }

  // Show login form
  return (
    <div className="App">
      <header className="App-header">
        <h1 style={{ marginBottom: '2rem' }}>Study Tool Login</h1>
        
        <form onSubmit={handleLogin} style={{ 
          display: 'flex', 
          flexDirection: 'column', 
          gap: '1rem',
          width: '300px',
          maxWidth: '90%'
        }}>
          <div>
            <input
              type="text"
              placeholder="Username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              style={{
                width: '100%',
                padding: '12px',
                fontSize: '16px',
                border: '1px solid #ccc',
                borderRadius: '4px',
                boxSizing: 'border-box'
              }}
            />
          </div>
          
          <div>
            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              style={{
                width: '100%',
                padding: '12px',
                fontSize: '16px',
                border: '1px solid #ccc',
                borderRadius: '4px',
                boxSizing: 'border-box'
              }}
            />
          </div>
          
          <button
            type="submit"
            disabled={loading}
            style={{
              padding: '12px',
              fontSize: '16px',
              backgroundColor: loading ? '#ccc' : '#61dafb',
              color: loading ? '#666' : '#282c34',
              border: 'none',
              borderRadius: '4px',
              cursor: loading ? 'not-allowed' : 'pointer',
              fontWeight: 'bold'
            }}
          >
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>
        
        {error && (
          <div style={{ 
            marginTop: '1rem', 
            padding: '10px', 
            backgroundColor: '#ff6b6b', 
            color: 'white', 
            borderRadius: '4px',
            maxWidth: '300px'
          }}>
            {error}
          </div>
        )}
        
        <div style={{ 
          marginTop: '2rem', 
          padding: '1rem', 
          backgroundColor: 'rgba(255,255,255,0.1)', 
          borderRadius: '4px',
          fontSize: '14px',
          maxWidth: '300px'
        }}>
          <p><strong>Test Credentials:</strong></p>
          <p>Username: testuser</p>
          <p>Password: password123</p>
          <p style={{ marginTop: '10px', fontSize: '12px', opacity: 0.8 }}>
            Or try: admin / admin123
          </p>
        </div>
      </header>
    </div>
  );
}

export default App;
