import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import ApiService from '../service/ApiService';
import { Search, MapPin, Calendar, Star, ArrowRight } from 'lucide-react';
import { motion } from 'framer-motion';
import './Home.css';

/**
 * Home Page - the landing page where users can search for hotels.
 */
function Home() {
    // Search states
    const [location, setLocation] = useState('');     // City name
    const [checkIn, setCheckIn] = useState('');       // Check-in date
    const [checkOut, setCheckOut] = useState('');     // Check-out date
    const [hotels, setHotels] = useState([]);         // List of hotels to display
    const [loading, setLoading] = useState(true);     // Loading spinner state

    const navigate = useNavigate();
    const today = new Date().toISOString().split('T')[0]; // Current date as YYYY-MM-DD

    // Fetch initial hotels when component loads
    useEffect(() => {
        fetchFeaturedHotels();
    }, []);

    const fetchFeaturedHotels = async () => {
        try {
            const response = await ApiService.getAllHotels();
            // Take only the first 3 hotels as "featured"
            setHotels(response.hotelList?.slice(0, 3) || []);
        } catch (error) {
            console.error("Error fetching featured hotels", error);
        } finally {
            setLoading(false);
        }
    };

    /**
     * Handles search button click.
     * Validates dates and redirects to AllHotels with search parameters.
     */
    const handleSearch = () => {
        if (!location) return alert("Please enter a location");
        
        // Build URL parameters for searching
        let query = `?location=${location}`;
        if (checkIn && checkOut) {
            query += `&checkIn=${checkIn}&checkOut=${checkOut}`;
        }
        navigate(`/hotels${query}`);
    };

    return (
        <div className="home-container">
            {/* HERO SECTION */}
            <motion.div initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} className="hero-section">
                <h1 className="hero-title">
                    Luxury Redefined at <span className="gradient-text">Ethereal Midnight</span>
                </h1>
                <p className="hero-subtitle">
                    Experience ultimate sophistication in the world's most exclusive properties. 
                    Your journey to absolute comfort starts here.
                </p>

                {/* SEARCH BAR */}
                <div className="glass search-bar-container">
                    <div className="search-field-large">
                        <label className="search-label">WHERE ARE YOU GOING?</label>
                        <div className="search-input-wrapper">
                            <MapPin size={18} color="var(--accent-cyan)" className="search-icon" />
                            <input type="text" placeholder="Location (e.g. Paris)" value={location} onChange={(e) => setLocation(e.target.value)} className="search-input" />
                        </div>
                    </div>

                    <div className="search-field-small">
                        <label className="search-label">CHECK IN</label>
                        <div className="search-input-wrapper">
                            <Calendar size={18} color="var(--accent-cyan)" className="search-icon" />
                            <input type="date" min={today} value={checkIn} onChange={(e) => setCheckIn(e.target.value)} className="search-input-date" />
                        </div>
                    </div>

                    <div className="search-field-small">
                        <label className="search-label">CHECK OUT</label>
                        <div className="search-input-wrapper">
                            <Calendar size={18} color="var(--accent-cyan)" className="search-icon" />
                            <input type="date" min={checkIn || today} value={checkOut} onChange={(e) => setCheckOut(e.target.value)} className="search-input-date" />
                        </div>
                    </div>

                    <button onClick={handleSearch} className="btn-primary search-button">
                        <Search size={20} /> Search Available
                    </button>
                </div>
            </motion.div>

            {/* FEATURED SECTION */}
            <div className="featured-section">
                <div className="featured-header">
                    <div>
                        <h2 className="featured-title">Featured <span className="gradient-text">Destinations</span></h2>
                        <p className="featured-subtitle">Exclusively picked properties for your premium stay.</p>
                    </div>
                    <button onClick={() => navigate('/hotels')} className="btn-secondary view-all-btn">
                        View All Hotels <ArrowRight size={18} />
                    </button>
                </div>

                {loading ? (
                    <div className="loading-text">Loading featured stays...</div>
                ) : (
                    <div className="hotel-grid">
                        {hotels.map((hotel, index) => (
                            <motion.div 
                                key={hotel.id}
                                initial={{ opacity: 0, scale: 0.95 }}
                                whileInView={{ opacity: 1, scale: 1 }}
                                transition={{ delay: index * 0.1 }}
                                className="glass glass-hover hotel-card"
                                onClick={() => navigate(`/hotel/${hotel.id}`)}
                            >
                                <img src={hotel.thumbnailUrl} alt={hotel.name} className="hotel-image" />
                                <div className="hotel-info">
                                    <div className="hotel-info-header">
                                        <h3 className="hotel-name">{hotel.name}</h3>
                                        <div className="hotel-rating">
                                            <Star size={16} fill="var(--accent-cyan)" /> {hotel.rating}
                                        </div>
                                    </div>
                                    <p className="hotel-location">
                                        <MapPin size={16} /> {hotel.location}
                                    </p>
                                    <div className="amenities-container">
                                        {hotel.amenities?.split(',').slice(0, 3).map(a => (
                                            <span key={a} className="amenity-badge">{a.trim()}</span>
                                        ))}
                                    </div>
                                </div>
                            </motion.div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}

export default Home;
