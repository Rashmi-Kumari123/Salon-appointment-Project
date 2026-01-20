import React, { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import { addNotification } from "../Redux/Notifications/action";
import { useDispatch } from "react-redux";
import { Client } from "@stomp/stompjs";   // ✅ Correct import

const useNotificationWebsoket = (userId, type) => {
  const dispatch = useDispatch();
  const [stompClient, setStompClient] = useState(null);

  useEffect(() => {
    if (!userId) return; // Only connect if userId is available

    // ✅ Create STOMP client using @stomp/stompjs
    const client = new Client({
      webSocketFactory: () => new SockJS("http://localhost:5000/api/notifications/ws"),
      reconnectDelay: 5000, // Auto-reconnect every 5s
      onConnect: () => {
        console.log("Connected to WebSocket");

        client.subscribe(`/notification/${type}/${userId}`, (message) => {
          console.log("New message received", message);
          const receivedMessage = JSON.parse(message.body);
          dispatch(addNotification(receivedMessage));
        });
      },
      onStompError: (frame) => {
        console.error("STOMP error:", frame);
      },
      debug: (str) => console.log(str), // Optional: log STOMP activity
    });

    client.activate();
    setStompClient(client);

    return () => {
      if (client.active) {
        client.deactivate(() => console.log("Disconnected from WebSocket"));
      }
    };
  }, [userId, type, dispatch]);

  return stompClient;
};

export default useNotificationWebsoket;
