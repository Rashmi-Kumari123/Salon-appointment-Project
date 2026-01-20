// actions/categoryActions.js
import {
  CREATE_CATEGORY_REQUEST,
  CREATE_CATEGORY_SUCCESS,
  CREATE_CATEGORY_FAILURE,
  GET_ALL_CATEGORIES_REQUEST,
  GET_ALL_CATEGORIES_SUCCESS,
  GET_ALL_CATEGORIES_FAILURE,
  GET_CATEGORIES_BY_SALON_REQUEST,
  GET_CATEGORIES_BY_SALON_SUCCESS,
  GET_CATEGORIES_BY_SALON_FAILURE,
  GET_CATEGORY_BY_ID_REQUEST,
  GET_CATEGORY_BY_ID_SUCCESS,
  GET_CATEGORY_BY_ID_FAILURE,
  DELETE_CATEGORY_REQUEST,
  DELETE_CATEGORY_SUCCESS,
  DELETE_CATEGORY_FAILURE,
  UPDATE_CATEGORY_REQUEST,
  UPDATE_CATEGORY_SUCCESS,
  UPDATE_CATEGORY_FAILURE,
} from "./actionTypes";
import api from "../../config/api";
import { extractErrorMessage } from "../../util/errorHandler";

const BASE_URL = "/api/categories";

// Create Category
export const createCategory =
  ({ category, jwt }) =>
  async (dispatch) => {
    dispatch({ type: CREATE_CATEGORY_REQUEST });
    try {
      // If jwt is passed, use it; otherwise interceptor will use localStorage token
      const response = await api.post(`${BASE_URL}/salon-owner`, category, {
        headers: jwt ? { Authorization: `Bearer ${jwt}` } : {},
      });
      console.log("created category", response.data);
      dispatch({ type: CREATE_CATEGORY_SUCCESS, payload: response.data });
    } catch (error) {
      console.log("error creating category", error);
      dispatch({
        type: CREATE_CATEGORY_FAILURE,
        payload: extractErrorMessage(error),
      });
    }
  };

// Get All Categories
export const getAllCategories = () => async (dispatch) => {
  dispatch({ type: GET_ALL_CATEGORIES_REQUEST });
  try {
    // JWT token automatically added by interceptor if needed
    const response = await api.get(BASE_URL);
    dispatch({ type: GET_ALL_CATEGORIES_SUCCESS, payload: response.data });
  } catch (error) {
    dispatch({
      type: GET_ALL_CATEGORIES_FAILURE,
      payload: extractErrorMessage(error),
    });
  }
};

// Get Categories by Salon
export const getCategoriesBySalon = ({jwt,salonId}) => async (dispatch) => {
  dispatch({ type: GET_CATEGORIES_BY_SALON_REQUEST });
  try {
    // If jwt is passed, use it; otherwise interceptor will use localStorage token
    const response = await api.get(`${BASE_URL}/salon/${salonId}`, {
      headers: jwt ? { Authorization: `Bearer ${jwt}` } : {},
    });
    dispatch({ type: GET_CATEGORIES_BY_SALON_SUCCESS, payload: response.data });
    console.log("response + ", response.data);
  } catch (error) {
    console.log("error getting salon categories", error);
    dispatch({
      type: GET_CATEGORIES_BY_SALON_FAILURE,
      payload: extractErrorMessage(error),
    });
  }
};

// Get Category by ID
export const getCategoryById = (id) => async (dispatch) => {
  dispatch({ type: GET_CATEGORY_BY_ID_REQUEST });
  try {
    // JWT token automatically added by interceptor
    const response = await api.get(`${BASE_URL}/${id}`);
    dispatch({ type: GET_CATEGORY_BY_ID_SUCCESS, payload: response.data });
    console.log("response get category by id ", response.data);
  } catch (error) {
    dispatch({
      type: GET_CATEGORY_BY_ID_FAILURE,
      payload: extractErrorMessage(error),
    });
  }
};

export const updateCategory = ({id, category}) => async (dispatch) => {
  dispatch({ type: UPDATE_CATEGORY_REQUEST });

  try {
    // JWT token automatically added by interceptor
    const response = await api.patch(
      `/api/categories/${id}`,
      category
    );

    dispatch({
      type: UPDATE_CATEGORY_SUCCESS,
      payload: response.data, 
    });
  } catch (error) {
    dispatch({
      type: UPDATE_CATEGORY_FAILURE,
      payload: extractErrorMessage(error)
    });
  }
};

// Delete Category
export const deleteCategory = (id) => async (dispatch) => {
  dispatch({ type: DELETE_CATEGORY_REQUEST });
  try {
    // JWT token automatically added by interceptor
    await api.delete(`${BASE_URL}/${id}`);
    dispatch({ type: DELETE_CATEGORY_SUCCESS, payload: id });
  } catch (error) {
    dispatch({
      type: DELETE_CATEGORY_FAILURE,
      payload: extractErrorMessage(error),
    });
  }
};
