import React, { useState } from 'react';
import Sidebar from '../components/Sidebar';
import './AddProduct.css';
import { useNavigate } from 'react-router-dom'; // Import useNavigate for redirection

const AddProductPage = () => {
  const navigate = useNavigate(); // Initialize useNavigate hook

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: '',
    quantity: '',
    imageUrl: ''
  });
  const [loading, setLoading] = useState(false); // New: Loading state
  const [error, setError] = useState(null);     // New: Error message state
  const [successMessage, setSuccessMessage] = useState(null); // New: Success message state

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true); // Start loading
    setError(null);   // Clear previous errors
    setSuccessMessage(null); // Clear previous success messages

    try {
      const token = localStorage.getItem('jwtToken');
      if (!token) {
        setError('Authentication token missing. Please log in.');
        // Redirect to login page if no token
        navigate('/login');
        return;
      }

      const response = await fetch('http://localhost:8080/api/farmer/product', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`, // Use backticks for template literal
        },
        body: JSON.stringify({
          ...formData,
          price: parseFloat(formData.price),
          quantity: parseInt(formData.quantity)
        }),
      });

      // *** Enhanced Error Handling ***
      if (!response.ok) {
        let errorMessage = 'Failed to add product.';
        // Attempt to parse JSON error message from backend
        try {
          const errorData = await response.json();
          if (errorData && errorData.message) {
            errorMessage = errorData.message;
          } else if (errorData && typeof errorData === 'object') {
            // Fallback for more complex error objects (e.g., validation errors)
            errorMessage = JSON.stringify(errorData);
          }
        } catch (jsonError) {
          // If response is not JSON, get it as text
          const plainTextError = await response.text();
          errorMessage = `Server responded with status ${response.status}: ${plainTextError}`;
        }

        // Specific handling for 401 Unauthorized or 403 Forbidden (common for expired/invalid tokens)
        if (response.status === 401 || response.status === 403) {
          errorMessage = errorMessage.includes('expired') || errorMessage.includes('invalid')
                         ? 'Your session has expired or is invalid. Please log in again.'
                         : 'You are not authorized to perform this action.';
          localStorage.removeItem('jwtToken'); // Clear expired token
          setTimeout(() => navigate('/login'), 2000); // Redirect after a short delay
        }

        throw new Error(errorMessage); // Throw error to be caught by the catch block
      }

      // If response is OK, parse it as JSON
      const successData = await response.json();
      console.log('Product added successfully:', successData);
      setSuccessMessage('Product added successfully!');

      // Clear form after successful submission
      setFormData({
        name: '',
        description: '',
        price: '',
        quantity: '',
        imageUrl: ''
      });

    } catch (error) {
      console.error('Add Product Error:', error.message);
      setError(error.message); // Set the error message for display
    } finally {
      setLoading(false); // End loading
    }
  };

  return (
    <div className="add-product-container">
      <Sidebar role="FARMER" /> {/* Sidebar role might also come from user context/state */}
      <div className="add-product-form-content"> {/* Added a wrapper div for form and messages */}
        <h2>Add New Product</h2>

        {loading && <p className="message loading">Adding product...</p>}
        {error && <p className="message error">{error}</p>}
        {successMessage && <p className="message success">{successMessage}</p>}

        <form onSubmit={handleSubmit}>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            placeholder="Product Name"
            required
          />
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            placeholder="Product Description"
            required
          />
          <input
            type="number"
            name="price"
            value={formData.price}
            onChange={handleChange}
            placeholder="Price"
            min="0"
            step="0.01"
            required
          />
          <input
            type="number"
            name="quantity"
            value={formData.quantity}
            onChange={handleChange}
            placeholder="Quantity"
            min="1"
            required
          />
          <input
            type="text"
            name="imageUrl"
            value={formData.imageUrl}
            onChange={handleChange}
            placeholder="Image URL"
            required
          />
          <button type="submit" disabled={loading}>
            {loading ? 'Adding...' : 'Add Product'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default AddProductPage;