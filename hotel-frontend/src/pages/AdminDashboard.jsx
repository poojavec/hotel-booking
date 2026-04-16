import React from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Building2, CalendarCheck, Shield } from 'lucide-react';
import './Admin.css';

/**
 * AdminDashboard - The central hub for administrators.
 * Shows quick navigation links to Hotel management and Booking management.
 */
function AdminDashboard() {
    const navigate = useNavigate(); // React Router hook to change pages

    return (
        <div className="admin-container">
            
            {/* DASHBOARD HEADER */}
            <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="glass admin-header">
                <Shield size={60} color="var(--accent-cyan)" style={{ marginBottom: '20px' }} />
                <h1 className="admin-title">Manager <span className="gradient-text">Dashboard</span></h1>
                <p className="admin-subtitle">Centralized oversight of the Ethereal Booking System properties and data.</p>
            </motion.div>

            {/* NAVIGATION CARDS */}
            <div className="nav-cards-grid">
                
                {/* 1. Manage Hotels/Rooms */}
                <motion.div 
                    initial={{ opacity: 0, x: -20 }} 
                    animate={{ opacity: 1, x: 0 }} 
                    className="glass glass-hover nav-card" 
                    onClick={() => navigate('/admin/hotels')} // Redirect to ManageHotels page
                >
                    <Building2 size={48} color="var(--accent-cyan)" style={{ marginBottom: '20px' }} />
                    <h2 className="nav-card-title">Manage Hotels</h2>
                    <p className="admin-subtitle">Register new luxury properties and update room inventories.</p>
                </motion.div>

                {/* 2. Manage All Bookings */}
                <motion.div 
                    initial={{ opacity: 0, x: 20 }} 
                    animate={{ opacity: 1, x: 0 }} 
                    className="glass glass-hover nav-card" 
                    onClick={() => navigate('/admin/bookings')} // Redirect to ManageBookings page
                >
                    <CalendarCheck size={48} color="var(--accent-violet)" style={{ marginBottom: '20px' }} />
                    <h2 className="nav-card-title">Manage Bookings</h2>
                    <p className="admin-subtitle">Review every reservation and handle global cancellations.</p>
                </motion.div>
                
            </div>
        </div>
    );
}

export default AdminDashboard;
