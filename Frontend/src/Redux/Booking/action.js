import {
  CREATE_BOOKING_REQUEST,
  CREATE_BOOKING_SUCCESS,
  CREATE_BOOKING_FAILURE,
  FETCH_CUSTOMER_BOOKINGS_REQUEST,
  FETCH_CUSTOMER_BOOKINGS_SUCCESS,
  FETCH_CUSTOMER_BOOKINGS_FAILURE,
  FETCH_SALON_BOOKINGS_REQUEST,
  FETCH_SALON_BOOKINGS_SUCCESS,
  FETCH_SALON_BOOKINGS_FAILURE,
  FETCH_BOOKING_BY_ID_REQUEST,
  FETCH_BOOKING_BY_ID_SUCCESS,
  FETCH_BOOKING_BY_ID_FAILURE,
  UPDATE_BOOKING_STATUS_REQUEST,
  UPDATE_BOOKING_STATUS_SUCCESS,
  UPDATE_BOOKING_STATUS_FAILURE,
  GET_SALON_REPORT_REQUEST,
  GET_SALON_REPORT_SUCCESS,
  GET_SALON_REPORT_FAILURE,
  FETCH_BOOKED_SLOTS_REQUEST,
  FETCH_BOOKED_SLOTS_SUCCESS,
  FETCH_BOOKED_SLOTS_FAILURE,
} from "./actionTypes";
import api from "../../config/api";
import { extractErrorMessage } from "../../util/errorHandler";

const API_BASE_URL = "/api/bookings";

export const createBooking = ({jwt, salonId, bookingData}) => async (dispatch) => {
  dispatch({ type: CREATE_BOOKING_REQUEST });
  try {
    // If jwt is passed, use it; otherwise interceptor will use localStorage token
    const { data } = await api.post(
      API_BASE_URL,
      bookingData,
      {
        headers: jwt ? { Authorization: `Bearer ${jwt}` } : {},
        params: { salonId, paymentMethod:"RAZORPAY" },
      }
    );

    // window.location.href=data.payment_link_url
    console.log(" create booking ", data)
    bookingData.navigate("/bookings");
    dispatch({ type: CREATE_BOOKING_SUCCESS, payload: data });
  } catch (error) {
    console.log("error creating booking ",error)
    dispatch({ type: CREATE_BOOKING_FAILURE, payload: extractErrorMessage(error) });
  }
};

export const fetchCustomerBookings = (jwt) => async (dispatch) => {
  dispatch({ type: FETCH_CUSTOMER_BOOKINGS_REQUEST });
  try {
    // If jwt is passed, use it; otherwise interceptor will use localStorage token
    const { data } = await api.get(`${API_BASE_URL}/customer`, {
      headers: jwt ? { Authorization: `Bearer ${jwt}` } : {},
    });
    console.log("customer bookings ",data)
    dispatch({ type: FETCH_CUSTOMER_BOOKINGS_SUCCESS, payload: data });
  } catch (error) {
    console.log("error ",error)
    dispatch({ type: FETCH_CUSTOMER_BOOKINGS_FAILURE, payload: extractErrorMessage(error) });
  }
};

export const fetchSalonBookings = ({jwt}) => async (dispatch) => {
  dispatch({ type: FETCH_SALON_BOOKINGS_REQUEST });
  try {
    // If jwt is passed, use it; otherwise interceptor will use localStorage token
    const { data } = await api.get(`${API_BASE_URL}/salon`, {
      headers: jwt ? { Authorization: `Bearer ${jwt}` } : {},
    });
    console.log("salon bookings ",data)
    dispatch({ type: FETCH_SALON_BOOKINGS_SUCCESS, payload: data });
  } catch (error) {
    console.log("error fetching salon bookings ",error)
    dispatch({ type: FETCH_SALON_BOOKINGS_FAILURE, payload: extractErrorMessage(error) });
  }
};


export const fetchBookingById = (bookingId) => async (dispatch) => {
  dispatch({ type: FETCH_BOOKING_BY_ID_REQUEST });
  try {
    // JWT token automatically added by interceptor
    const { data } = await api.get(`${API_BASE_URL}/${bookingId}`);
    dispatch({ type: FETCH_BOOKING_BY_ID_SUCCESS, payload: data });
  } catch (error) {
    dispatch({ type: FETCH_BOOKING_BY_ID_FAILURE, payload: extractErrorMessage(error) });
  }
};

export const updateBookingStatus = ({bookingId, status, jwt}) => async (dispatch) => {
  dispatch({ type: UPDATE_BOOKING_STATUS_REQUEST });
  try {
    // If jwt is passed, use it; otherwise interceptor will use localStorage token
    const { data } = await api.put(`${API_BASE_URL}/${bookingId}/status`, null, {
      headers: jwt ? { Authorization: `Bearer ${jwt}` } : {},
      params: { status },
    });
    console.log("update booking status ",data)
    dispatch({ type: UPDATE_BOOKING_STATUS_SUCCESS, payload: data });
  } catch (error) {
    console.log("error updating booking status ",error)
    dispatch({ type: UPDATE_BOOKING_STATUS_FAILURE, payload: extractErrorMessage(error) });
  }
};

export const getSalonReport = (jwt) => async (dispatch) => {
  try {
      dispatch({ type: GET_SALON_REPORT_REQUEST });
      
      // If jwt is passed, use it; otherwise interceptor will use localStorage token
      const response = await api.get('/api/bookings/report', {
          headers: jwt ? { 'Authorization': `Bearer ${jwt}` } : {},
      });

      dispatch({
          type: GET_SALON_REPORT_SUCCESS,
          payload: response.data, 
      });
      console.log("bookings report ",response.data)
  } catch (error) {
    console.log("error ",error)
      dispatch({
          type: GET_SALON_REPORT_FAILURE,
          payload: extractErrorMessage(error), 
      });
  }
};



export const fetchBookedSlotsRequest = () => ({
  type: FETCH_BOOKED_SLOTS_REQUEST,
});

export const fetchBookedSlotsSuccess = (slots) => ({
  type: FETCH_BOOKED_SLOTS_SUCCESS,
  payload: slots,
});

export const fetchBookedSlotsFailure = (error) => ({
  type: FETCH_BOOKED_SLOTS_FAILURE,
  payload: error,
});

// Thunk action to fetch booked slots
export const fetchBookedSlots = ({salonId, date, jwt}) => async (dispatch) => {
  dispatch(fetchBookedSlotsRequest());

  try {
    // If jwt is passed, use it; otherwise interceptor will use localStorage token
    const response = await api.get(
      `${API_BASE_URL}/slots/salon/${salonId}/date/${date}`,
      {
        headers: jwt ? { Authorization: `Bearer ${jwt}` } : {},
      }
    );
    console.log("fetch booked slots: ", response.data);
    dispatch(fetchBookedSlotsSuccess(response.data));
  } catch (error) {
    console.log("fetch booked slots error - : ",error);
    dispatch(fetchBookedSlotsFailure(extractErrorMessage(error)));
  }
};



