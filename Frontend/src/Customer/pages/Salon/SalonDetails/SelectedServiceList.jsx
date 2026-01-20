import React from "react";
import { Close } from "@mui/icons-material";
import { IconButton } from "@mui/material";

const SelectedServiceList = ({ services, handleRemoveService }) => {
  return (
    <div className="space-y-2 my-5">
      {services.map((item) => (
        <div className="py-2 px-4 rounded-md bg-slate-100 flex items-center justify-between">
          <h1 className="font-thin"> {item.name} </h1>
          <p>₹{item.price}</p>
          <IconButton onClick={() => handleRemoveService(item.id)}> 
            <Close />
          </IconButton>
        </div>
      ))}
    </div>
  );
};

export default SelectedServiceList;
