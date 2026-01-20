import {
  REGISTER_REQUEST,
  REGISTER_SUCCESS,
  REGISTER_FAILURE,
  LOGIN_REQUEST,
  LOGIN_SUCCESS,
  LOGIN_FAILURE,
  GET_USER_REQUEST,
  GET_USER_SUCCESS,
  GET_USER_FAILURE,
  LOGOUT,
  GET_ALL_CUSTOMERS_REQUEST,
  GET_ALL_CUSTOMERS_SUCCESS,
  GET_ALL_CUSTOMERS_FAILURE,
} from "./actionTypes";
import api from "../../config/api";
import { extractErrorMessage } from "../../util/errorHandler";

export const registerUser = (userData) => async (dispatch) => {
  dispatch({ type: REGISTER_REQUEST });
  console.log("auth action - ",userData)
  try {
    // Use api instance instead of axios - base URL and interceptors already configured
    const response = await api.post("/auth/signup", userData.userData);
    const user = response.data;
    if (user.token) {
      localStorage.setItem("jwt", user.token);
      userData.navigate("/");
    }
    console.log("registerr :- ", user);
    dispatch({ type: REGISTER_SUCCESS, payload: user });
  } catch (error) {
    console.log("error ", error);
    // Use standardized error handler
    const errorMessage = extractErrorMessage(error);
    dispatch({ type: REGISTER_FAILURE, payload: { ...error, message: errorMessage } });
  }
};

// Login action creators
const loginRequest = () => ({ type: LOGIN_REQUEST });
const loginSuccess = (user) => ({ type: LOGIN_SUCCESS, payload: user });

export const loginUser = (userData) => async (dispatch) => {
  dispatch(loginRequest());
  try {
    // Use api instance instead of axios - base URL and interceptors already configured
    const response = await api.post("/auth/login", userData.data);
    const user = response.data;
    if (user.token) {
      localStorage.setItem("jwt", user.token);
      
      // Get role from response - backend returns "CUSTOMER", "SALON_OWNER", "ADMIN"
      const userRole = user.user?.role || user.claims?.role || "";
      
      // Dispatch success with user data included
      dispatch(loginSuccess({ ...user, user: user.user }));
      
      // Navigate based on role (handle both "ROLE_*" and plain role names)
      if (userRole === "ROLE_ADMIN" || userRole === "ADMIN") {
        userData.navigate("/admin");
      } else if (userRole === "ROLE_SALON_OWNER" || userRole === "SALON_OWNER") {
        userData.navigate("/salon-dashboard");
      } else {
        // For CUSTOMER or any other role, navigate to home page
        userData.navigate("/");
      }
    }

    console.log("login ", user);
  } catch (error) {
    console.log("error ", error);
    // Use standardized error handler
    const errorMessage = extractErrorMessage(error);
    dispatch({ type: LOGIN_FAILURE, payload: { ...error, message: errorMessage } });
  }
};

//  get user from token
export const getAllCustomers = (token) => {
  return async (dispatch) => {
    console.log("jwt - ", token);
    dispatch({ type: GET_ALL_CUSTOMERS_REQUEST });
    try {
      // Use api instance - token will be added automatically by interceptor
      // But if token is passed as parameter, we can override it
      const response = await api.get("/api/admin/users", {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      const users = response.data;
      dispatch({ type: GET_ALL_CUSTOMERS_SUCCESS, payload: users });
      console.log("All Customers", users);
    } catch (error) {
      console.log(error);
      const errorMessage = extractErrorMessage(error);
      dispatch({ type: GET_ALL_CUSTOMERS_FAILURE, payload: errorMessage });
    }
  };
};

export const getUser = (token) => {
  return async (dispatch) => {
    dispatch({ type: GET_USER_REQUEST });
    try {
      // Use api instance - token will be added automatically by interceptor
      // But if token is passed as parameter, we can override it
      const response = await api.get("/api/users/profile", {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      const user = response.data;
      dispatch({ type: GET_USER_SUCCESS, payload: user });
      console.log("req User ", user);
    } catch (error) {
      const errorMessage = extractErrorMessage(error);
      dispatch({ type: GET_USER_FAILURE, payload: errorMessage });
    }
  };
};

export const logout = () => {
  return async (dispatch) => {
    dispatch({ type: LOGOUT });
    localStorage.clear();
  };
};
