import React from "react";
import { useFormik } from "formik";
// import * as Yup from "yup";
import { TextField, Button, Box, Rating, InputLabel } from "@mui/material";
import { useDispatch, useSelector } from "react-redux";
import { useParams } from "react-router-dom";

import { createReview } from "../../../../Redux/Review/action";

const CreateReviewForm = () => {
  const dispatch = useDispatch();
  const { review } = useSelector((store) => store.review); 
  const { id } = useParams();

  const formik = useFormik({
    initialValues: {
      reviewText: "",
      rating: 0,
    },
    onSubmit: (values) => {
      if (id) {
        dispatch(createReview({ salonId: id,
          jwt: localStorage.getItem("jwt"),
          reviewData : values
         }));
      }
      console.log("Submitting", values);
    },
  });

  return (
    <Box
      component="form"
      onSubmit={formik.handleSubmit}
      sx={{ mt: 3 }}
      className="space-y-5 w-full lg:w-1/2"
    >
      <TextField
        fullWidth
        id="reviewText"
        name="reviewText"
        label="Review Text"
        variant="outlined"
        multiline
        rows={4}
        value={formik.values.reviewText}
        onChange={formik.handleChange}
        onBlur={formik.handleBlur}
        // error={formik.touched.reviewText && Boolean(formik.errors.reviewText)}
        // helperText={formik.touched.reviewText && formik.errors.reviewText}
      />

      <div className="space-y-2">
        <InputLabel>Rating</InputLabel>
        <Rating
          id="rating"
          name="rating"
          value={formik.values.rating}
          onChange={(event, newValue) =>
            formik.setFieldValue("rating", newValue)
          }
          onBlur={formik.handleBlur}
          precision={0.5}
        />
      </div>
      {/* {formik.touched.reviewRating && formik.errors.reviewRating && (
        <Typography color="error" variant="body2">
          {formik.errors.reviewRating}
        </Typography>
      )} */}

      <Button color="primary" variant="contained" type="submit">
        Submit Review
      </Button>
    </Box>
  );
};

export default CreateReviewForm;
