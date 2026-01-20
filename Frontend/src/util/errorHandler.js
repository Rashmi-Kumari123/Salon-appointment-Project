/**
 * Utility function to extract error messages from API responses
 * Provides consistent error handling across the application
 * 
 * @param {Error} error - The error object from axios/API call
 * @returns {string} - A user-friendly error message
 */
export const extractErrorMessage = (error) => {
  // Priority 1: Check for message in response.data.message (most common)
  if (error.response?.data?.message) {
    return error.response.data.message;
  }
  
  // Priority 2: Check if response.data is a string
  if (error.response?.data && typeof error.response.data === 'string') {
    return error.response.data;
  }
  
  // Priority 3: Check if response.data exists (could be an object)
  if (error.response?.data) {
    // Try to stringify if it's an object
    try {
      return JSON.stringify(error.response.data);
    } catch (e) {
      return "An error occurred";
    }
  }
  
  // Priority 4: Use error.message (network errors, etc.)
  if (error.message) {
    return error.message;
  }
  
  // Fallback: Generic error message
  return "An error occurred. Please try again.";
};
