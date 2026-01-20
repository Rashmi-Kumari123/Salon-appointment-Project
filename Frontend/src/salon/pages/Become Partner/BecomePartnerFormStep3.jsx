import React from "react";
import { Grid, TextField } from "@mui/material";
import { useFormik } from "formik";
import * as Yup from "yup";

// ✅ Validation schema with Yup
const validationSchema = Yup.object({
  salonAddress: Yup.object({
    phoneNumber: Yup.string()
      .matches(/^[0-9]{10}$/, "Mobile number must be 10 digits")
      .required("Mobile number is required"),
    pincode: Yup.string()
      .matches(/^[0-9]{6}$/, "Pincode must be 6 digits")
      .required("Pincode is required"),
    address: Yup.string().required("Address is required"),
    city: Yup.string().required("City is required"),
    email: Yup.string()
      .email("Enter a valid email")
      .required("Email is required"),
  }),
});

const BecomePartnerFormStep2 = () => {
  const formik = useFormik({
    initialValues: {
      salonAddress: {
        phoneNumber: "",
        pincode: "",
        address: "",
        city: "",
        email: "",
      },
    },
    validationSchema: validationSchema,
    onSubmit: (values) => {
      console.log("Form Submitted:", values);
    },
  });

  return (
    <form onSubmit={formik.handleSubmit}>
      <Grid container spacing={3}>
        <Grid item xs={6}>
          <TextField
            fullWidth
            name="salonAddress.phoneNumber"
            label="Mobile"
            value={formik.values.salonAddress.phoneNumber}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={
              formik.touched.salonAddress?.phoneNumber &&
              Boolean(formik.errors.salonAddress?.phoneNumber)
            }
            helperText={
              formik.touched.salonAddress?.phoneNumber &&
              formik.errors.salonAddress?.phoneNumber
            }
          />
        </Grid>

        <Grid item xs={6}>
          <TextField
            fullWidth
            name="salonAddress.pincode"
            label="Pincode"
            value={formik.values.salonAddress.pincode}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={
              formik.touched.salonAddress?.pincode &&
              Boolean(formik.errors.salonAddress?.pincode)
            }
            helperText={
              formik.touched.salonAddress?.pincode &&
              formik.errors.salonAddress?.pincode
            }
          />
        </Grid>

        <Grid item xs={12}>
          <TextField
            fullWidth
            name="salonAddress.address"
            label="Address (House No, Building, Street)"
            value={formik.values.salonAddress.address}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={
              formik.touched.salonAddress?.address &&
              Boolean(formik.errors.salonAddress?.address)
            }
            helperText={
              formik.touched.salonAddress?.address &&
              formik.errors.salonAddress?.address
            }
          />
        </Grid>

        <Grid item xs={12}>
          <TextField
            fullWidth
            name="salonAddress.city"
            label="City"
            value={formik.values.salonAddress.city}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={
              formik.touched.salonAddress?.city &&
              Boolean(formik.errors.salonAddress?.city)
            }
            helperText={
              formik.touched.salonAddress?.city &&
              formik.errors.salonAddress?.city
            }
          />
        </Grid>

        <Grid item xs={12}>
          <TextField
            fullWidth
            name="salonAddress.email"
            label="Email"
            value={formik.values.salonAddress.email}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={
              formik.touched.salonAddress?.email &&
              Boolean(formik.errors.salonAddress?.email)
            }
            helperText={
              formik.touched.salonAddress?.email &&
              formik.errors.salonAddress?.email
            }
          />
        </Grid>
      </Grid>

      <button
        type="submit"
        className="mt-4 px-6 py-2 bg-blue-500 text-white rounded-md"
      >
        Submit
      </button>
    </form>
  );
};

export default BecomePartnerFormStep2;
