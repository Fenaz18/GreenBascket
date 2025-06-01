import React, { useEffect, useState } from 'react';
import { NavLink, useNavigate, useLocation } from 'react-router-dom';
import './Sidebar.css';

const Sidebar = ({ role }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const [currentRole, setCurrentRole] = useState(role || localStorage.getItem('userRole'));

  useEffect(() => {
    if (role) {
      setCurrentRole(role);
    }
  }, [role]);

  const handleLogout = () => {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userId');
    localStorage.removeItem('userName');
    localStorage.removeItem('userRole');
    navigate('/login');
  };

  const getLinkClass = ({ isActive }) => (isActive ? 'active' : undefined);

  return (
    <div className="sidebar-container">
      <div className="sidebar-header">
        <h3>GreenBasket</h3>
      </div>
      <nav className="sidebar-nav">
        <ul>
          {currentRole === 'FARMER' && (
            <>
              <li className="sidebar-section-title">Farmer Tools</li>
              <li><NavLink to="/farmer-dashboard" className={getLinkClass}>Dashboard</NavLink></li>
              <li><NavLink to="/add-product" className={getLinkClass}>Add Product</NavLink></li>
              <li><NavLink to="/my-products" className={getLinkClass}>My Products</NavLink></li>
              <li><NavLink to="/farmer-orders" className={getLinkClass}>View Orders</NavLink></li>
              <li><NavLink to="/farmer-messages" className={getLinkClass}>Messages</NavLink></li>
            </>
          )}

          {currentRole === 'CONSUMER' && (
            <>
              <li className="sidebar-section-title">Consumer Hub</li>
              <li><NavLink to="/consumer-dashboard" className={getLinkClass}>Dashboard</NavLink></li>
              <li><NavLink to="/browse-products" className={getLinkClass}>Browse Products</NavLink></li>
              <li><NavLink to="/my-orders" className={getLinkClass}>My Orders</NavLink></li>
              <li><NavLink to="/my-cart" className={getLinkClass}>My Cart</NavLink></li>
              <li><NavLink to="/consumer-messages" className={getLinkClass}>Messages</NavLink></li>
            </>
          )}

          {currentRole && (
            <>
              <li className="sidebar-section-title">Account</li>
              <li><NavLink to="/profile-settings" className={getLinkClass}>Profile Settings</NavLink></li>
              <li>
                <button onClick={handleLogout} className="sidebar-logout-button">Logout</button>
              </li>
            </>
          )}

          {!currentRole && (
            <>
              <li><NavLink to="/login" className={getLinkClass}>Login</NavLink></li>
              <li><NavLink to="/signup" className={getLinkClass}>Signup</NavLink></li>
            </>
          )}
        </ul>
      </nav>
    </div>
  );
};

export default Sidebar;
