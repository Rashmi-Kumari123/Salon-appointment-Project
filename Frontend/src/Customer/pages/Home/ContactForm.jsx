import React from "react";
import "./ContactForm.css";
import { FaEnvelopeOpenText } from "react-icons/fa";

const ContactForm = () => {
  return (
    <div className="contact-container">
      <div className="contact-card">
        <div className="contact-header">
          <FaEnvelopeOpenText className="contact-icon" />
          <h2>Get In Touch</h2>
        </div>
        <form className="contact-form">
          <input
            type="text"
            placeholder="Name"
            name="name"
            required
          />
          <input
            type="email"
            placeholder="Email"
            name="email"
            required
          />
          <textarea
            placeholder="Message"
            name="message"
            rows="6"
          ></textarea>
          <button type="submit">Get In Touch</button>
        </form>
      </div>
    </div>
  );
};

export default ContactForm;
