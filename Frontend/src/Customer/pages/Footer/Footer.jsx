import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { FaPhoneAlt, FaEnvelope, FaMapMarkerAlt, FaFacebookF, FaTwitter, FaLinkedinIn, FaGithub } from "react-icons/fa";
import "./Footer.css";
const Footer = () => {
  const { salon } = useSelector((store) => store);
  const { auth } = useSelector((store) => store);
 let salonName = "Rashmika Salon"; // default

  if (auth.user?.role === "SALON_OWNER" && salon?.salon?.name) {
    salonName = salon.salon.name;
  } else if (auth.user?.role === "CUSTOMER") {
    salonName = "Saloon Appointment Booking System";
  }
  return (
    <footer className="footer">
      <div className="footer-container">

        {/* About Us */}
        <div className="footer-about">
          <h3 className="footer-title">About Us</h3>
          <p className="footer-text">
            Welcome to {salonName}, your one-stop destination for premium
            salon services. Book appointments with ease and experience luxury at
            your fingertips.
          </p>
          <div className="footer-social">
            <a href="/"><FaFacebookF /></a>
            <a href="/"><FaTwitter /></a>
            <a href="/"><FaLinkedinIn /></a>
            <a href="/"><FaGithub /></a>
          </div>
        </div>

        {/* Quick Links */}
        <div className="footer-links">
          <h3 className="footer-title">Quick Links</h3>
          <ul class="space-y-2 text-sm">
            <li>
              <a href="/" class="hover:text-gray-400">
                Home
              </a>
            </li>
            <li>
              <a href="/" class="hover:text-gray-400">
                Services
              </a>
            </li>
            <li>
              <a href="/" class="hover:text-gray-400">
                Book Appointment
              </a>
            </li>
            <li>
              <a href="/" class="hover:text-gray-400">
                About Us
              </a>
            </li>
            <li>
              <a href="/" class="hover:text-gray-400">
                Contact
              </a>
            </li>
          </ul>
        </div>

        <div>
          <h3 class="text-xl font-semibold mb-4">Contact Us</h3>
          <ul className="footer-contact">
            <li><FaPhoneAlt /> +1 234 567 890</li>
            <li><FaEnvelope /> support@rashmikasalon.com</li>
            <li><FaMapMarkerAlt /> 123 Salon Street, City, Country</li>
          </ul>
          
        </div>
      </div>

      <p className="footer-bottom">
          &copy; 2025 [Your Salon Name]. All Rights Reserved. | Privacy | Terms
      </p>
        
      
    </footer>
  );
};

export default Footer;
