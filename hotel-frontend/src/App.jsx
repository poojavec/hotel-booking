import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';

// Import our page components
import Navbar from './components/Navbar';
import Home from './pages/Home';
import AllHotels from './pages/AllHotels';
import Login from './pages/Login';
import Register from './pages/Register';
import HotelDetails from './pages/HotelDetails';
import BookingPage from './pages/BookingPage';
import ProfilePage from './pages/ProfilePage';
import AdminDashboard from './pages/AdminDashboard';
import ManageHotels from './pages/ManageHotels';
import ManageBookings from './pages/ManageBookings';
import ApiService from './service/ApiService';

/**
 * ProtectedRoute: Wraps pages that require LOGIN to access.
 * If the user is NOT logged in, redirect them to the login page.
 * WHY? We don't want non-logged-in users accessing the booking or profile pages.
 */
const ProtectedRoute = ({ children }) => {
    const isLoggedIn = ApiService.isAuthenticated();
    // If logged in, show the page. Otherwise go to /login.
    return isLoggedIn ? children : <Navigate to="/login" />;
};

/**
 * AdminRoute: Wraps pages that require ADMIN role to access.
 * If the user is not an admin, redirect to login.
 */
const AdminRoute = ({ children }) => {
    const isAdmin = ApiService.isAdmin();
    return isAdmin ? children : <Navigate to="/login" />;
};

/**
 * App is the root component of our React app.
 * It sets up the router and defines all the page routes.
 *
 * BrowserRouter = enables URL-based navigation in React (uses history API)
 * Routes + Route = maps each URL path to a specific page component
 */
function App() {
    return (
        <BrowserRouter>
            {/* Toaster shows popup notifications (success/error) anywhere in the app */}
            <Toaster position="top-center" reverseOrder={false} />

            <div className="App">
                {/* Navbar is shown on every page */}
                <Navbar />

                <div className="content">
                    <Routes>
                        {/* Public pages — anyone can visit these */}
                        <Route path="/home" element={<Home />} />
                        <Route path="/hotels" element={<AllHotels />} />
                        <Route path="/login" element={<Login />} />
                        <Route path="/register" element={<Register />} />
                        <Route path="/hotel/:id" element={<HotelDetails />} />

                        {/* Protected pages — must be logged in */}
                        <Route path="/book/:hotelId/:roomId" element={
                            <ProtectedRoute>
                                <BookingPage />
                            </ProtectedRoute>
                        } />
                        <Route path="/profile" element={
                            <ProtectedRoute>
                                <ProfilePage />
                            </ProtectedRoute>
                        } />

                        {/* Admin-only pages */}
                        <Route path="/admin" element={
                            <AdminRoute>
                                <AdminDashboard />
                            </AdminRoute>
                        } />
                        <Route path="/admin/hotels" element={
                            <AdminRoute>
                                <ManageHotels />
                            </AdminRoute>
                        } />
                        <Route path="/admin/bookings" element={
                            <AdminRoute>
                                <ManageBookings />
                            </AdminRoute>
                        } />

                        {/* Fallback: any unknown URL goes to home */}
                        <Route path="*" element={<Navigate to="/home" />} />
                    </Routes>
                </div>
            </div>
        </BrowserRouter>
    );
}

export default App;
