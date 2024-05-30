import React, { useContext, useEffect } from "react";
import { AuthContext } from "../../contexts/AuthProvider";
import { useNavigate } from "react-router-dom";
import googleIcon from "../../assets/googleIcon.png";
import login from "../../assets/login.png"

const Authentication = () => {
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();
  useEffect(() => {
      if(user?.email){
          navigate("/")
      }
  }, [user, navigate])

  const handleUser = () => {
    window.location.href = `${process.env.REACT_APP_BASE_URL}/login`;
  };

  if (user && Object.keys(user).length > 0) {
    navigate("/");
  }

  return (
    //bg-gradient-to-r from-[#FFF6E7] to-[#8AC8FF]
    <div className="min-h-screen flex flex-col items-center justify-center  bg-accent">
      <div className="w-[50%] bg-white rounded-3xl p-[46px] flex gap-5 shadow-lg">
        <div>
          <img className="w-[350px] " src={login} alt="" />
        </div>
        <div>
        <div>
          <p className="text-[24px] font-semibold text-primary mb-3">
            PerFix
          </p>
          <h1 className="text-[32px] font-semibold mb-4">Sign In</h1>
          <p className="text-base">Using your Google account</p>
        </div>
        <div className="mt-8">
          <button
            onClick={handleUser}
            className="w-full flex justify-center gap-3 text-xl font-semibold hover:bg-accent"
            style={{ padding: "13px 39px", border: "1px solid #C9C0C0", borderRadius: "64px" }}
          >
            <img src={googleIcon} alt="" />
            Sign in with Google
          </button>
        </div>
        </div>
      </div>
    </div>
  );
};

export default Authentication;
