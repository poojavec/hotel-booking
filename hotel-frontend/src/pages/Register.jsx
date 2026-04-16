import React, { useState } from 'react';
import { useNavigate, NavLink } from 'react-router-dom';
import ApiService from '../service/ApiService';
import { motion } from 'framer-motion';
import { UserPlus, Mail, Lock, User, AlertCircle } from 'lucide-react';
import toast from 'react-hot-toast';
import './Auth.css';

/**
 * Register Page - allows new users to create an account.
 */
function Register() {
    // State to hold form input values
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: ''
    });
    const [error, setError] = useState(''); // Holds error message for display
    const navigate = useNavigate();

    /**
     * Updates the formData state when any input changes.
     * Use a single function for all inputs to reduce code.
     */
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    /**
     * Handles form submission to the backend.
     */
    const handleSubmit = async (e) => {
        e.preventDefault();
        
        // Basic validation before calling API
        if (!formData.firstName || !formData.lastName || !formData.email || !formData.password) {
            setError("Please fill in all fields");
            return;
        }

        try {
            // Call the registerUser API method
            const response = await ApiService.registerUser(formData);
            
            if (response.statusCode === 200) {
                toast.success("Registration successful! Please login.");
                navigate('/login'); // Go to login page after success
            }
        } catch (error) {
            // Show error message from backend or generic error
            setError(error.response?.data?.message || error.message);
            setTimeout(() => setError(''), 5000); // Hide error after 5 seconds
        }
    };



    return (
        <div className="auth-container">
            <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="glass auth-card-wide">
                
                <div className="auth-header">
                    <UserPlus size={48} className="auth-icon" />
                    <h2 className="auth-title">Create <span className="gradient-text">Account</span></h2>
                    <p className="auth-subtitle">Join the Ethereal luxury community</p>
                </div>

                {error && (
                    <div className="auth-error">
                        <AlertCircle size={20} /> {error}
                    </div>
                )}

                <form onSubmit={handleSubmit}>
                    <div className="form-grid">
                        <div>
                            <label className="form-label">First Name</label>
                            <div className="form-input-container">
                                <User size={18} className="input-icon" />
                                <input type="text" name="firstName" value={formData.firstName} onChange={handleInputChange} className="form-input" placeholder="John" />
                            </div>
                        </div>
                        <div>
                            <label className="form-label">Last Name</label>
                            <div className="form-input-container">
                                <User size={18} className="input-icon" />
                                <input type="text" name="lastName" value={formData.lastName} onChange={handleInputChange} className="form-input" placeholder="Doe" />
                            </div>
                        </div>
                    </div>

                    <label className="form-label">Email Address</label>
                    <div className="form-input-container">
                        <Mail size={18} className="input-icon" />
                        <input type="email" name="email" value={formData.email} onChange={handleInputChange} className="form-input" placeholder="john@example.com" />
                    </div>

                    <label className="form-label">Password</label>
                    <div className="form-input-container">
                        <Lock size={18} className="input-icon" />
                        <input type="password" name="password" value={formData.password} onChange={handleInputChange} className="form-input" placeholder="••••••••" />
                    </div>

                    <button type="submit" className="btn-primary auth-btn">
                        Register Now
                    </button>
                </form>

                <p className="auth-footer">
                    Already have an account? <NavLink to="/login" className="auth-link">Login instead</NavLink>
                </p>
            </motion.div>
        </div>
    );
}

export default Register;
