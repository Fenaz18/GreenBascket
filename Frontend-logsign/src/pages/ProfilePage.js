// src/pages/ProfilePage.js
import React, { useEffect, useState } from 'react';
import Sidebar from '../components/Sidebar';
import { useNavigate } from 'react-router-dom'; // For redirection
import axios from 'axios'; // Using axios for cleaner handling, but fetch is fine too
import './ProfilePage.css'; // Create this CSS file

const ProfilePage = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null); // Full user object from backend
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    location: '',
    // password and confirmPassword would be separate for security, not part of regular profile update
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);
  const [isEditing, setIsEditing] = useState(false); // To toggle between view and edit mode

  useEffect(() => {
    const fetchUserProfile = async () => {
      setLoading(true);
      setError(null);
      const token = localStorage.getItem('jwtToken');

      if (!token) {
        setError('Authentication token missing. Please log in.');
        navigate('/login');
        setLoading(false);
        return;
      }

      try {
        const response = await axios.get('http://localhost:8080/api/users/profile', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setUser(response.data); // Store the full user object
        setFormData({ // Initialize form data with fetched user data
          name: response.data.name || '',
          email: response.data.email || '',
          phone: response.data.phone || '',
          location: response.data.location || '',
        });
      } catch (err) {
        console.error('Error fetching user profile:', err);
        const errorMessage = err.response?.data?.message || err.message || 'Failed to fetch profile data.';

        if (err.response?.status === 401 || err.response?.status === 403) {
          setError('Your session has expired or is invalid. Please log in again.');
          localStorage.removeItem('jwtToken');
          setTimeout(() => navigate('/login'), 2000);
        } else {
          setError(errorMessage);
        }
      } finally {
        setLoading(false);
      }
    };

    fetchUserProfile();
  }, [navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccessMessage(null);
    const token = localStorage.getItem('jwtToken');

    if (!token) {
      setError('Authentication token missing. Please log in.');
      navigate('/login');
      setLoading(false);
      return;
    }

    try {
      // Send only updated fields to backend
      const updateData = {
        name: formData.name,
        // Email is usually not editable or requires special handling
        // email: formData.email,
        phone: formData.phone,
        location: formData.location,
      };

      const response = await axios.put('http://localhost:8080/api/users/profile', updateData, {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
      });

      setUser(response.data); // Update user state with new data
      setSuccessMessage('Profile updated successfully!');
      setIsEditing(false); // Exit editing mode
    } catch (err) {
      console.error('Error updating user profile:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Failed to update profile.';

      if (err.response?.status === 401 || err.response?.status === 403) {
        setError('Your session has expired or is invalid. Please log in again.');
        localStorage.removeItem('jwtToken');
        setTimeout(() => navigate('/login'), 2000);
      } else {
        setError(errorMessage);
      }
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="profile-container">
        <Sidebar role={user?.role || ''} />
        <div className="profile-content">
          <p className="message loading">Loading profile...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="profile-container">
        <Sidebar role={user?.role || ''} />
        <div className="profile-content">
          <p className="message error">{error}</p>
        </div>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="profile-container">
        <Sidebar role={user?.role || ''} />
        <div className="profile-content">
          <p className="message error">User profile not found.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="profile-container">
      <Sidebar role={user.role} />
      <div className="profile-content">
        <h2>My Profile</h2>

        {successMessage && <p className="message success">{successMessage}</p>}
        {error && <p className="message error">{error}</p>}

        {!isEditing ? (
          <div className="profile-details">
            <p><strong>Name:</strong> {user.name}</p>
            <p><strong>Email:</strong> {user.email}</p>
            <p><strong>Role:</strong> {user.role}</p>
            <p><strong>Phone:</strong> {user.phone || 'N/A'}</p>
            <p><strong>Location:</strong> {user.location || 'N/A'}</p>

            <button className="edit-button" onClick={() => setIsEditing(true)}>Edit Profile</button>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="profile-form">
            <label>
              Name:
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                required
              />
            </label>
            <label>
              Email:
              {/* Email is often not editable or requires re-authentication,
                  so display it as read-only or omit from editable fields */}
              <input
                type="email"
                name="email"
                value={formData.email}
                readOnly // Email usually not directly editable
                disabled // Disable it to make it clear it's read-only
                className="read-only-input"
              />
            </label>
            <label>
              Phone:
              <input
                type="text"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
              />
            </label>
            <label>
              Location:
              <input
                type="text"
                name="location"
                value={formData.location}
                onChange={handleChange}
              />
            </label>

            {/* Role-specific fields if any, example: */}
            {user.role === 'FARMER' && (
              <p className="role-specific-info">
                As a Farmer, you can manage your products.
              </p>
              // Add farmer-specific editable fields here if needed
            )}
            {user.role === 'CONSUMER' && (
              <p className="role-specific-info">
                As a Consumer, you can browse and purchase products.
              </p>
              // Add consumer-specific editable fields here if needed
            )}

            <div className="form-actions">
              <button type="submit" disabled={loading}>
                {loading ? 'Saving...' : 'Save Changes'}
              </button>
              <button type="button" className="cancel-button" onClick={() => setIsEditing(false)} disabled={loading}>
                Cancel
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};

export default ProfilePage;