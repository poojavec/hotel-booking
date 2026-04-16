import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import ApiService from '../service/ApiService';
import { Hotel, LogOut, LogIn } from 'lucide-react';
import './Navbar.css';

/**
 * Navbar component - shown on every page of the app.
 * It shows different links based on whether the user is logged in or is an admin.
 */
function Navbar() {
    // Check login status and role by reading from localStorage
    const isAuthenticated = ApiService.isAuthenticated(); // Is user logged in?
    const isAdmin = ApiService.isAdmin();                 // Does user have admin role?

    const handleLogout = () => {
        // Ask the user to confirm before logging out
        const confirmed = window.confirm("Are you sure you want to logout?");
        if (confirmed) {
            ApiService.logout();              // Clears token/role/userId from localStorage
            window.location.href = '/home';   // Redirect to home (full page refresh to reset state)
        }
    };

    return (
        <nav className="glass">
            {/* Logo / Brand name */}
            <div className="nav-logo">
                <NavLink to="/home" className="gradient-text nav-logo-link">
                    <Hotel size={28} className="nav-logo-icon" />
                    ETHEREAL
                </NavLink>
            </div>

            {/* Navigation links */}
            <ul className="nav-links">

                {/* Always visible links */}
                <li><NavLink to="/home" className={({ isActive }) => isActive ? "active-link" : "nav-item"}>Home</NavLink></li>
                <li><NavLink to="/hotels" className={({ isActive }) => isActive ? "active-link" : "nav-item"}>Hotels</NavLink></li>

                {/* Show Profile link only if logged in */}
                {isAuthenticated && (
                    <li><NavLink to="/profile" className={({ isActive }) => isActive ? "active-link" : "nav-item"}>Profile</NavLink></li>
                )}

                {/* Show Admin link only if user is an admin */}
                {isAdmin && (
                    <li><NavLink to="/admin" className={({ isActive }) => isActive ? "active-link" : "nav-item"}>Admin</NavLink></li>
                )}

                {/* Show Login button if not logged in, Logout button if logged in */}
                {!isAuthenticated ? (
                    <li>
                        <NavLink to="/login" className="btn-primary nav-login-link">
                            <LogIn size={20} className="nav-icon" />
                            Login
                        </NavLink>
                    </li>
                ) : (
                    <li>
                        <button onClick={handleLogout} className="btn-secondary nav-logout-btn">
                            <LogOut size={20} className="nav-icon" />
                            Logout
                        </button>
                    </li>
                )}
            </ul>
        </nav>
    );
}

export default Navbar;
