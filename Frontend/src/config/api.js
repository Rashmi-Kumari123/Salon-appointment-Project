import axios from "axios";
const LOCALHOST='http://localhost:5000'

export const API_BASE_URL = LOCALHOST

const api = axios.create({
  baseURL: API_BASE_URL,
});

api.defaults.headers.post['Content-Type'] = 'application/json';

// Request Interceptor: Automatically add JWT token to all requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("jwt");
    // Only add token if it exists and Authorization header is not already set
    if (token && !config.headers.Authorization) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response Interceptor: Handle errors globally (especially 401 Unauthorized)
api.interceptors.response.use(
  (response) => {
    // If response is successful, just return it
    return response;
  },
  (error) => {
    // Handle 401 Unauthorized - token expired or invalid
    if (error.response?.status === 401) {
      // Clear invalid token
      localStorage.removeItem("jwt");
      // Redirect to login page (only if not already on auth pages)
      if (!window.location.pathname.includes("/login") && 
          !window.location.pathname.includes("/register")) {
        window.location.href = "/login";
      }
    }
    // Return error so individual actions can handle it
    return Promise.reject(error);
  }
);

export default api;