import React, { useState } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import './Auth.css';

const LoginPage = () => {
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState(''); // State for messages

  const handleLogin = async (e) => {
    e.preventDefault();
    setMessage(''); // Clear previous messages
    try {
      const res = await axios.post('http://localhost:8080/api/users/login', {
        email,
        password,
      });
      localStorage.setItem('jwtToken', res.data.token);
      setMessage('Login successful!');
      navigate('/dashboard');
    } catch (err) {
      // Axios error objects have a 'response' property if the server sent one
      if (err.response && err.response.data && err.response.data.message) {
        setMessage(err.response.data.message); // Display server's error message
      } else {
        setMessage('Login failed. Check credentials or try again later.'); // Generic message
        console.error('Login error:', err); // Log the full error for debugging
      }
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-box">
        <h2>Login</h2>
        {message && <p className={`message ${message.includes('successful') ? 'success' : 'error'}`}>{message}</p>} {/* Display message */}
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