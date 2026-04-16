import React, { useState, useEffect } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import ApiService from '../service/ApiService';
import { motion } from 'framer-motion';
import { Calendar, CreditCard, Sparkles, CheckCircle, ArrowLeft } from 'lucide-react';
import toast from 'react-hot-toast';
import './Hotel.css';

/**
 * BookingPage - Final step to confirm a hotel room reservation.
 */
function BookingPage() {
    // Get hotel and room IDs from the URL path
    const { hotelId, roomId } = useParams();
    const location = useLocation();
    const navigate = useNavigate();

    // Read initial dates from URL (passed from previous search)
    const queryParams = new URLSearchParams(location.search);
    const initialCheckIn = queryParams.get('checkIn') || '';
    const initialCheckOut = queryParams.get('checkOut') || '';

    // State variables
    const [hotel, setHotel] = useState(null);
    const [room, setRoom] = useState(null);
    const [checkIn, setCheckIn] = useState(initialCheckIn);
    const [checkOut, setCheckOut] = useState(initialCheckOut);
    const [totalAmount, setTotalAmount] = useState(0);
    const [confirmationCode, setConfirmationCode] = useState('');
    const [loading, setLoading] = useState(true);
    const [bookingSuccess, setBookingSuccess] = useState(false);

    const today = new Date().toISOString().split('T')[0];

    // Load hotel and room details when the page opens
    useEffect(() => {
        fetchBookingInfo();
    }, [hotelId, roomId]);

    // Recalculate price whenever dates or room info changes
    useEffect(() => {
        if (checkIn && checkOut && room) {
            updateTotalCost();
        }
    }, [checkIn, checkOut, room]);

    const fetchBookingInfo = async () => {
        try {
            const response = await ApiService.getHotelById(hotelId);
            setHotel(response.hotel);
            
            // Find the specific room in the hotel's room list
            const foundRoom = response.hotel.rooms.find(r => r.id === parseInt(roomId));
            setRoom(foundRoom);
        } catch (error) {
            toast.error("Failed to load property details");
        } finally {
            setLoading(false);
        }
    };

    /**
     * Calculates total price based on: (check-out - check-in) * price per night
     */
    const updateTotalCost = () => {
        const start = new Date(checkIn);
        const end = new Date(checkOut);
        
        // Difference in milliseconds
        const diffInMs = end - start;
        // Convert to days
        const diffInDays = Math.ceil(diffInMs / (1000 * 60 * 60 * 24));
        
        if (diffInDays > 0) {
            setTotalAmount(diffInDays * room.pricePerNight);
        } else {
            setTotalAmount(0);
        }
    };

    /**
     * Sends the booking request to the backend.
     */
    const handleConfirmBooking = async () => {
        if (!checkIn || !checkOut) return toast.error("Select your stay dates");
        if (new Date(checkIn) >= new Date(checkOut)) return toast.error("Check-out must be after check-in");
        
        setLoading(true);
        try {
            const userId = localStorage.getItem('userId');
            const bookingData = {
                checkInDate: checkIn,
                checkOutDate: checkOut,
                totalAmount: totalAmount
            };

            const response = await ApiService.bookRoom(roomId, userId, bookingData);
            
            if (response.statusCode === 200) {
                setConfirmationCode(response.bookingConfirmationCode);
                setBookingSuccess(true);
                toast.success("Reservation Secured!");
            }
        } catch (error) {
            toast.error(error.response?.data?.message || "Booking failed");
        } finally {
            setLoading(false);
        }
    };

    if (loading && !bookingSuccess) return <div className="loading-overlay">Finalizing your luxury experience...</div>;

    // Show SUCCESS screen after booking
    if (bookingSuccess) {
        return (
            <div className="success-screen">
                <motion.div initial={{ scale: 0.8, opacity: 0 }} animate={{ scale: 1, opacity: 1 }} className="glass success-card">
                    <CheckCircle size={80} color="var(--accent-cyan)" style={{ marginBottom: '30px' }} />
                    <h1 className="success-title">Stay <span className="gradient-text">Confirmed!</span></h1>
                    <p className="success-subtitle">Your luxury suite at {hotel?.name} is waiting for you.</p>
                    <div className="glass confirmation-code-box">
                        <p className="confirmation-label">CONFIRMATION CODE</p>
                        <h2 className="confirmation-number">{confirmationCode}</h2>
                    </div>
                    <button onClick={() => navigate('/home')} className="btn-primary btn-full">Return to Discovery</button>
                </motion.div>
            </div>
        );
    }

    return (
        <div className="booking-page-container">
            <button onClick={() => navigate(-1)} className="back-btn">
                <ArrowLeft size={18} style={{ marginRight: '10px' }} /> Back to Selection
            </button>

            <div className="booking-grid">
                {/* LEFT SIDE: FORM & PRICING */}
                <motion.div initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} className="glass reservation-form-panel">
                    <h2 className="reservation-title">Complete Reservation</h2>
                    
                    <div className="date-picker-row">
                        <div className="date-picker-group">
                            <label className="date-label">CHECK-IN</label>
                            <div className="date-picker-wrapper">
                                <Calendar size={18} color="var(--accent-cyan)" style={{ marginRight: '10px' }} />
                                <input type="date" min={today} value={checkIn} onChange={(e) => setCheckIn(e.target.value)} className="date-picker-input" />
                            </div>
                        </div>
                        <div className="date-picker-group">
                            <label className="date-label">CHECK-OUT</label>
                            <div className="date-picker-wrapper">
                                <Calendar size={18} color="var(--accent-cyan)" style={{ marginRight: '10px' }} />
                                <input type="date" min={checkIn || today} value={checkOut} onChange={(e) => setCheckOut(e.target.value)} className="date-picker-input" />
                            </div>
                        </div>
                    </div>

                    <div className="glass summary-panel">
                        <h3 className="summary-title"><CreditCard size={20} color="var(--accent-cyan)" /> Stay Summary</h3>
                        <div className="summary-row">
                            <span>Suite Type: {room?.roomType}</span>
                            <span>${room?.pricePerNight} / Night</span>
                        </div>
                        <div className="summary-divider" />
                        <div className="summary-total">
                            <span>Total Due</span>
                            <span className="gradient-text">${totalAmount}</span>
                        </div>
                    </div>

                    <button onClick={handleConfirmBooking} className="btn-primary confirm-booking-btn">
                        <Sparkles size={24} /> Confirm Reservation
                    </button>
                </motion.div>

                {/* RIGHT SIDE: HOTEL PREVIEW */}
                <motion.div initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} className="glass hotel-preview-panel">
                    <img src={hotel?.thumbnailUrl} className="hotel-preview-img" />
                    <div className="hotel-preview-info">
                        <h3 className="hotel-preview-name">{hotel?.name}</h3>
                        <p className="hotel-preview-loc">{hotel?.location}</p>
                        <div className="hotel-preview-perks">
                            <p>● Priority Airport Transfer</p>
                            <p>● 24/7 Personal Butler Service</p>
                            <p>● Complimentary Spa Access</p>
                        </div>
                    </div>
                </motion.div>
            </div>
        </div>
    );
}

export default BookingPage;
