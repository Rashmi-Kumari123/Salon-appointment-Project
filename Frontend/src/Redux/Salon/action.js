import {
  CREATE_SALON_REQUEST,
  CREATE_SALON_SUCCESS,
  CREATE_SALON_FAILURE,
  UPDATE_SALON_REQUEST,
  UPDATE_SALON_SUCCESS,
  UPDATE_SALON_FAILURE,
  FETCH_SALONS_REQUEST,
  FETCH_SALONS_SUCCESS,
  FETCH_SALONS_FAILURE,
  FETCH_SALON_BY_ID_REQUEST,
  FETCH_SALON_BY_ID_SUCCESS,
  FETCH_SALON_BY_ID_FAILURE,
  FETCH_SALON_BY_OWNER_REQUEST,
  FETCH_SALON_BY_OWNER_SUCCESS,
  FETCH_SALON_BY_OWNER_FAILURE,
  SEARCH_SALONS_REQUEST,
  SEARCH_SALONS_SUCCESS,
  SEARCH_SALONS_FAILURE,
  DELETE_SALON_REQUEST,
  DELETE_SALON_SUCCESS,
  DELETE_SALON_FAILURE,
} from "./actionTypes";
import api from "../../config/api";
import { extractErrorMessage } from "../../util/errorHandler";

const API_BASE_URL = "/api/salons";

export const createSalon = (reqData) => async (dispatch) => {
  dispatch({ type: CREATE_SALON_REQUEST });
  try {
    const response = await api.post(`/auth/signup`, reqData.ownerDetails);

    console.log("response ", response.data);

    localStorage.setItem("jwt", response.data.token);

    // Token will be automatically added by interceptor from localStorage
    // But we need to set it first since we just got it from signup
    localStorage.setItem("jwt", response.data.token);
    const { data } = await api.post(API_BASE_URL, reqData.salonDetails);

    reqData.navigate("/salon-dashboard");

    console.log("salon created successfully", data);
    dispatch({ type: CREATE_SALON_SUCCESS, payload: data });
  } catch (error) {
    console.log("error creating salon", error);
    dispatch({ type: CREATE_SALON_FAILURE, payload: extractErrorMessage(error) });
  }
};

export const updateSalon = (salonId, salon) => async (dispatch) => {
  dispatch({ type: UPDATE_SALON_REQUEST });
  try {
    // JWT token automatically added by interceptor
    const { data } = await api.put(`${API_BASE_URL}/${salonId}`, salon);
    dispatch({ type: UPDATE_SALON_SUCCESS, payload: data });
  } catch (error) {
    dispatch({ type: UPDATE_SALON_FAILURE, payload: extractErrorMessage(error) });
  }
};

export const fetchSalons = () => async (dispatch) => {
  dispatch({ type: FETCH_SALONS_REQUEST });
  try {
    // JWT token automatically added by interceptor
    const { data } = await api.get(API_BASE_URL);
    console.log("all salons ", data);
    dispatch({ type: FETCH_SALONS_SUCCESS, payload: data });
  } catch (error) {
    console.log("error fetching salons", error);
    dispatch({ type: FETCH_SALONS_FAILURE, payload: extractErrorMessage(error) });
  }
};

export const fetchSalonById = (salonId) => async (dispatch) => {
  dispatch({ type: FETCH_SALON_BY_ID_REQUEST });
  try {
    // JWT token automatically added by interceptor
    const { data } = await api.get(`${API_BASE_URL}/${salonId}`);
    dispatch({ type: FETCH_SALON_BY_ID_SUCCESS, payload: data });
  } catch (error) {
    dispatch({ type: FETCH_SALON_BY_ID_FAILURE, payload: extractErrorMessage(error) });
  }
};

export const fetchSalonByOwner = (jwt) => async (dispatch) => {
  dispatch({ type: FETCH_SALON_BY_OWNER_REQUEST });
  try {
    // If jwt is passed, use it; otherwise interceptor will use localStorage token
    const { data } = await api.get(`${API_BASE_URL}/owner`, {
      headers: jwt ? { Authorization: `Bearer ${jwt}` } : {},
    });
    console.log("salon by owner - ", data);
    dispatch({ type: FETCH_SALON_BY_OWNER_SUCCESS, payload: data });
  } catch (error) {
    console.log("error fetching salon by owner - ", error);
    dispatch({ type: FETCH_SALON_BY_OWNER_FAILURE, payload: extractErrorMessage(error) });
  }
};

export const searchSalon =
  ({ jwt, city }) =>
  async (dispatch) => {
    dispatch({ type: SEARCH_SALONS_REQUEST });
    try {
      // If jwt is passed, use it; otherwise interceptor will use localStorage token
      const { data } = await api.get(`${API_BASE_URL}/search`, {
        headers: jwt ? { Authorization: `Bearer ${jwt}` } : {},
        params: { city: city },
      });
      console.log("Search salon - ", data);
      dispatch({ type: SEARCH_SALONS_SUCCESS, payload: data });
    } catch (error) {
      console.log("error fetching salon by owner - ", error);
      dispatch({ type: SEARCH_SALONS_FAILURE, payload: extractErrorMessage(error) });
    }
  };

export const deleteSalon = (salonId, navigate) => async (dispatch) => {
  dispatch({ type: DELETE_SALON_REQUEST });
  try {
    // JWT token automatically added by interceptor
    await api.delete(`${API_BASE_URL}/${salonId}`);

    dispatch({ type: DELETE_SALON_SUCCESS, payload: salonId });

    // Navigate to salon list after deletion
    if (navigate) {
      navigate("/salons");
    }
  } catch (error) {
    console.log("error deleting salon", error);
    dispatch({
      type: DELETE_SALON_FAILURE,
      payload: extractErrorMessage(error),
    });
  }
};
