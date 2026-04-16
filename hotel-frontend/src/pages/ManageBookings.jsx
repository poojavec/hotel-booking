import React, { useState, useEffect } from 'react';
import ApiService from '../service/ApiService';
import toast from 'react-hot-toast';
import { motion } from 'framer-motion';
import { Search, XCircle, FileText } from 'lucide-react';
import './Admin.css';

/**
 * ManageBookings - Admin page showing every single booking in the entire database.
 */
function ManageBookings() {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState(''); // Local search by confirmation code

    // Fetch all bookings globally when page loads
    useEffect(() => {
        fetchAllBookings();
    }, []);

    const fetchAllBookings = async () => {
        try {
            const response = await ApiService.getAllBookings();
            setBookings(response.bookingList || []);
        } catch (error) {
            toast.error("Failed to fetch the booking registry");
        } finally {
            setLoading(false);
        }
    };

    /**
     * Admin cancellation - permanently stops a booking but keeps it in history.
     */
    const handleGlobalCancel = async (bookingId) => {
        if (!window.confirm("As an administrator, are you sure you want to cancel this booking?")) return;
        try {
            await ApiService.cancelBooking(bookingId);
            toast.success("Global cancellation successful");
            fetchAllBookings(); // Refresh the registry
        } catch (error) {
            toast.error("Failed to cancel booking via registry");
        }
    };

    /**
     * Filter the registry based on what admin types (Confirmation code or member email)
     */
    const filteredRegistry = bookings.filter(b => 
        b.bookingConfirmationCode.toLowerCase().includes(searchTerm.toLowerCase()) ||
        b.user?.email.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (loading) return <div className="loading-overlay">Accessing global registry records...</div>;

    return (
        <div className="admin-container">
            
            {/* REGISTRY HEADER */}
            <div className="admin-top-bar">
                <div>
                    <h1 className="admin-title" style={{marginBottom: 0}}>Global <span className="gradient-text">Registry</span></h1>
                    <p className="admin-subtitle">Central database of all luxury stay reservations.</p>
                </div>

                <div className="glass search-bar-admin">
                    <Search size={22} color="var(--accent-cyan)" style={{ marginRight: '10px' }} />
                    <input 
                        type="text" 
                        placeholder="Search by Code or Member email..." 
                        value={searchTerm} 
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="search-input-admin" 
                    />
                </div>
            </div>
            
            {/* REGISTRY TABLE/LIST */}
            <div className="registry-list">
                {filteredRegistry.map((booking, index) => (
                    <motion.div 
                        key={booking.id} 
                        initial={{ opacity: 0, scale: 0.98 }} 
                        animate={{ opacity: 1, scale: 1 }} 
                        transition={{ delay: index * 0.05 }}
                        className={`glass registry-item ${booking.bookingStatus === 'CONFIRMED' ? 'item-confirmed' : 'item-cancelled'}`} 
                    >
                        <div className="registry-item-content">
                            <div className="registry-icon-box">
                                <FileText size={32} color="var(--text-dim)" />
                            </div>
                            <div>
                                <h3 className="registry-code">Confirmation: {booking.bookingConfirmationCode}</h3>
                                <div className="registry-details-grid">
                                    <span><strong>Member:</strong> {booking.user?.email}</span>
                                    <span><strong>Dates:</strong> {booking.checkInDate} to {booking.checkOutDate}</span>
                                    <span><strong>Hotel:</strong> {booking.room?.hotel?.name}</span>
                                    <span><strong>Suite:</strong> {booking.room?.roomType}</span>
                                </div>
                                <div style={{ marginTop: '10px' }}>
                                    <span className={`status-badge-admin ${booking.bookingStatus === 'CONFIRMED' ? 'status-confirmed' : 'status-cancelled'}`}>
                                        {booking.bookingStatus}
                                    </span>
                                </div>
                            </div>
                        </div>

                        {booking.bookingStatus !== 'CANCELLED' && (
                            <button onClick={() => handleGlobalCancel(booking.id)} className="btn-primary cancel-booking-btn">
                                <XCircle size={18} /> Cancel Booking
                            </button>
                        )}
                    </motion.div>
                ))}

                {filteredRegistry.length === 0 && <p className="empty-msg">No records found in the registry matching that search.</p>}
            </div>
        </div>
    );
}

export default ManageBookings;
