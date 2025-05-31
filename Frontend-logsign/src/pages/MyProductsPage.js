import React, { useEffect, useState } from 'react';
import Sidebar from '../components/Sidebar';
import './MyProducts.css';
import { useNavigate } from 'react-router-dom'; // Added for potential redirection

const MyProductsPage = () => {
  const navigate = useNavigate(); // Initialize navigate hook

  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [userRole, setUserRole] = useState('FARMER'); // Assuming static role for now, but should come from context

  useEffect(() => {
    const fetchMyProducts = async () => {
      setLoading(true);
      setError(null);

      const token = localStorage.getItem('jwtToken');
      if (!token) {
        setError('Authentication token missing. Please log in.');
        navigate('/login'); // Redirect to login if no token
        return;
      }

      try {
        const response = await fetch('http://localhost:8080/api/farmer/products', { // <--- CORRECTED URL HERE
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          let errorMessage = 'Failed to fetch your products.';
          try {
            const errorData = await response.json();
            if (errorData && errorData.message) {
              errorMessage = errorData.message;
            } else if (errorData && typeof errorData === 'object') {
              errorMessage = JSON.stringify(errorData); // Fallback for complex errors
            }
          } catch (jsonError) {
            // If response is not JSON, get it as text
            const plainTextError = await response.text();
            errorMessage = `Server responded with status ${response.status}: ${plainTextError}`;
          }

          if (response.status === 401 || response.status === 403) {
            errorMessage = errorMessage.includes('expired') || errorMessage.includes('invalid')
                           ? 'Your session has expired or is invalid. Please log in again.'
                           : 'You are not authorized to view your products.';
            localStorage.removeItem('jwtToken'); // Clear expired token
            setTimeout(() => navigate('/login'), 2000); // Redirect after a short delay
          }

          throw new Error(errorMessage);
        }

        const data = await response.json();
        setProducts(data);

      } catch (err) {
        console.error('Error fetching my products:', err);
        setError(err.message || 'An unexpected error occurred while loading your products.');
      } finally {
        setLoading(false);
      }
    };

    fetchMyProducts();
  }, [navigate]); // Add navigate to dependency array for useEffect

  if (loading) {
    return (
      <div className="my-products-container">
        <Sidebar role={userRole} />
        <div className="my-products-content">
          <h2>My Products</h2>
          <p>Loading your products...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="my-products-container">
        <Sidebar role={userRole} />
        <div className="my-products-content">
          <h2>My Products</h2>
          <p className="error-message" style={{ color: 'red' }}>Error: {error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="my-products-container">
      <Sidebar role={userRole} />
      <div className="my-products-content">
        <h2>My Products</h2>
        <div className="product-grid">
          {products.length === 0 ? (
            <p>You haven't added any products yet.</p>
          ) : (
            products.map(p => (
              <div className="product-card" key={p.productId}> {/* Ensure you use productId as key */}
                <h4>{p.name}</h4>
                <p>{p.description}</p>
                <p>₹{p.price}</p>
                {/* Add more product details here if needed */}
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default MyProductsPage;