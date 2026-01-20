import React, { useState } from "react";
import { useFormik } from "formik";
import * as Yup from "yup";
import {
  TextField,
  Button,
  CircularProgress,
  IconButton,
  Snackbar,
  Alert,
  Grid,
} from "@mui/material";
import AddPhotoAlternateIcon from "@mui/icons-material/AddPhotoAlternate";
import CloseIcon from "@mui/icons-material/Close";
import "tailwindcss/tailwind.css";

import { uploadToCloudinary } from "../../../util/uploadToCloudnary";
import { useDispatch } from "react-redux";
import { createCategory } from "../../../Redux/Category/action";

// ✅ Validation schema
const validationSchema = Yup.object({
  name: Yup.string().required("Category name is required"),
  image: Yup.string().required("Image is required"),
});

const CategoryForm = () => {
  const [uploadImage, setUploadingImage] = useState(false);
  const [snackbar, setSnackbar] = useState({ open: false, message: "", type: "success" });

  const dispatch = useDispatch();

  const formik = useFormik({
    initialValues: {
      name: "",
      image: "",
    },
    validationSchema: validationSchema,
    onSubmit: async (values, { resetForm }) => {
      try {
        await dispatch(
          createCategory({
            category: values,
            jwt: localStorage.getItem("jwt"),
          })
        );
        setSnackbar({ open: true, message: "Category created successfully", type: "success" });
        resetForm();
      } catch (error) {
        setSnackbar({ open: true, message: "Failed to create category", type: "error" });
      }
    },
  });

  const handleImageChange = async (event) => {
    const file = event.target.files[0];
    if (!file) return;
    setUploadingImage(true);
    const image = await uploadToCloudinary(file);
    formik.setFieldValue("image", image);
    setUploadingImage(false);
  };

  const handleRemoveImage = () => {
    formik.setFieldValue("image", "");
  };

  const handleCloseSnackbar = () => {
    setSnackbar({ ...snackbar, open: false });
  };

  return (
    <div className="flex justify-center items-center">
      <form
        onSubmit={formik.handleSubmit}
        className="space-y-4 p-4 w-full lg:w-1/2"
      >
        <Grid container spacing={2}>
          <Grid item xs={12}>
            {formik.values.image ? (
              <div className="relative border w-24 h-24">
                <img
                  className="w-24 h-24 object-cover"
                  src={formik.values.image}
                  alt="Category"
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
                    <div className="absolute left-0 right-0 top-0 bottom-0 w-24 h-24 flex justify-center items-center">
                      <CircularProgress />
                    </div>
                  )}
                </label>
                {formik.touched.image && formik.errors.image && (
                  <p className="text-red-500 text-sm">{formik.errors.image}</p>
                )}
              </>
            )}
          </Grid>

          <Grid item xs={12}>
            <TextField
              fullWidth
              id="name"
              name="name"
              label="Category Name"
              value={formik.values.name}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              error={formik.touched.name && Boolean(formik.errors.name)}
              helperText={formik.touched.name && formik.errors.name}
              required
            />
          </Grid>

          <Grid item xs={12}>
            <Button type="submit" variant="outlined" fullWidth sx={{ py: ".8rem" }}>
              Create Category
            </Button>
          </Grid>
        </Grid>
      </form>

      {/* ✅ Snackbar for success/error */}
      <Snackbar
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={handleCloseSnackbar}
      >
        <Alert
          onClose={handleCloseSnackbar}
          severity={snackbar.type}
          variant="filled"
          sx={{ width: "100%" }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </div>
  );
};

export default CategoryForm;
