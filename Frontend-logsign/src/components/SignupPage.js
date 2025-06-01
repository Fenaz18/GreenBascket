import React, { useState } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import './Auth.css'; // Assuming your styling is in Auth.css

function SignupPage() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: '', // This will hold 'FARMER' or 'CONSUMER'
    phone: '',
    location: '',
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  const handleChange = (e) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSignup = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccess(null);

    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match!');
      setLoading(false);
      return;
    }

    if (!formData.role) {
      setError('Please select a role (Farmer or Consumer).');
      setLoading(false);
      return;
    }

    try {
      // Destructure to exclude confirmPassword before sending to backend
      const { confirmPassword, ...dataToSend } = formData;

      // Make the POST request to your Spring Boot backend
      const response = await axios.post('http://localhost:8080/api/users/register', dataToSend);

      // Backend should return 200 OK or 201 Created with UserResponseDTO in body
      if (response.status === 200 || response.status === 201) {
        const userData = response.data; // This will be the UserResponseDTO from backend

        setSuccess('Registration successful! Redirecting to dashboard...');

        // --- CRITICAL CHANGE: UNCOMMENT AND ENSURE YOUR BACKEND RETURNS 'token' ---
        localStorage.setItem('userRole', userData.role);
        localStorage.setItem('userId', userData.userId);
        localStorage.setItem('userName', userData.name);
        localStorage.setItem('jwtToken', userData.token); // <--- THIS LINE WAS COMMENTED OUT!

        // Redirect based on role after a short delay
        setTimeout(() => {
          if (userData.role === 'FARMER') {
            navigate('/farmer-dashboard');
          } else if (userData.role === 'CONSUMER') {
            navigate('/consumer-dashboard');
          } else {
            // Fallback if role is unexpected (shouldn't happen with enum)
            console.warn('Unknown role received:', userData.role);
            navigate('/login'); // Redirect to login as a safe fallback
          }
        }, 1500); // 1.5 second delay for user to see success message

      } else {
        setError(response.data.message || 'Registration failed with an unexpected response.');
      }
    } catch (err) {
      console.error('Registration error:', err);

      if (axios.isAxiosError(err) && err.response) {
        console.error('Backend Response Data:', err.response.data);
        console.error('Backend Response Status:', err.response.status);

        let backendErrorMessage = 'Registration failed. Please try again.';
        if (err.response.status === 409) {
          backendErrorMessage = 'Email already registered. Please use a different email or log in.';
        } else if (err.response.data) {
          if (err.response.data.message) {
            backendErrorMessage = err.response.data.message;
          } else if (err.response.data.errors && err.response.data.errors.length > 0) {
            backendErrorMessage = err.response.data.errors.map(e => e.defaultMessage || e.message).join('; ');
          } else if (typeof err.response.data === 'string') {
            backendErrorMessage = err.response.data;
          }
        }
        setError(backendErrorMessage);

      } else if (err.request) {
        setError('No response from server. Please check your network connection and try again.');
      } else {
        setError('An unexpected client-side error occurred during registration.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-box">
        <h2>Sign Up</h2>

        {/* Display messages */}
        {loading && <p className="message loading">Registering...</p>}
        {error && <p className="message error">{error}</p>}
        {success && <p className="message success">{success}</p>}

        <form onSubmit={handleSignup}>
          <input
            type="text"
            name="name"
            placeholder="Full Name"
            value={formData.name}
            onChange={handleChange}
            required
          />
          <input
            type="email"
            name="email"
            placeholder="Email"
            value={formData.email}
            onChange={handleChange}
            required
          />
          <input
            type="text"
            name="phone"
            placeholder="Phone Number (e.g., +91XXXXXXXXXX)"
            value={formData.phone}
            onChange={handleChange}
            required
            pattern="[0-9]{10,15}"
            title="Phone number must be 10 to 15 digits long"
          />
          <input
            type="text"
            name="location"
            placeholder="Location (e.g., City, State)"
            value={formData.location}
            onChange={handleChange}
          />
          <select name="role" value={formData.role} onChange={handleChange} required>
            <option value="">Select Role</option>
            <option value="FARMER">Farmer</option>
            <option value="CONSUMER">Consumer</option>
          </select>
          <input
            type="password"
            name="password"
            placeholder="Password"
            value={formData.password}
            onChange={handleChange}
            required
            minLength="6"
            title="Password must be at least 6 characters long"
          />
          <input
            type="password"
            name="confirmPassword"
            placeholder="Confirm Password"
            value={formData.confirmPassword}
            onChange={handleChange}
            required
          />
          <button type="submit" disabled={loading}>
            {loading ? 'Registering...' : 'Register'}
          </button>
        </form>

        <p className="auth-switch-text">
          Already have an account?{' '}
          <Link to="/login" className="auth-switch-link">Login here</Link>
        </p>
      </div>
    </div>
  );
}

export default SignupPage;