import React from "react";
import { Avatar, Grid, IconButton, Box, Rating } from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import { red } from "@mui/material/colors";
import { deleteReview } from "../../../../Redux/Review/action";
import { useDispatch, useSelector } from "react-redux";

const ReviewCard = ({item}) => {
  const { auth, user } = useSelector(store => store);
  const dispatch = useDispatch()
  const handleDeleteReview = () => {
    dispatch(deleteReview({ reviewId: item.id, jwt: localStorage.getItem("jwt") || "" }))
  };
  return (
    <div className="flex justify-between">
      <Grid container spacing={2} className="w-full">
        Avatar
        <Grid item xs={1}>
          <Box>
            <Avatar
              className="text-white"
              sx={{ width: 56, height: 56, bgcolor: "#9155FD" }}
            >
              {item.user.fullName[0]}
            </Avatar>
          </Box>
        </Grid>
        {/* Review Content */}
        <Grid item xs={10}>
          <div className="space-y-2">
            <div>
              <p className="font-semibold text-lg">{item.user.fullName}</p>
              <p className="opacity-70">2025-06-25 17:34:57.140056</p>
            </div>

            <Rating readOnly value={item.rating} name="half-rating" precision={0.5} />

            <p>{item.reviewText}</p>
          </div>
        </Grid>
        {/* Delete Button */}
        <Grid item xs={1}>
          {item.user?.id === auth.user?.id && 
          <IconButton onClick={handleDeleteReview}>
            <DeleteIcon sx={{ color: red[700] }} />
          </IconButton>}
        </Grid>
      </Grid>
    </div>
  );
};

export default ReviewCard;
