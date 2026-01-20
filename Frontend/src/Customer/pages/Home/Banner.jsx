import React from "react";
import { useNavigate } from "react-router-dom";

const Banner = () => {
  const navigate = useNavigate();
  return (
    <div className="w-full relative h-[80vh]">
      <video
        className="w-full h-full object-cover "
        autoPlay
        autoFocus
        muted
        src="https://booksy-public.s3.amazonaws.com/horizontal_.webm"
      ></video>
      <div className="textPart absolute flex flex-col items-center justify-center inset-0 text-white z-20 space-y-3 px-5">
        <h1 className="text-5xl font-bold">
          Beauty isn’t a luxury — it’s self-care 💇‍♀️
        </h1>
        <p className="text-slate-400 text-2xl text-center font-semibold">
          Slay Every Day – Find Beauty & Self-Care Near You 💁‍♀️
        </p>
        <input
          readOnly
          onClick={() => navigate("/search")}
          className="cursor-pointer border-none bg-white rounded-md py-4 w-[15rem] md:w-[33rem] outline-none text-black px-5"
          placeholder="search salon by city..."
        />
      </div>
      <div className="z-10 absolute top-0 bottom-0 right-0 left-0 bg-black opacity-75"></div>
    </div>
  );
};

export default Banner;
