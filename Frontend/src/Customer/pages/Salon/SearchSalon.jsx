import React, { useEffect, useState } from "react";
import SalonList from "./SalonList";
import { useDispatch, useSelector } from "react-redux";
import { searchSalon } from "../../../Redux/Salon/action";
import { FiSearch } from "react-icons/fi";
import { esES } from "@mui/x-date-pickers/locales";

const SearchSalon = () => {
  const { salon } = useSelector(store=>store);
  const dispatch = useDispatch();

  const handleSearch = (e) => {
    console.log("handleSearch ", e.target.value);
    dispatch(
      searchSalon({ jwt: localStorage.getItem("jwt"), city: e.target.value })
    );
  };
  return (
    <div className="lg:px-20 px-5">
      <div className="flex flex-col justify-center items-center pb-10 text-white z-20 space-y-3 px-5">
        <div className="relative w-[15rem] md:w-[33rem]">
          <input
            onChange={handleSearch}
            className="cursor-pointer border bg-white rounded-md py-4 w-full outline-none text-black pl-10 pr-4"
            placeholder="search salon by city..."
          />
          <FiSearch className="absolute top-1/2 left-3 transform -translate-y-1/2 text-gray-500 text-lg" />
        </div>
      </div>
      <div>
        <SalonList salons={salon.searchSalons} />
      </div>
    </div>
  );
};

export default SearchSalon;
