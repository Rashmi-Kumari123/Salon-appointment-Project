import React from "react";
import { Navigate } from "react-router-dom";
import { useSelector } from "react-redux";

const OwnerRoute = ({ children }) => {
  const { auth } = useSelector((store) => store);
  if (auth.user?.role !== "SALON_OWNER") {
    return <Navigate to="/" replace />;
  }
  return children;
};

export default OwnerRoute;
