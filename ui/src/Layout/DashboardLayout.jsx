import React, { useContext } from "react";
import { Link, Outlet, useLocation } from "react-router-dom";
import { FaUserAlt } from "react-icons/fa";
import { IoMenu } from "react-icons/io5";
import SideBarAddButton from "../components/SideBarAddButton";
import { AiOutlineExperiment } from "react-icons/ai";
import { GoDatabase } from "react-icons/go";
import { AuthContext } from "../contexts/AuthProvider";
import { FiLogOut } from "react-icons/fi";
import axios from "axios";
import toast from "react-hot-toast";

const menus = [
  {
    name: "Datasets",
    link: "/",
    icon: <GoDatabase />,
  },
  {
    name: "Database Configuration",
    link: "/database",
    icon: <GoDatabase size={18} />,
  },
  {
    name: "Experiment",
    link: "/experiment",
    icon: <AiOutlineExperiment size={18} />,
  },
];

const DashboardLayout = () => {
  const { user, setUser } = useContext(AuthContext);
  const location = useLocation();

  const userProfile = {
    name: user?.name,
    email: user?.email,
    profilePic: <FaUserAlt />,
  };

  const handleLogout = async () => {
    const res = await axios.get(`${process.env.REACT_APP_BASE_URL}/logout`, {
      withCredentials: true,
    });
    console.log(res);
    if (res.status === 200) {
      toast.success("Logged out successfully!");
      setUser({});
    }
  };

  return (
    //bg-[#fcf8f8]
    <div className="drawer lg:drawer-open bg-accent">
      <input id="my-drawer-2" type="checkbox" className="drawer-toggle" />
      <div className="drawer-content ">
        {/* Page content here */}

        <div className="flex items-center gap-3 lg:hidden ps-4 py-3 bg-primary">
          <label htmlFor="my-drawer-2" className=" drawer-button lg:hidden">
            <IoMenu color="white" size={25}></IoMenu>
          </label>
          <h3 className="text-2xl text-white">PerFix</h3>
        </div>
        <div className="bg-white m-4 rounded-2xl min-h-screen">
          <Outlet></Outlet>
        </div>
      </div>
      <div className="drawer-side h-screen">
        <label
          htmlFor="my-drawer-2"
          aria-label="close sidebar"
          className="drawer-overlay"
        ></label>
        <div
          className={`menu p-4 w-[250px] min-h-screen bg-accent  flex flex-col justify-between relative duration-300`}
        >
          {/* Sidebar content here */}

          <div className="">
            <h1 className={`mt-6 ms-4 font-medium text-[24px]`}>PerFix</h1>

            <div className="mt-8 ms-4 flex">
              <SideBarAddButton
                value="Experiment"
                url="/add-experiment-dataset"
              />
            </div>
            <div className="mt-5 ms-1">
              {menus.map((menu) => (
                <Link
                  //bg-[#fdd3db]
                  key={menu.name}
                  className={`w-56 px-3 py-2  mt-1 flex gap-4 items-center ${
                    location.pathname === menu.link && " bg-[#A3D4FF]"
                  } hover:bg-[#57B1FF] font-bold rounded-3xl `}
                  to={`${menu.link}`}
                >
                  {menu.icon}
                  <span> {menu.name}</span>
                </Link>
              ))}
            </div>
          </div>

          <div className="flex flex-col  mb-2 border-t border-gray-600 py-5">
            <div className="flex items-center mb-4 ">
              <span className="p-3 bg-gray-300 rounded-full">
                {userProfile.profilePic}
              </span>
              <div className="ml-3 flex flex-col">
                <span className={`text-[12px] text-black`}>
                  {userProfile.name}
                </span>
                <span className="text-[12px] text-black">{user?.email}</span>
              </div>
            </div>
            <div className="mt-2 ms-4">
              <button
                onClick={handleLogout}
                className={`btn btn-sm h-[40px] bg-primary hover:bg-[#57B1FF] pb-0 px-5 flex items-center gap-3 
                 border border-primary rounded text-base font-semibold  text-white`}
              >
                <FiLogOut />
                Logout
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardLayout;
