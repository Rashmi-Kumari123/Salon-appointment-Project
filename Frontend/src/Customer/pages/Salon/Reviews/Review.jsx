import React, { useEffect } from "react";
import RatingCard from "./RatingCard";
import ReviewCard from "./ReviewCard";
import { Divider } from "@mui/material";
import { useDispatch, useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import { fetchReviews } from '../../../../Redux/Review/action';



const Review = () => {
  const dispatch = useDispatch();
  const { review } = useSelector((store) => store);
  const { id } = useParams();

  useEffect(() => {
    
    if (id) {
      
      dispatch(fetchReviews({ salonId: id, jwt: localStorage.getItem("jwt") }));
    }
  }, [id, dispatch]);
  return (
    <div className="pt-10 flex flex-col lg:flex-row gap-20">
      <section className="w-full md:w-1/2 lg:w-[40%] space-y-2">
        <h1 className="font-semibold text-lg pb-4">Review & Ratings</h1>

        <RatingCard />
      </section>
      <section className="w-full md:w-1/2 lg:w-[60%]">
        <div className="mt-10">
          <div className="space-y-5">
            {review.reviews.map((item) => (
              <div className="space-y-4">
                <ReviewCard item={item}/>
                <Divider />
              </div>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
};

export default Review;
