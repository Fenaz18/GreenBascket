import React from 'react';
import { Link } from 'react-router-dom';
import './HomePage.css';

const HomePage = () => {
  return (
    <div className="home-container">
      {/* Hero Section */}
      <section className="hero-section">
        <h1>Welcome to GreenBasket</h1>
        <p>Connecting Farmers and Consumers for a Sustainable Future.</p>
        <div className="cta-buttons">
          <Link to="/login" className="home-btn login-btn">Login</Link>
          <Link to="/signup" className="home-btn signup-btn">Signup</Link>
        </div>
      </section>

      {/* Footer */}
      <footer className="home-footer">
        <div className="footer-content">
          <p>&copy; {new Date().getFullYear()} GreenBasket. All rights reserved.</p>
          <p>Built for sustainability and better living 🌿</p>
        </div>
      </footer>
    </div>
  );
};

export default HomePage;
