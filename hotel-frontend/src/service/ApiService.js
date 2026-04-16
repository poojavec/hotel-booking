import axios from "axios";

/**
 * ApiService is a central place for ALL backend API calls.
 *
 * WHY use a class like this?
 * Instead of writing the full URL in every page component, we write it once here.
 * This makes the code cleaner and easier to maintain (change URL in one place only).
 *
 * We use 'axios' library to make HTTP requests.
 * WHY axios? It's simpler than the built-in fetch() API and automatically parses JSON.
 */
export default class ApiService {

    // The base URL of the backend. All API calls start with this.
    static BASE_URL = "http://localhost:8080";

    /**
     * Returns the Authorization header with the stored JWT token.
     * WHY? Protected endpoints require us to send the token to prove we are logged in.
     * The token is stored in the browser's localStorage after login.
     */
    static getHeader() {
        const token = localStorage.getItem("token"); // Get token saved at login
        return {
            Authorization: `Bearer ${token}`, // "Bearer " prefix is required by our backend
            "Content-Type": "application/json" // Tell the backend we're sending JSON
        };
    }

    // ========== AUTH ==========

    // Register a new user. Sends user data (name, email, password) to the backend.
    static async registerUser(registration) {
        const response = await axios.post(`${this.BASE_URL}/auth/register`, registration);
        return response.data; // response.data is the JSON body the backend sends back
    }

    // Login: sends email + password, gets back a JWT token and role.
    static async loginUser(loginDetails) {
        const response = await axios.post(`${this.BASE_URL}/auth/login`, loginDetails);
        return response.data;
    }

    // ========== HOTELS ==========

    // Get all hotels (public — no login needed)
    static async getAllHotels() {
        const response = await axios.get(`${this.BASE_URL}/hotels/all`);
        return response.data;
    }

    // Get one specific hotel by its ID (used on hotel details page)
    static async getHotelById(hotelId) {
        const response = await axios.get(`${this.BASE_URL}/hotels/${hotelId}`);
        return response.data;
    }

    // Search hotels by location name (e.g. "Paris")
    static async searchHotels(location) {
        const response = await axios.get(`${this.BASE_URL}/hotels/search?location=${location}`);
        return response.data;
    }

    // Get hotels that have available rooms for the given dates and location
    static async getAvailableHotelsByDateAndLocation(checkInDate, checkOutDate, location) {
        const response = await axios.get(
            `${this.BASE_URL}/hotels/available-hotels-by-date-and-location?checkInDate=${checkInDate}&checkOutDate=${checkOutDate}&location=${location}`
        );
        return response.data;
    }

    // ========== ROOMS ==========

    // Get available rooms for a specific hotel on specific dates
    static async getAvailableRooms(hotelId, checkInDate, checkOutDate) {
        const response = await axios.get(
            `${this.BASE_URL}/rooms/available-rooms-by-date-and-type?hotelId=${hotelId}&checkInDate=${checkInDate}&checkOutDate=${checkOutDate}`
        );
        return response.data;
    }

    // ========== BOOKINGS ==========

    // Book a specific room for a user (requires login — sends Authorization header)
    static async bookRoom(roomId, userId, booking) {
        const response = await axios.post(
            `${this.BASE_URL}/bookings/book-room/${roomId}/${userId}`,
            booking,
            { headers: this.getHeader() } // Send JWT token
        );
        return response.data;
    }

    // Get all bookings for a specific user (their booking history)
    static async getUserBookings(userId) {
        const response = await axios.get(
            `${this.BASE_URL}/bookings/user-bookings/${userId}`,
            { headers: this.getHeader() }
        );
        return response.data;
    }

    // Cancel a booking by its ID
    static async cancelBooking(bookingId) {
        const response = await axios.delete(
            `${this.BASE_URL}/bookings/cancel-booking/${bookingId}`,
            { headers: this.getHeader() }
        );
        return response.data;
    }

    // Update a booking's dates
    static async updateBooking(bookingId, bookingData) {
        const response = await axios.put(
            `${this.BASE_URL}/bookings/update-booking/${bookingId}`,
            bookingData,
            { headers: this.getHeader() }
        );
        return response.data;
    }

    // ========== ADMIN ENDPOINTS ==========

    // Add a new hotel (Admin only)
    static async addHotel(hotelData) {
        const response = await axios.post(`${this.BASE_URL}/hotels/add`, hotelData, {
            headers: this.getHeader()
        });
        return response.data;
    }

    // Update an existing hotel (Admin only)
    static async updateHotel(hotelId, hotelData) {
        const response = await axios.put(`${this.BASE_URL}/hotels/update/${hotelId}`, hotelData, {
            headers: this.getHeader()
        });
        return response.data;
    }

    // Delete a hotel (Admin only)
    static async deleteHotel(hotelId) {
        const response = await axios.delete(`${this.BASE_URL}/hotels/${hotelId}`, {
            headers: this.getHeader()
        });
        return response.data;
    }

    // Add a new room to a hotel (Admin only)
    // WHY params instead of body? Our backend uses @RequestParam, so data goes in URL params.
    static async addNewRoom(hotelId, roomData) {
        const response = await axios.post(`${this.BASE_URL}/rooms/add/${hotelId}`, null, {
            params: roomData,       // Sends data as query parameters e.g. ?roomType=Deluxe&price=299
            headers: this.getHeader()
        });
        return response.data;
    }

    // Update a room's details (Admin only)
    static async updateRoom(roomId, roomData) {
        const response = await axios.put(`${this.BASE_URL}/rooms/update/${roomId}`, null, {
            params: roomData,
            headers: this.getHeader()
        });
        return response.data;
    }

    // Delete a room (Admin only)
    static async deleteRoom(roomId) {
        const response = await axios.delete(`${this.BASE_URL}/rooms/delete/${roomId}`, {
            headers: this.getHeader()
        });
        return response.data;
    }

    // Get ALL bookings in the system (Admin only)
    static async getAllBookings() {
        const response = await axios.get(`${this.BASE_URL}/bookings/all`, {
            headers: this.getHeader()
        });
        return response.data;
    }

    // ========== AUTH HELPER METHODS ==========

    // Logout: remove all stored user data from the browser
    static logout() {
        localStorage.removeItem("token");
        localStorage.removeItem("role");
        localStorage.removeItem("userId");
    }

    // Check if a user is logged in (a token exists in localStorage)
    static isAuthenticated() {
        const token = localStorage.getItem("token");
        return !!token; // !! converts to boolean: null → false, "abc" → true
    }

    // Check if the logged-in user has the admin role
    static isAdmin() {
        const role = localStorage.getItem("role");
        return role === "ROLE_ADMIN";
    }

    // Check if the logged-in user has the regular user role
    static isUser() {
        const role = localStorage.getItem("role");
        return role === "ROLE_USER";
    }
}
