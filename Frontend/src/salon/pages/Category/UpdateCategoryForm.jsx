import React, { useEffect, useState } from "react";
import { useFormik } from "formik";
import * as Yup from "yup";
import {
  TextField,
  Button,
  MenuItem,
  Select,
  InputLabel,
  FormControl,
  FormHelperText,
  CircularProgress,
  IconButton,
  Snackbar,
  Alert,
  Grid,
} from "@mui/material";
import "tailwindcss/tailwind.css";
import AddPhotoAlternateIcon from "@mui/icons-material/AddPhotoAlternate";
import CloseIcon from "@mui/icons-material/Close";

import { uploadToCloudinary } from "../../../util/uploadToCloudnary";
import { useDispatch, useSelector } from "react-redux";
import {
  getCategoryById,
  updateCategory,
} from "../../../Redux/Category/action";
import { useParams } from "react-router-dom";

const UpdateCategoryForm = ({ onClose }) => {
  const [uploadImage, setUploadingImage] = useState(false);
  const [snackbarOpen, setSnackbarOpen] = useState(false);

  const dispatch = useDispatch();
  const { id } = useParams();
  const { category } = useSelector((store) => store);

  const formik = useFormik({
    initialValues: {
      name: "",
      image: "",
    },
    onSubmit: (values) => {
      dispatch(updateCategory({ category: values, id }));
      setSnackbarOpen(true); // ✅ Show success toast
      onClose();
    },
  });

  const handleImageChange = async (event) => {
    const file = event.target.files[0];
    setUploadingImage(true);
    const image = await uploadToCloudinary(file);
    formik.setFieldValue("image", image);
    setUploadingImage(false);
  };

  const handleRemoveImage = () => {
    formik.setFieldValue("image", "");
  };

  const handleCloseSnackbar = (event, reason) => {
    if (reason === "clickaway") return;
    setSnackbarOpen(false);
  };

  useEffect(() => {
    dispatch(getCategoryById(id));
  }, [dispatch, id]);

  useEffect(() => {
    if (category.category) {
      formik.setFieldValue("name", category.category.name);
      formik.setFieldValue("image", category.category.image);
    }
  }, [category.category]);

  return (
    <div className="flex justify-center items-center">
      <form onSubmit={formik.handleSubmit} className="space-y-4 p-4 w-full">
        <Grid container spacing={2}>
          {/* Image upload */}
          <Grid item xs={12}>
            {formik.values.image ? (
              <div className="relative border w-24 h-24">
                <img
                  className="w-24 h-24 object-cover"
                  src={formik.values.image}
                  alt="Service"
                />
                <IconButton
                  onClick={handleRemoveImage}
                  size="small"
                  color="error"
                  sx={{
                    position: "absolute",
                    top: 0,
                    right: 0,
                    outline: "none",
                  }}
                >
                  <CloseIcon sx={{ fontSize: "1rem" }} />
                </IconButton>
              </div>
            ) : (
              <>
                <input
                  type="file"
                  accept="image/*"
                  id="fileInput"
                  style={{ display: "none" }}
                  onChange={handleImageChange}
                />
                <label className="relative" htmlFor="fileInput">
                  <span className="w-24 h-24 cursor-pointer flex items-center justify-center p-3 border rounded-md border-gray-400">
                    <AddPhotoAlternateIcon className="text-gray-700" />
                  </span>
                  {uploadImage && (
                    <div className="absolute inset-0 w-24 h-24 flex justify-center items-center">
                      <CircularProgress />
                    </div>
                  )}
                </label>
              </>
            )}
          </Grid>

          {/* Name field */}
          <Grid item xs={12}>
            <TextField
              fullWidth
              id="name"
              name="name"
              label="Category Name"
              value={formik.values.name}
              onChange={formik.handleChange}
              error={formik.touched.name && Boolean(formik.errors.name)}
              helperText={formik.touched.name && formik.errors.name}
              required
            />
          </Grid>

          {/* Submit button */}
          <Grid item xs={12}>
            <Button
              type="submit"
              variant="outlined"
              fullWidth
              sx={{ py: ".8rem" }}
            >
              Update
            </Button>
          </Grid>
        </Grid>
      </form>

      {/* ✅ Success Snackbar */}
      <Snackbar
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
        open={snackbarOpen}
        autoHideDuration={3000}
        onClose={handleCloseSnackbar}
      >
        <Alert
          onClose={handleCloseSnackbar}
          severity="success"
          variant="filled"
          sx={{ width: "100%" }}
        >
          Category updated successfully!
        </Alert>
      </Snackbar>
    </div>
  );
};

export default UpdateCategoryForm;
