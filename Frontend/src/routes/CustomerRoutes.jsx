import React from "react";
import Home from "../Customer/pages/Home/Home";
import Notification from "../Customer/pages/Notifications/Notification";
import Bookings from "../Customer/pages/Bookings/Bookings";
import SalonDetails from "../Customer/pages/Salon/SalonDetails/SalonDetails";
import { Route, Routes } from "react-router-dom";
import Navbar from "../Customer/pages/Navbar/Navbar";
import NotFound from "../Customer/pages/NotFound/NotFound";
import PaymentSuccessHandler from "../Customer/pages/Payment/PaymentSuccessHandler";

const CustomerRoutes = () => {
  return (
    <div className='pb-20 min-h-[90vh] mt-[5rem]'>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/notification" element={<Notification />} />
        <Route path="/bookings" element={<Bookings />} />
        <Route path="/salon/:id" element={<SalonDetails />} />
        <Route path='/payment-success/:id' element={<PaymentSuccessHandler/>}/>
        <Route path="*" element={<NotFound />} />
      </Routes>
    </div>
  );
};

export default CustomerRoutes;
