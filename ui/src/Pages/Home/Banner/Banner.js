import React from "react";
import Navbar from "../../../components/Navbar";
import arrow from "../../../assets/button-arrow.svg"
import axios from "axios";

const Banner = () => {

  const handleAddEmail = async(e) => {
    e.preventDefault();
    const email = e.target.email.value;
    console.log(email);

    try{
      const res = await axios.post("http://localhost:5000/user-email", {email});
      console.log(res);
    }
    catch(err){
      console.log(err);
    }
    
  };
  return (
    <div className="  px-[30px] lg:px-[160px] z-50">
      <div>
        <div className="py-[50px] mx-auto flex items-center justify-center">
          <div className="px-[0px] lg:px-[30px] max-w-[695px] text-center relative">
            <div className="hidden lg:block absolute bottom-2 right-4">
              <img
                src={arrow}
                alt="arrow"
              />
            </div>
            <h3 
              className="text-[35px] lg:text-[60px] font-bold mb-4 leading-tight"
              style={{
                background:
                  "linear-gradient(87deg, rgb(255, 255, 255) 0%, rgb(138, 138, 138) 100%)",
                WebkitBackgroundClip: "text",
                WebkitTextFillColor: "transparent",
              }}
            >
              Join The Waitlist for FramerIt Today!
            </h3>

            <div className=" mb-8 w-">
              <p
                className="text-[14px] md:text-[17px] text-center leading-7"
                style={{ color: "rgb(186, 186, 186)" }}
              >
                Discover an Array of Incredible Framer Templates and Be Prepared
                for an Exciting Wave of New Resources on the Horizon. Sign up to
                Our Waitlist to be notified when we launch!
              </p>
            </div>
            <form onSubmit={handleAddEmail} className="block md:flex justify-center  gap-3 ">
              <input
                className="bg-transparent text-white focus:outline-none text-[16px] px-4 py-[25px] w-full md:w-[340px] h-10 rounded-lg"
                style={{
                  background: "rgba(89, 89, 89, 0.6)",
                  
                }}
                name="email"
                type="email"
                placeholder="email@example.com"
                required
                autoComplete="off"
              />
              <button className="w-full md:w-[142px] mt-3 md:mt-0 bg-white text-black px-4 py-3 md:py-1 rounded-lg text-[17px] font-semibold">
                Join Waitlist!
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Banner;
