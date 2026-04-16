import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import ApiService from '../service/ApiService';
import { motion } from 'framer-motion';
import { MapPin, Star, Building2, Search } from 'lucide-react';
import './Hotel.css';

/**
 * AllHotels Page - displays a list of all hotels, or filtered hotels based on search.
 */
function AllHotels() {
    const [hotels, setHotels] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState(''); // Real-time search filter

    const location = useLocation(); // Used to read URL search parameters
    const navigate = useNavigate();

    // Fetch hotels when page loads or when URL search parameters change
    useEffect(() => {
        const queryParams = new URLSearchParams(location.search);
        const loc = queryParams.get('location');
        const checkIn = queryParams.get('checkIn');
        const checkOut = queryParams.get('checkOut');

        if (loc && checkIn && checkOut) {
            // If user searched for dates and location
            fetchAvailableHotels(checkIn, checkOut, loc);
        } else if (loc) {
            // If user searched only for location
            handleSearchLocation(loc);
        } else {
            // Default: show all hotels
            fetchAllHotels();
        }
    }, [location.search]);

    const fetchAllHotels = async () => {
        setLoading(true);
        try {
            const response = await ApiService.getAllHotels();
            setHotels(response.hotelList || []);
        } catch (error) {
            console.error("Error fetching hotels", error);
        } finally {
            setLoading(false);
        }
    };

    const handleSearchLocation = async (loc) => {
        setLoading(true);
        try {
            const response = await ApiService.searchHotels(loc);
            setHotels(response.hotelList || []);
        } catch (error) {
            console.error("Error searching location", error);
        } finally {
            setLoading(false);
        }
    };

    const fetchAvailableHotels = async (checkIn, checkOut, loc) => {
        setLoading(true);
        try {
            const response = await ApiService.getAvailableHotelsByDateAndLocation(checkIn, checkOut, loc);
            setHotels(response.hotelList || []);
        } catch (error) {
            console.error("Error fetching available hotels", error);
        } finally {
            setLoading(false);
        }
    };

    /**
     * Filter hotels list based on what user types in the search bar on THIS page.
     * This is an "on-the-fly" filter (local search).
     */
    const filteredHotels = hotels.filter(hotel => 
        hotel.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        hotel.location.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="page-container">
            {/* PAGE HEADER & SEARCH BAR */}
            <div className="page-header">
                <div>
                    <h1 className="page-title">Luxury <span className="gradient-text">Properties</span></h1>
                    <p className="page-subtitle">Explore our curated collection of worldwide hotels.</p>
                </div>

                <div className="glass search-filter-container">
                    <Search size={20} color="var(--accent-cyan)" style={{ marginRight: '10px' }} />
                    <input 
                        type="text" 
                        placeholder="Filter by name or city..." 
                        value={searchTerm} 
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="search-filter-input" 
                    />
                </div>
            </div>

            {/* HOTELS GRID */}
            {loading ? (
                <div className="loading-overlay">Finding luxury destinations...</div>
            ) : (
                <div className="hotel-grid-dynamic">
                    {filteredHotels.map((hotel, index) => (
                        <motion.div 
                            key={hotel.id}
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ delay: index * 0.05 }}
                            className="glass glass-hover hotel-card-main"
                            onClick={() => navigate(`/hotel/${hotel.id}`)}
                        >
                            <img src={hotel.thumbnailUrl} alt={hotel.name} className="hotel-card-image" />
                            <div className="hotel-card-content">
                                <div className="hotel-card-header">
                                    <h3 className="hotel-card-title">{hotel.name}</h3>
                                    <div className="hotel-card-rating">
                                        <Star size={14} fill="var(--accent-cyan)" /> {hotel.rating}
                                    </div>
                                </div>
                                <p className="hotel-card-loc">
                                    <MapPin size={16} color="var(--accent-cyan)" /> {hotel.location}
                                </p>
                                <div className="amenity-list">
                                    {hotel.amenities?.split(',').slice(0, 2).map(a => (
                                        <span key={a} className="amenity-chip">{a.trim()}</span>
                                    ))}
                                </div>
                                <button className="btn-secondary card-action-btn">
                                    <Building2 size={16} /> View Available Rooms
                                </button>
                            </div>
                        </motion.div>
                    ))}

                    {filteredHotels.length === 0 && (
                        <div className="no-hotels-msg">
                            No hotels found matching your search.
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}

export default AllHotels;
