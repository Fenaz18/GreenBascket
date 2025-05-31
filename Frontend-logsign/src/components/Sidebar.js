// src/components/Sidebar.js
import React from 'react';
import { Link, useNavigate } from 'react-router-dom'; // Import useNavigate
import './Sidebar.css';

const Sidebar = ({ role }) => {
  const navigate = useNavigate(); // Initialize useNavigate

  const handleLogout = () => {
    localStorage.removeItem('jwtToken'); // Clear token
    navigate('/login'); // Redirect to login page
  };

  return (
    <div className="sidebar">
      <div className="sidebar-header"> {/* Add a header for the sidebar */}
        <h3>GreenBasket</h3>
      </div>
      <ul className="sidebar-menu"> {/* Use ul for list items */}
        <li><Link to="/dashboard">Dashboard</Link></li>
        <li><Link to="/profile">My Profile</Link></li> {/* Consistent "My Profile" */}

        {role === 'FARMER' && (
          <> {/* Use a fragment for multiple links */}
            <li><Link to="/add-product">Add Product</Link></li>
            <li><Link to="/my-products">My Products</Link></li>
          </>
        )}
        {/* Add CONSUMER specific links if any */}
        {role === 'CONSUMER' && (
          <li><Link to="/browse-products">Browse Products</Link></li>
        )}

        <li><button onClick={handleLogout} className="logout-button">Logout</button></li>
      </ul>
    </div>
  );
};

export default Sidebar;