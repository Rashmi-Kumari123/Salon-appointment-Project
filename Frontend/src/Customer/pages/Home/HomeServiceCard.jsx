
import React, { useState } from "react";
const HomeServiceCard = ({ item }) => {
  const [showPopup, setShowPopup] = useState(false);

  return (
    <div
      className="relative group cursor-pointer"
      onMouseEnter={() => setShowPopup(true)}
      onMouseLeave={() => setShowPopup(false)}
    >
      <div className="w-28 h-28 bg-blue-50 rounded-lg shadow-md flex flex-col items-center justify-center text-center p-2">
        <img src={item.image} alt={item.name} className="w-16 h-16 rounded-full object-cover mb-1" />
        <p className="text-sm font-medium">{item.name}</p>
      </div>

      {/* Popup Box */}
      {showPopup && (
        <div className="absolute top-full left-0 z-50 mt-2 w-72 bg-white p-4 rounded-md shadow-xl text-sm text-gray-700">
          <h4 className="font-semibold text-base mb-1">{item.name}</h4>
          <p className="text-xs mb-2">{item.description}</p>
          <ul className="list-disc list-inside text-xs">
            {item.points?.map((point, idx) => (
              <li key={idx}>{point}</li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};

export default HomeServiceCard;
