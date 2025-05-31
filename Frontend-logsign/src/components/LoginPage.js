import React, { useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom'; // <-- Added for routing
import './Auth.css';

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post('http://localhost:8080/api/users/login', {
        email,
        password,
      });
      localStorage.setItem('jwtToken', res.data.token);
      alert('Login successful!');
    } catch (err) {
      alert('Login failed. Check credentials.');
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-box">
        <h2>Login</h2>
        <form onSubmit={handleLogin}>
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <button type="submit">Login</button>
        </form>

        {/* Link to Signup Page */}
        <p>
          Don&apos;t have an account?{' '}
          <Link to="/signup">Signup here</Link>
        </p>
      </div>
    </div>
  );
};

export default LoginPage;
