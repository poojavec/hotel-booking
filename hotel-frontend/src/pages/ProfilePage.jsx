import React, { useState, useEffect } from 'react';
import ApiService from '../service/ApiService';
import { motion, AnimatePresence } from 'framer-motion';
import { User, History, MapPin, Calendar, Clock, LogOut, Edit2, XCircle } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import './ProfilePage.css';

/**
 * ProfilePage - Shows user info and their booking history.
 * Users can also edit or cancel their bookings here.
 */
function ProfilePage() {
    // Current user and their bookings
    const [user, setUser] = useState(null);
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);

    // Editing State (for the Edit Modal)
    const [editingBooking, setEditingBooking] = useState(null);
    const [editForm, setEditForm] = useState({ checkInDate: '', checkOutDate: '' });

    const navigate = useNavigate();

    // Fetch profile data when the page loads
    useEffect(() => {
        fetchProfileAndBookings();
    }, []);

    const fetchProfileAndBookings = async () => {
        try {
            const userId = localStorage.getItem('userId');
            const response = await ApiService.getUserBookings(userId);
            
            setBookings(response.bookingList || []);
            
            // Set user info from the first booking (or a default if no bookings)
            if (response.bookingList?.length > 0) {
                setUser(response.bookingList[0].user);
            } else {
                // If no bookings, we could fetch user info from another endpoint, 
                // but for simplicity we'll show a generic name if unavailable
                setUser({ firstName: 'User', lastName: 'Member' });
            }
        } catch (error) {
            toast.error("Failed to load profile data");
        } finally {
            setLoading(false);
        }
    };

    /**
     * Log out the user and redirect to login page.
     */
    const handleLogout = () => {
        if (window.confirm("Are you sure you want to logout?")) {
            ApiService.logout();
            navigate('/login');
        }
    };

    /**
     * Cancel a booking (changes status to CANCELLED).
     */
    const handleCancelClick = async (bookingId) => {
        if (!window.confirm("Are you sure you want to cancel this booking?")) return;
        
        try {
            await ApiService.cancelBooking(bookingId);
            toast.success("Booking cancelled successfully!");
            fetchProfileAndBookings(); // Refresh the list
        } catch (error) {
            toast.error("Failed to cancel booking");
        }
    };

    /**
     * Opens the Edit Modal for a specific booking.
     */
    const openEditModal = (booking) => {
        setEditingBooking(booking);
        setEditForm({
            checkInDate: booking.checkInDate,
            checkOutDate: booking.checkOutDate
        });
    };

    /**
     * Submits the updated booking dates to the backend.
     */
    const handleEditSubmit = async (e) => {
        e.preventDefault();
        try {
            await ApiService.updateBooking(editingBooking.id, editForm);
            toast.success("Stay dates updated!");
            setEditingBooking(null);     // Close modal
            fetchProfileAndBookings();    // Refresh list
        } catch (error) {
            toast.error(error.response?.data?.message || "Select different dates");
        }
    };

    if (loading) return <div className="profile-loading">Accessing your profile...</div>;

    return (
        <div className="profile-container">
            <div className="profile-grid">
                
                {/* LEFT SIDE: USER CARD */}
                <motion.div initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} className="glass user-card">
                    <div className="user-card-header">
                        <div className="user-avatar-container">
                            <User size={50} color="white" />
                        </div>
                        <h2 className="user-name">{user?.firstName} {user?.lastName}</h2>
                        <p className="user-level">Premium Member</p>
                    </div>

                    <div className="user-stats">
                        <p className="user-stat-item"><Clock size={16} className="stat-icon" /> Member since 2026</p>
                        <p><History size={16} className="stat-icon" /> {bookings.length} Historic Bookings</p>
                    </div>

                    <button onClick={handleLogout} className="btn-secondary logout-btn">
                        <LogOut size={18} /> Logout Account
                    </button>
                </motion.div>

                {/* RIGHT SIDE: BOOKINGS LIST */}
                <motion.div initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }}>
                    <h2 className="history-title">Stay <span className="gradient-text">History</span></h2>
                    
                    <div className="bookings-list">
                        {bookings.map((booking, index) => (
                            <motion.div key={booking.id} initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: index * 0.1 }} className="glass booking-card">
                                <img src={booking.room.hotel?.thumbnailUrl} className="booking-img" />
                                
                                <div>
                                    <h3 className="hotel-title">{booking.room.hotel?.name}</h3>
                                    <p className="hotel-addr"><MapPin size={14} /> {booking.room.hotel?.location}</p>
                                    <p className="room-type-text">{booking.room.roomType}</p>
                                    <p className="booking-code">Code: {booking.bookingConfirmationCode}</p>
                                </div>

                                <div className="booking-right-panel">
                                    <div className="booking-dates">
                                        <Calendar size={14} /> {new Date(booking.checkInDate).toLocaleDateString()} - {new Date(booking.checkOutDate).toLocaleDateString()}
                                    </div>
                                    <div className="booking-amount">${booking.totalAmount}</div>
                                    
                                    <div className={`status-badge ${booking.bookingStatus === 'CONFIRMED' ? 'status-confirmed' : 'status-cancelled'}`}>
                                        {booking.bookingStatus}
                                    </div>

                                    {booking.bookingStatus === 'CONFIRMED' && (
                                        <div className="booking-actions">
                                            <button onClick={() => openEditModal(booking)} className="btn-secondary action-btn">
                                                <Edit2 size={14}/> Edit
                                            </button>
                                            <button onClick={() => handleCancelClick(booking.id)} className="btn-primary action-btn btn-danger">
                                                <XCircle size={14}/> Cancel
                                            </button>
                                        </div>
                                    )}
                                </div>
                            </motion.div>
                        ))}

                        {bookings.length === 0 && <div className="glass no-bookings">No reservations found.</div>}
                    </div>
                </motion.div>
            </div>

            {/* EDIT MODAL */}
            <AnimatePresence>
                {editingBooking && (
                    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="modal-overlay">
                        <motion.div initial={{ scale: 0.9 }} animate={{ scale: 1 }} exit={{ scale: 0.9 }} className="glass modal-content">
                            <h2 className="modal-title">Modify Selection</h2>
                            <form onSubmit={handleEditSubmit}>
                                <div className="modal-form-group">
                                    <label className="form-label">New Check-in</label>
                                    <input type="date" required value={editForm.checkInDate} onChange={(e) => setEditForm({...editForm, checkInDate: e.target.value})} className="modal-input" />
                                </div>
                                <div className="modal-form-group-last">
                                    <label className="form-label">New Check-out</label>
                                    <input type="date" required value={editForm.checkOutDate} onChange={(e) => setEditForm({...editForm, checkOutDate: e.target.value})} className="modal-input" />
                                </div>
                                <div className="modal-actions">
                                    <button type="button" onClick={() => setEditingBooking(null)} className="btn-secondary modal-btn">Discard Changes</button>
                                    <button type="submit" className="btn-primary modal-btn">Update Stay</button>
                                </div>
                            </form>
                        </motion.div>
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    );
}

export default ProfilePage;
