// src/pages/AddProductPage.js

import React, { useState } from 'react';
import Sidebar from '../components/Sidebar';
import './AddProduct.css';
import { useNavigate } from 'react-router-dom';

const AddProductPage = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: '',
    quantity: '',
  });
  const [selectedImageFile, setSelectedImageFile] = useState(null);
  const [imagePreviewUrl, setImagePreviewUrl] = useState(null); // For displaying image preview

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    setSelectedImageFile(file);

    if (file) {
      setImagePreviewUrl(URL.createObjectURL(file));
    } else {
      setImagePreviewUrl(null);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccessMessage(null);

    // Get farmerId from localStorage
    const farmerId = localStorage.getItem('userId'); // <--- ASSUMPTION: userId is stored as farmerId
    if (!farmerId) {
      setError('Farmer ID not found. Please log in again.');
      navigate('/login');
      setLoading(false);
      return;
    }

    if (!selectedImageFile) {
      setError('Please select an image to upload.');
      setLoading(false);
      return;
    }

    try {
      const token = localStorage.getItem('jwtToken');
      if (!token) {
        setError('Authentication token missing. Please log in.');
        navigate('/login');
        return;
      }

      const data = new FormData();
      data.append('name', formData.name);
      data.append('description', formData.description);
      data.append('price', parseFloat(formData.price));
      data.append('quantity', parseInt(formData.quantity));
      data.append('imageFile', selectedImageFile);

      // --- CRITICAL CHANGE: Include farmerId in the URL path ---
      const response = await fetch(`http://localhost:8080/api/products/${farmerId}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
        body: data,
      });

      if (!response.ok) {
        let errorMessage = 'Failed to add product.';
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
                         : 'You are not authorized to perform this action.';
          localStorage.removeItem('jwtToken');
          localStorage.removeItem('userId'); // Clear userId too
          setTimeout(() => navigate('/login'), 2000);
        }

        throw new Error(errorMessage);
      }

      const successData = await response.json();
      console.log('Product added successfully:', successData);
      setSuccessMessage('Product added successfully!');

      // Clear form and image states after successful submission
      setFormData({
        name: '',
        description: '',
        price: '',
        quantity: '',
      });
      setSelectedImageFile(null);
      setImagePreviewUrl(null);

      // Optionally, navigate to My Products page or refresh current page
      // navigate('/my-products');

    } catch (error) {
      console.error('Add Product Error:', error.message);
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="add-product-container">
      <Sidebar role="FARMER" /> {/* Role might also come from localStorage/context */}
      <div className="add-product-form-content">
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
            type="file"
            name="imageFile"
            accept="image/*"
            onChange={handleFileChange}
            required
          />
          {imagePreviewUrl && (
            <div className="image-preview">
              <img src={imagePreviewUrl} alt="Image Preview" style={{ maxWidth: '200px', maxHeight: '200px', marginTop: '10px' }} />
            </div>
          )}

          <button type="submit" disabled={loading}>
            {loading ? 'Adding...' : 'Add Product'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default AddProductPage;