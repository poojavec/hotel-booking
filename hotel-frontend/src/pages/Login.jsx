import React, { useState } from 'react';
import { useNavigate, NavLink } from 'react-router-dom';
import ApiService from '../service/ApiService';
import { motion } from 'framer-motion';
import { LogIn, Mail, Lock, AlertCircle } from 'lucide-react';
import './Auth.css';

/**
 * Login Page - lets users enter their email and password to log in.
 *
 * useState = React hook to store simple values that change (like form inputs).
 * WHY useState? When the value changes, React re-renders the component automatically.
 */
function Login() {
    // State variables for the form fields and error message
    const [email, setEmail] = useState('');     // Stores what the user types in the email field
    const [password, setPassword] = useState(''); // Stores what the user types in the password field
    const [error, setError] = useState('');     // Stores error message to show if login fails

    const navigate = useNavigate(); // Used to redirect to another page after login

    /**
     * Called when the login form is submitted.
     * e.preventDefault() stops the browser from refreshing the page (default form behavior).
     */
    const handleSubmit = async (e) => {
        e.preventDefault(); // Prevent default page refresh on form submit

        // Basic check: don't submit if fields are empty
        if (!email || !password) {
            setError("Please fill in all fields");
            return;
        }

        try {
            // Call the backend login API with email and password
            const response = await ApiService.loginUser({ email, password });

            if (response.statusCode === 200) {
                // Login was successful — save the token, role, and userId to localStorage
                // WHY localStorage? So the data persists even if the page refreshes.
                localStorage.setItem('token', response.token);    // JWT token for future API calls
                localStorage.setItem('role', response.role);      // "ROLE_USER" or "ROLE_ADMIN"
                localStorage.setItem('userId', response.user.id); // Used to fetch bookings later

                window.location.href = '/home'; // Redirect to home (full reload to update Navbar)
            }
        } catch (error) {
            // If login fails, show the error message from the backend (or a generic one)
            setError(error.response?.data?.message || error.message);
            // Clear the error after 5 seconds
            setTimeout(() => setError(''), 5000);
        }
    };

    return (
        <div className="auth-container">

            {/* motion.div = animated div from framer-motion library — fades in on page load */}
            <motion.div
                initial={{ opacity: 0, scale: 0.9 }} // Start invisible and slightly smaller
                animate={{ opacity: 1, scale: 1 }}   // Animate to visible and full size
                className="glass auth-card"
            >
                {/* Page header */}
                <div className="auth-header">
                    <LogIn size={48} className="auth-icon" />
                    <h2 className="auth-title">Welcome <span className="gradient-text">Back</span></h2>
                    <p className="auth-subtitle">Access your Ethereal journey</p>
                </div>

                {/* Show error message only if there is one */}
                {error && (
                    <div className="auth-error">
                        <AlertCircle size={20} /> {error}
                    </div>
                )}

                {/* Login form */}
                <form onSubmit={handleSubmit}>

                    {/* Email input field */}
                    <div className="form-group">
                        <label className="form-label">Email Address</label>
                        <div className="form-input-container">
                            <Mail size={18} className="input-icon" />
                            <input
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)} // Update state on every keystroke
                                className="form-input"
                                placeholder="name@example.com"
                            />
                        </div>
                    </div>

                    {/* Password input field */}
                    <div className="form-group">
                        <label className="form-label">Password</label>
                        <div className="form-input-container">
                            <Lock size={18} className="input-icon" />
                            <input
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                className="form-input"
                                placeholder="••••••••"
                            />
                        </div>
                    </div>

                    {/* Submit button */}
                    <button type="submit" className="btn-primary auth-btn">
                        Login to Account
                    </button>
                </form>

                {/* Link to the register page */}
                <p className="auth-footer">
                    Don't have an account? <NavLink to="/register" className="auth-link">Register here</NavLink>
                </p>
            </motion.div>
        </div>
    );
}

export default Login;
