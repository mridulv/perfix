import React, { useContext, useEffect } from "react";
import { AuthContext } from "../../contexts/AuthProvider";
import { useNavigate } from "react-router-dom";
import googleIcon from "../../assets/googleIcon.png";
import login from "../../assets/login.png";

const Authentication = () => {
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();

  useEffect(() => {
    if (user?.email) {
      navigate("/");
    }
  }, [user, navigate]);

  const handleUser = () => {
    window.location.href = `${process.env.REACT_APP_BASE_URL}/login`;
  };

  if (user && Object.keys(user).length > 0) {
    navigate("/");
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-r from-accent to-[#CFDEF3]">
      <div className="w-[90%] max-w-[900px] bg-white rounded-3xl p-[46px] flex gap-5 shadow-2xl">
        <div className="flex-1">
          <img
            className="w-full max-w-[350px] mx-auto"
            src={login}
            alt="Login Illustration"
          />
        </div>
        <div className="flex-1 flex flex-col justify-center">
          <div className="mb-8">
            <p className="text-[24px] font-semibold text-primary mb-1">
              PerFix
            </p>
            <h1 className="text-[32px] font-semibold mb-2">Sign In</h1>
            <p className="text-base">Using your Google account</p>
          </div>
          <div className="flex justify-start">
            <button
              onClick={handleUser}
              className="flex items-center justify-center gap-3 text-xl font-semibold bg-white hover:bg-gray-100 px-6 py-3 rounded-full border border-gray-300 shadow-sm transition-all"
              style={{ width: "100%", maxWidth: "300px" }}
            >
              <img src={googleIcon} alt="Google Icon" className="w-6 h-6" />
              Sign in with Google
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Authentication;
