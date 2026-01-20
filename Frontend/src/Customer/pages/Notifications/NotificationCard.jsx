import { Card } from "@mui/material";
import React from "react";
import NotificationsActiveIcon from "@mui/icons-material/NotificationsActive";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { markNotificationAsRead } from "../../../Redux/Notifications/action";

const NotificationCard = ({ item }) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleReadNotification = () => {
    dispatch(
      markNotificationAsRead({
        notificationId: item.id,
        jwt: localStorage.getItem("jwt"),
      })
    );
    navigate("/bookings");
  };

  return (
    <Card
      sx={{ bgcolor: item.isRead ? "white" : "#EAF0F1" }}
      className="cursor-pointer p-5 flex items-center gap-5"
      onClick={handleReadNotification}
    >
      <NotificationsActiveIcon />
      <div>
        <p>{item.description}</p>
        <h1 className="space-x-3">salon Id : {item.salonId}</h1>
      </div>
    </Card>
  );
};

export default NotificationCard;
