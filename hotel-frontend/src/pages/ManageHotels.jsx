import React, { useState, useEffect } from 'react';
import ApiService from '../service/ApiService';
import toast from 'react-hot-toast';
import { motion } from 'framer-motion';
import { X, Edit2, Plus, Trash2 } from 'lucide-react';
import './Admin.css';

/**
 * ManageHotels - Admin page for adding, editing, and deleting hotels and rooms.
 */
function ManageHotels() {
    // Lists and Loading
    const [hotels, setHotels] = useState([]);
    const [loading, setLoading] = useState(true);
    
    // Hotel Addition States
    const [showAddModal, setShowAddModal] = useState(false);
    const [newHotel, setNewHotel] = useState({ name: '', location: '', description: '', rating: 5, amenities: '', thumbnailUrl: '' });

    // Hotel Editing States
    const [showEditModal, setShowEditModal] = useState(false);
    const [editingHotel, setEditingHotel] = useState(null);

    // Room Addition States
    const [showAddRoomModal, setShowAddRoomModal] = useState(false);
    const [targetHotelId, setTargetHotelId] = useState(null);
    const [newRoom, setNewRoom] = useState({ roomType: '', price: '', capacity: '', description: '', amenities: '' });

    // Look at Admin.css for `.form-input-admin` instead of `inputStyle`

    // Load all hotels when component mounts
    useEffect(() => {
        fetchAllHotels();
    }, []);

    const fetchAllHotels = async () => {
        try {
            const response = await ApiService.getAllHotels();
            setHotels(response.hotelList || []);
        } catch (error) {
            toast.error("Error retrieving properties");
        } finally {
            setLoading(false);
        }
    };

    // --- HOTEL ACTIONS ---

    const handleAddHotelSubmit = async (e) => {
        e.preventDefault();
        try {
            await ApiService.addHotel(newHotel);
            toast.success("Property added to records!");
            setShowAddModal(false); // Close the form
            setNewHotel({ name: '', location: '', description: '', rating: 5, amenities: '', thumbnailUrl: '' }); // Reset
            fetchAllHotels(); // Refresh the list
        } catch (error) {
            toast.error("Failed to add property");
        }
    };

    const handleUpdateHotelSubmit = async (e) => {
        e.preventDefault();
        try {
            await ApiService.updateHotel(editingHotel.id, editingHotel);
            toast.success("Property updated successfully!");
            setShowEditModal(false);
            fetchAllHotels();
        } catch (error) {
            toast.error("Failed to update property");
        }
    };

    const handleHotelDelete = async (hotelId) => {
        if (!window.confirm("Permanently delete this hotel and all its rooms?")) return;
        try {
            await ApiService.deleteHotel(hotelId);
            toast.success("Property removed.");
            fetchAllHotels();
        } catch (error) {
            toast.error("Error deleting property");
        }
    };

    // --- ROOM ACTIONS ---

    const handleAddRoomSubmit = async (e) => {
        e.preventDefault();
        try {
            // targetHotelId is set when the "Add Room" button is clicked for a specific hotel
            await ApiService.addNewRoom(targetHotelId, newRoom);
            toast.success("Room registered to hotel!");
            setShowAddRoomModal(false);
            setNewRoom({ roomType: '', price: '', capacity: '', description: '', amenities: '' }); // Reset
            fetchAllHotels();
        } catch (error) {
            toast.error("Failed to register room");
        }
    };

    const handleRoomDelete = async (roomId) => {
        if (!window.confirm("Delete this specific room?")) return;
        try {
            await ApiService.deleteRoom(roomId);
            toast.success("Room removed.");
            fetchAllHotels();
        } catch (error) {
            toast.error("Error removing room");
        }
    };

    if (loading) return <div className="loading-overlay">Syncing with property database...</div>;

    return (
        <div className="admin-container">
            
            {/* TOP ACTIONS BAR */}
            <div className="admin-top-bar">
                <motion.h1 initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="admin-title" style={{marginBottom: 0}}>Property <span className="gradient-text">Management</span></motion.h1>
                <button onClick={() => {setShowAddModal(true); setShowEditModal(false);}} className="btn-primary top-bar-btn">
                    <Plus size={20} /> Add New Property
                </button>
            </div>
            
            {/* ADD HOTEL MODAL BOX (Conditional Rendering) */}
            {showAddModal && (
                <motion.div initial={{ height: 0, opacity: 0 }} animate={{ height: 'auto', opacity: 1 }} className="glass modal-box">
                    <div className="modal-header">
                        <h2 className="modal-title-cyan">Register Luxury Hotel</h2>
                        <button onClick={() => setShowAddModal(false)} className="close-btn"><X size={24} /></button>
                    </div>
                    <form onSubmit={handleAddHotelSubmit} className="form-grid-2">
                        <input placeholder="Hotel Name" required value={newHotel.name} onChange={e => setNewHotel({...newHotel, name: e.target.value})} className="form-input-admin" />
                        <input placeholder="City, Country" required value={newHotel.location} onChange={e => setNewHotel({...newHotel, location: e.target.value})} className="form-input-admin" />
                        <input placeholder="Amenities (WiFi, Gym, Pool...)" value={newHotel.amenities} onChange={e => setNewHotel({...newHotel, amenities: e.target.value})} className="form-input-admin" />
                        <input placeholder="Image URL (Thumbnail)" value={newHotel.thumbnailUrl} onChange={e => setNewHotel({...newHotel, thumbnailUrl: e.target.value})} className="form-input-admin" />
                        <textarea placeholder="Tell us about the property..." required value={newHotel.description} onChange={e => setNewHotel({...newHotel, description: e.target.value})} className="form-input-admin form-textarea" />
                        <button type="submit" className="btn-primary form-submit-btn">Save Property</button>
                    </form>
                </motion.div>
            )}

            {/* EDIT HOTEL MODAL BOX */}
            {showEditModal && editingHotel && (
                <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="glass modal-box-violet">
                    <div className="modal-header">
                        <h2 className="modal-title-violet">Update Property - ID {editingHotel.id}</h2>
                        <button onClick={() => setShowEditModal(false)} className="close-btn"><X size={24} /></button>
                    </div>
                    <form onSubmit={handleUpdateHotelSubmit} className="form-grid-2">
                        <input placeholder="Name" required value={editingHotel.name} onChange={e => setEditingHotel({...editingHotel, name: e.target.value})} className="form-input-admin" />
                        <input placeholder="Location" required value={editingHotel.location} onChange={e => setEditingHotel({...editingHotel, location: e.target.value})} className="form-input-admin" />
                        <input placeholder="Amenities" value={editingHotel.amenities} onChange={e => setEditingHotel({...editingHotel, amenities: e.target.value})} className="form-input-admin" />
                        <input placeholder="Image URL" value={editingHotel.thumbnailUrl} onChange={e => setEditingHotel({...editingHotel, thumbnailUrl: e.target.value})} className="form-input-admin" />
                        <textarea placeholder="Description" required value={editingHotel.description} onChange={e => setEditingHotel({...editingHotel, description: e.target.value})} className="form-input-admin form-textarea" />
                        <button type="submit" className="btn-primary form-submit-btn btn-violet">Confirm Changes</button>
                    </form>
                </motion.div>
            )}

            {/* ADD ROOM MODAL BOX */}
            {showAddRoomModal && (
                <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="glass modal-box">
                    <div className="modal-header">
                        <h2 className="modal-title-cyan">Register New Room</h2>
                        <button onClick={() => setShowAddRoomModal(false)} className="close-btn"><X size={24} /></button>
                    </div>
                    <form onSubmit={handleAddRoomSubmit} className="form-grid-2">
                        <input placeholder="Room Type (e.g. Presidential Suite)" required value={newRoom.roomType} onChange={e => setNewRoom({...newRoom, roomType: e.target.value})} className="form-input-admin" />
                        <input type="number" placeholder="Price Per Night ($)" required value={newRoom.price} onChange={e => setNewRoom({...newRoom, price: e.target.value})} className="form-input-admin" />
                        <input type="number" placeholder="Max Guests (Capacity)" required value={newRoom.capacity} onChange={e => setNewRoom({...newRoom, capacity: e.target.value})} className="form-input-admin" />
                        <input placeholder="Room Amenities" value={newRoom.amenities} onChange={e => setNewRoom({...newRoom, amenities: e.target.value})} className="form-input-admin" />
                        <textarea placeholder="Room Features description..." required value={newRoom.description} onChange={e => setNewRoom({...newRoom, description: e.target.value})} className="form-input-admin form-textarea-small" />
                        <button type="submit" className="btn-primary form-submit-btn">Save Room Details</button>
                    </form>
                </motion.div>
            )}

            {/* LIST OF HOTELS AND THEIR ROOMS */}
            <div className="hotel-list-container">
                {hotels.map(hotel => (
                    <motion.div key={hotel.id} initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="glass hotel-list-item">
                        <div className="hotel-item-header">
                            <div>
                                <h2 className="hotel-item-title">{hotel.name}</h2>
                                <p className="hotel-item-loc">{hotel.location}</p>
                            </div>
                            <div className="hotel-actions-group">
                                <button onClick={() => {setEditingHotel({...hotel}); setShowEditModal(true);}} className="btn-secondary admin-btn-action"><Edit2 size={16}/> Edit</button>
                                <button onClick={() => handleHotelDelete(hotel.id)} className="btn-primary admin-btn-danger admin-btn-action"><Trash2 size={16}/> Wipe Property</button>
                            </div>
                        </div>
                        
                        {/* THE NESTED ROOMS SECTION FOR THIS HOTEL */}
                        <div className="rooms-section">
                            <div className="rooms-section-header">
                                <h3 className="rooms-section-title">Managed Suites</h3>
                                <button onClick={() => {setTargetHotelId(hotel.id); setShowAddRoomModal(true);}} className="btn-secondary admin-btn-action"><Plus size={16}/> Add New Suite</button>
                            </div>
                            
                            {hotel.rooms?.length > 0 ? (
                                <div className="room-grid">
                                    {hotel.rooms.map(room => (
                                        <div key={room.id} className="room-admin-card">
                                            <button onClick={() => handleRoomDelete(room.id)} className="delete-room-btn"><Trash2 size={18}/></button>
                                            <p className="room-type-title">{room.roomType}</p>
                                            <p className="room-price-admin">${room.pricePerNight} / night</p>
                                            <p className="room-capacity">Adults: {room.capacity} | {room.amenities}</p>
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <p className="empty-msg">No suites registered yet.</p>
                            )}
                        </div>
                    </motion.div>
                ))}
            </div>
        </div>
    );
}

export default ManageHotels;
