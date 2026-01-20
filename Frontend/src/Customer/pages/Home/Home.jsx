import React, { useEffect } from "react";
import { services } from "../../../Data/Services";
import HomeServiceCard from "./HomeServiceCard";
import SalonList from "../Salon/SalonList";
import Banner from "./Banner";
import { useDispatch, useSelector } from "react-redux";
import { fetchSalons } from "../../../Redux/Salon/action";
import ContactForm from "./ContactForm";

const Home = () => {
  const { auth } = useSelector((store) => store);
  const { salon } = useSelector((store) => store);
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(fetchSalons());
  }, []);

  return (
    <div className="space-y-20 max-w-[1440px] mx-auto">
      {/* Banner Section */}
      <section>
        <Banner />
      </section>

      {/* Services + Images */}
      <section className="px-5 sm:px-10 lg:px-20 space-y-10 lg:space-y-0 lg:flex lg:flex-row flex-col-reverse gap-5 items-center box-border">
        {/* Services */}
        <div className="flex-1">
          <div className="overflow-hidden h-10 sm:h-12 relative mb-6 sm:mb-8">
            <h1 className="animate-marquee whitespace-nowrap text-2xl font-semibold text-center lg:text-left">
              What are you looking for, Bestie? 👀
            </h1>
          </div>

          <div className="flex flex-wrap justify-center lg:justify-start items-center gap-4 sm:gap-5">
            {services.map((item) => (
              <HomeServiceCard key={item.id} item={item} />
            ))}
          </div>
        </div>

        {/* Image Grid */}
        <div className="w-full lg:w-1/2 grid grid-cols-2 grid-rows-6 gap-3 min-h-[60vh] md:min-h-[90vh] box-border">
          <div className="row-span-3">
            <img
              className="w-full h-full object-cover rounded-md"
              src="https://promotion.lakmesalon.in/lf/hairday/ghd/model/2.png"
              alt="Hair Styling"
            />
          </div>
          <div className="row-span-2">
            <img
              className="w-full h-full object-cover rounded-md"
              src="https://content.jdmagicbox.com/quickquotes/images_main/beard-styling-600083166-q62816qk.jpg"
              alt=""
            />
          </div>
          <div className="row-span-3">
            <img
              className="w-full h-full object-cover rounded-md"
              src="https://content.jdmagicbox.com/quickquotes/images_main/hair-styling-600083178-2dug5d6o.jpg"
              alt=""
            />
          </div>
          <div className="row-span-2">
            <img
              className="w-full h-full object-cover rounded-md"
              src="https://content.jdmagicbox.com/quickquotes/images_main/eye-brow-threading-600083188-wz4u5tn8.jpg"
              alt="Eyebrow Threading"
            />
          </div>
        </div>

      </section>

   {/* Salon List - Only for CUSTOMER */}
      {auth?.user?.role === "CUSTOMER" && (
        <section className="px-5 sm:px-10 lg:px-20">
          <h1 className="text-3xl font-bold pb-10 text-center lg:text-left">
            👉 Book Your Favourite Salon
          </h1>
          <SalonList salons={salon.salons} />
        </section>
      )}

      <section className="Get In Touch">
        <ContactForm/>
      </section>
    </div>
  );
};

export default Home;
