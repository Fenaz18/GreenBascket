import React, { useState } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import './Auth.css'; // Assuming your styling is in Auth.css

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

      // Assuming your backend's LoginResponseDTO includes:
      // token, userId, name, and role
      const { token, userId, name, role } = res.data;

      // Store all necessary user data in localStorage
      localStorage.setItem('jwtToken', token);
      localStorage.setItem('userId', userId);
      localStorage.setItem('userName', name);
      localStorage.setItem('userRole', role); // <--- Store the user's role here!

      setMessage('Login successful! Redirecting...');

      // Redirect based on the user's role
      setTimeout(() => { // Optional: Add a small delay for the user to see the message
        if (role === 'FARMER') {
          navigate('/farmer-dashboard');
        } else if (role === 'CONSUMER') {
          navigate('/consumer-dashboard');
        } else {
          // Fallback for unexpected roles or if role is missing
          console.warn('Unknown role received during login:', role);
          navigate('/'); // Redirect to home or a default page
        }
      }, 1000); // 1-second delay

    } catch (err) {
      // Axios error objects have a 'response' property if the server sent one
      if (axios.isAxiosError(err) && err.response) {
        // Log backend response details for debugging
        console.error('Backend Response Data:', err.response.data);
        console.error('Backend Response Status:', err.response.status);

        let backendErrorMessage = 'Login failed. Please try again.';
        if (err.response.data && err.response.data.message) {
          backendErrorMessage = err.response.data.message;
        } else if (typeof err.response.data === 'string') {
          backendErrorMessage = err.response.data;
        }
        setMessage(backendErrorMessage);
      } else if (err.request) {
        // The request was made but no response was received (e.g., network error, backend not running)
        setMessage('No response from server. Please check your network connection and try again.');
      } else {
        // Something else happened in setting up the request that triggered an error
        setMessage('An unexpected client-side error occurred during login.');
        console.error('Login error:', err); // Log the full error for debugging
      }
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-box">
        <h2>Login</h2>
        {message && <p className={`message ${message.includes('successful') ? 'success' : 'error'}`}>{message}</p>}
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

        <p className="auth-switch-text">
          Don&apos;t have an account?{' '}
          <Link to="/signup" className="auth-switch-link">Signup here</Link>
        </p>
      </div>
    </div>
  );
};

export default LoginPage;
