// src/pages/MyProductsPage.js

import React, { useEffect, useState } from 'react';
import Sidebar from '../components/Sidebar';
import './MyProducts.css'; // Make sure this CSS file is present
import { useNavigate } from 'react-router-dom';

const MyProductsPage = () => {
  const navigate = useNavigate();

  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [userRole, setUserRole] = useState('FARMER'); // Still static for sidebar, adjust as per your auth context

  useEffect(() => {
    const fetchMyProducts = async () => {
      setLoading(true);
      setError(null);

      const token = localStorage.getItem('jwtToken');
      const farmerId = localStorage.getItem('userId'); // <--- ASSUMPTION: userId is stored as farmerId

      if (!token) {
        setError('Authentication token missing. Please log in.');
        navigate('/login');
        return;
      }
      if (!farmerId) {
        setError('Farmer ID not found. Please log in again.');
        navigate('/login');
        return;
      }

      try {
        // --- CRITICAL CHANGE: Include farmerId in the URL path ---
        const response = await fetch(`http://localhost:8080/api/products/farmer/${farmerId}`, {
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
              errorMessage = JSON.stringify(errorData);
            }
          } catch (jsonError) {
            const plainTextError = await response.text();
            errorMessage = `Server responded with status ${response.status}: ${plainTextError}`;
          }

          if (response.status === 401 || response.status === 403) {
            errorMessage = errorMessage.includes('expired') || errorMessage.includes('invalid')
                           ? 'Your session has expired or is invalid. Please log in again.'
                           : 'You are not authorized to view your products.';
            localStorage.removeItem('jwtToken');
            localStorage.removeItem('userId'); // Clear userId too
            setTimeout(() => navigate('/login'), 2000);
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

  // Optional: Function to handle product deletion (you'll need to create a delete endpoint on backend)
  const handleDeleteProduct = async (productId) => {
    const confirmDelete = window.confirm('Are you sure you want to delete this product?');
    if (!confirmDelete) return;

    setLoading(true);
    setError(null);

    const token = localStorage.getItem('jwtToken');
    const farmerId = localStorage.getItem('userId');

    if (!token || !farmerId) {
      setError('Authentication missing. Please log in.');
      navigate('/login');
      setLoading(false);
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/api/products/${farmerId}/${productId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        let errorMessage = `Failed to delete product ID: ${productId}.`;
        try {
          const errorData = await response.json();
          if (errorData && errorData.message) errorMessage = errorData.message;
        } catch (e) { /* ignore */ }
        throw new Error(errorMessage);
      }

      setProducts(products.filter(p => p.productId !== productId)); // Remove from state
      setLoading(false);
      alert('Product deleted successfully!');

    } catch (err) {
      console.error('Delete Product Error:', err);
      setError(err.message || 'An error occurred during deletion.');
      setLoading(false);
    }
  };


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
              // --- ADDED CONSOLE.LOG HERE ---
              // This will print the product ID and its imageUrl to your browser's console
              // for every product being mapped.
              console.log("Product ID:", p.productId, "Product Name:", p.name, "Image URL:", p.imageUrl),
              // --- END CONSOLE.LOG ---

              <div className="product-card" key={p.productId}>
                {p.imageUrl && ( // This condition means the <img> tag only renders if p.imageUrl is not null, undefined, or empty string
                  <img
                    src={`http://localhost:8080/api/products/images/${p.imageUrl}`} // Construct image URL
                    alt={p.name}
                    className="product-image"
                    onError={(e) => { e.target.onerror = null; e.target.src="https://via.placeholder.com/150?text=Image+Not+Found"; }} // Fallback for broken images
                  />
                )}
                <h4>{p.name}</h4>
                <p>{p.description}</p>
                <p>₹{p.price.toFixed(2)}</p> {/* Format price to 2 decimal places */}
                <p>Quantity: {p.quantity}</p>
                <p>Added: {new Date(p.createdAt).toLocaleDateString()}</p>

                <div className="product-actions">
                    {/* Add Edit Button/Link here (e.g., navigate to /edit-product/productId) */}
                    <button className="delete-button" onClick={() => handleDeleteProduct(p.productId)} disabled={loading}>
                        Delete
                    </button>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default MyProductsPage;