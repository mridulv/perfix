import React, { useState } from "react";
import { Link, Outlet, useLocation } from "react-router-dom";
import { MdOutlineDashboard } from "react-icons/md";
import { FaUserAlt } from "react-icons/fa";
import { IoReturnUpBack } from "react-icons/io5";
import { FaAngleLeft } from "react-icons/fa6";
import { IoMenu } from "react-icons/io5";

const DashboardLayout = () => {
  const menus = [
    {
      name: "Dashboard",
      link: "/",
      icon: <MdOutlineDashboard color="white" size={30} />,
    },

    {
      name: "DB Configuration",
      link: "/db-configuration",
      icon: <MdOutlineDashboard color="white" size={30} />,
    },
    {
      name: "Datasets",
      link: "/datasets",
      icon: <MdOutlineDashboard color="white" size={30} />,
    },
    {
      name: "Experiment",
      link: "/experiment",
      icon: <MdOutlineDashboard color="white" size={30} />,
    },
  ];
  const [menuOpen, setMenuOpen] = useState(true);
  const location = useLocation();

  const userProfile = {
    name: "John Doe",
    profilePic: <FaUserAlt />,
  };

  return (
    <div className="drawer lg:drawer-open">
      <input id="my-drawer-2" type="checkbox" className="drawer-toggle" />
      <div className="drawer-content fle">
        {/* Page content here */}
        
        <div className="flex items-center gap-3 md:hidden ps-4 py-3 bg-primary">
        <label
          htmlFor="my-drawer-2"
          className=" drawer-button lg:hidden"
          >
          <IoMenu color="white" size={25}></IoMenu>
        </label>
        <h3 className="text-2xl font-bold text-white">Dashboard</h3>
        </div>
          <Outlet></Outlet>
      </div>
      <div className="drawer-side h-screen">
        {/* <button
            className={`bg-blue-500 hidden md:block rounded-full px-2 py-2 cursor-pointer absolute top-7 -right-3 z-50  ${
              !menuOpen && "rotate-180"
            }`}
            onClick={() => setMenuOpen(!menuOpen)}
          >
            <FaAngleLeft color="white" />
          </button> */}
        <label
          htmlFor="my-drawer-2"
          aria-label="close sidebar"
          className="drawer-overlay"
        ></label>
        <div
          className={`menu p-4 ${
            menuOpen ? "w-72" : "w-20"
          } min-h-screen bg-gray-400 flex flex-col justify-between relative duration-300`}
        >
          {/* Sidebar content here */}

          <div className="mb-5">
            <h1
              className={`italic text-2xl font-bold text-white ${
                !menuOpen && "scale-0"
              }`}
            >
              Dashboard
            </h1>
            <div className="mt-4">
              {menus.map((menu) => (
                <Link
                  key={menu.name}
                  className={`font-bold hover:bg-purple-300 ${
                    location.pathname === menu.link ? "bg-purple-600" : ""
                  }  ${
                    menuOpen ? "w-52" : "w-12"
                  } px-3 py-3 rounded-lg flex gap-4 items-center`}
                  to={`${menu.link}`}
                >
                  {menu.icon}
                  <span
                    className={`${
                      !menuOpen && "hidden"
                    } origin-left duration-200`}
                  >
                    {" "}
                    {menu.name}
                  </span>
                </Link>
              ))}
            </div>
          </div>

          <div className="flex flex-col ps-2 mb-2 border-t border-gray-600 py-5">
            <div className="flex items-center mb-4">
              <span>{userProfile.profilePic}</span>
              <span
                className={`ml-3 text-white ${
                  !menuOpen && "hidden"
                } origin-left duration-200`}
              >
                {userProfile.name}
              </span>
            </div>
            <div className="mt-2">
              <button
                className={`bg-purple-500 text-white ${
                  menuOpen ? "px-5" : "px-2"
                } h-[40px] py-2 flex items-center gap-3 rounded-xl origin-left duration-500`}
              >
                <span className={`${!menuOpen && "hidden"} `}>
                  Back to Home
                </span>
                <IoReturnUpBack size={20} color="white" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardLayout;
