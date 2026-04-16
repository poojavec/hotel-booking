import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import ApiService from '../service/ApiService';
import { MapPin, Star, Calendar, Users, Home, ShieldCheck } from 'lucide-react';
import { motion } from 'framer-motion';
import toast from 'react-hot-toast';
import './Hotel.css';

/**
 * HotelDetails Page - shows info about one hotel and its available rooms.
 */
function HotelDetails() {
    const { id } = useParams(); // Get hotel ID from URL /hotel/:id
    const navigate = useNavigate();
    const location = useLocation();

    // State for hotel info and rooms
    const [hotel, setHotel] = useState(null);
    const [rooms, setRooms] = useState([]);
    const [loading, setLoading] = useState(true);

    // State for availability search dates
    const [checkIn, setCheckIn] = useState('');
    const [checkOut, setCheckOut] = useState('');

    const today = new Date().toISOString().split('T')[0];

    useEffect(() => {
        // Read dates from URL if they exist (passed from Home page search)
        const params = new URLSearchParams(location.search);
        const urlIn = params.get('checkIn');
        const urlOut = params.get('checkOut');
        
        if (urlIn) setCheckIn(urlIn);
        if (urlOut) setCheckOut(urlOut);

        fetchHotelAndRooms(urlIn, urlOut);
    }, [id]);

    const fetchHotelAndRooms = async (cin, cout) => {
        setLoading(true);
        try {
            // Step 1: Get basic hotel details
            const hotelResp = await ApiService.getHotelById(id);
            setHotel(hotelResp.hotel);

            // Step 2: Get rooms. If dates are provided, get available rooms. Otherwise get all.
            if (cin && cout) {
                const roomResp = await ApiService.getAvailableRooms(id, cin, cout);
                setRooms(roomResp.roomList || []);
            } else {
                setRooms(hotelResp.hotel?.rooms || []);
            }
        } catch (error) {
            toast.error("Error loading hotel details");
        } finally {
            setLoading(false);
        }
    };

    /**
     * Search for available rooms when dates are changed.
     */
    const handleCheckAvailability = async () => {
        if (!checkIn || !checkOut) return toast.error("Please select both dates");
        if (new Date(checkIn) >= new Date(checkOut)) return toast.error("Check-out must be after check-in");

        setLoading(true);
        try {
            const response = await ApiService.getAvailableRooms(id, checkIn, checkOut);
            setRooms(response.roomList || []);
            if (response.roomList?.length === 0) toast.error("No rooms available for these dates");
        } catch (error) {
            toast.error("Error searching rooms");
        } finally {
            setLoading(false);
        }
    };

    /**
     * Redirect to the booking page for the selected room.
     */
    const handleBookRoom = (roomId) => {
        if (!ApiService.isAuthenticated()) {
            toast.error("Please login to book a room");
            navigate('/login');
            return;
        }
        if (!checkIn || !checkOut) return toast.error("Please select dates first");

        navigate(`/book/${id}/${roomId}?checkIn=${checkIn}&checkOut=${checkOut}`);
    };

    if (loading && !hotel) return <div className="loading-overlay">Loading Hotel Majesty...</div>;

    return (
        <div className="page-container">
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
                
                {/* HOTEL INFO HEADER */}
                <div className="hotel-detail-header">
                    <div className="glass hotel-img-container">
                        <img 
                            src={hotel?.thumbnailUrl} 
                            alt={hotel?.name} 
                            className="hotel-full-img"
                        />
                    </div>
                    <div className="glass hotel-info-panel">
                        <h1 className="hotel-info-name">{hotel?.name}</h1>
                        <p className="hotel-info-loc">
                            <MapPin size={20} color="var(--accent-cyan)" /> {hotel?.location}
                        </p>
                        <div className="hotel-amenities">
                             {hotel?.amenities?.split(',').map(a => (
                                <span key={a} className="hotel-amenity">{a.trim()}</span>
                             ))}
                        </div>
                        <div className="glass hotel-rating-box">
                            <Star size={24} fill="var(--accent-cyan)" /> {hotel?.rating} / 5.0 Rating
                        </div>
                    </div>
                </div>

                {/* SEARCH AVAILABILITY BAR */}
                <div className="glass availability-bar">
                    <div className="date-field">
                        <label className="date-label">CHECK-IN DATE</label>
                        <input type="date" min={today} value={checkIn} onChange={(e) => setCheckIn(e.target.value)} className="date-input" />
                    </div>
                    <div className="date-field">
                        <label className="date-label">CHECK-OUT DATE</label>
                        <input type="date" min={checkIn || today} value={checkOut} onChange={(e) => setCheckOut(e.target.value)} className="date-input" />
                    </div>
                    <button onClick={handleCheckAvailability} className="btn-primary check-rooms-btn">
                        Check Available Rooms
                    </button>
                </div>

                {/* ROOMS LISTING */}
                <h2 className="rooms-section-title">Luxury <span className="gradient-text">Accommodations</span></h2>
                <div className="rooms-grid">
                    {rooms.map((room, index) => (
                        <motion.div 
                            key={room.id}
                            initial={{ opacity: 0, scale: 0.95 }}
                            whileInView={{ opacity: 1, scale: 1 }}
                            transition={{ delay: index * 0.1 }}
                            className="glass glass-hover room-card"
                        >
                            <div className="room-info">
                                <h3 className="room-title">{room.roomType}</h3>
                                <p className="room-desc">{room.description}</p>
                                
                                <div className="room-features">
                                    <span className="room-feature"><Users size={16} /> {room.capacity} Guests</span>
                                    <span className="room-feature"><ShieldCheck size={16} /> Luxury Class</span>
                                </div>

                                <div className="room-price">
                                    ${room.pricePerNight} <span className="room-price-night">/ night</span>
                                </div>

                                <button onClick={() => handleBookRoom(room.id)} className="btn-primary book-room-btn">Book This Room</button>
                            </div>
                        </motion.div>
                    ))}
                    {!loading && rooms.length === 0 && <div className="no-rooms-msg">No available suites found for these dates.</div>}
                </div>
            </motion.div>
        </div>
    );
}

export default HotelDetails;
