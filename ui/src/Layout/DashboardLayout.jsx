import React, { useContext, useState } from "react";
import { Link, Outlet, useLocation } from "react-router-dom";
import { FaUserAlt } from "react-icons/fa";
import { IoMenu } from "react-icons/io5";
import SideBarAddButton from "../components/SideBarAddButton";
import icon2 from "../../src/assets/icon2.png";
import icon3 from "../../src/assets/icon3.png";
import { AuthContext } from "../contexts/AuthProvider";
import { FiLogOut } from "react-icons/fi";

const menus = [
  {
    name: "Datasets",
    link: "/",
    icon: icon2,
  },
  {
    name: "Database Configuration",
    link: "/database",
    icon: icon2,
  },
  {
    name: "Experiment",
    link: "/experiment",
    icon: icon3,
  },
];

const DashboardLayout = () => {
  const { user } = useContext(AuthContext);
  const [menuOpen, setMenuOpen] = useState(true);
  const location = useLocation();

  const userProfile = {
    name: user.name,
    email: user.email,
    profilePic: <FaUserAlt />,
  };

  return (
    <div className="drawer lg:drawer-open bg-[#fcf8f8]">
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
            menuOpen ? "w-[250px]" : "w-20"
          } min-h-screen bg-[#fcf8f8]  flex flex-col justify-between relative duration-300`}
        >
          {/* Sidebar content here */}

          <div className="">
            <h1
              className={`mt-6 ms-4 font-medium text-[24px] ${
                !menuOpen && "scale-0"
              }`}
            >
              PerFix
            </h1>

            {location.pathname === "/" ||
            location.pathname === "/database" ||
            location.pathname === "/experiment" ? (
              <div className="mt-8 ms-4 flex">
                <SideBarAddButton
                  value={`${
                    location.pathname === "/"
                      ? "Dataset"
                      : location.pathname === "/database"
                      ? "Database"
                      : "Experiment"
                  }`}
                  url={`${
                    location.pathname === "/"
                      ? "/add-dataset"
                      : location.pathname === "/database"
                      ? "/add-database"
                      : "/add-experiment-dataset"
                  }`}
                />
              </div>
            ) : (
              <div className="mt-8"></div>
            )}
            <div className="mt-5 ms-1">
              {menus.map((menu) => (
                <Link
                  key={menu.name}
                  className={`font-bold hover:bg-purple-300 mt-1 ${
                    location.pathname === menu.link ? "bg-[#fdd3db]" : ""
                  }  ${
                    menuOpen ? "w-56" : "w-12"
                  } px-3 py-2 rounded-3xl flex gap-4 items-center`}
                  to={`${menu.link}`}
                >
                  <img src={menu.icon} alt="" />
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
            <div className="flex items-center mb-4 ms-5">
              <span className="p-3 bg-gray-300 rounded-full">{userProfile.profilePic}</span>
              <div className="ml-3 flex flex-col">
                <span
                  className={` text-black ${
                    !menuOpen && "hidden"
                  } origin-left duration-200`}
                >
                  {userProfile.name}
                </span>
                <span className="text-black">{user.email}</span>
              </div>
            </div>
            <div className="mt-2 ms-4">
              <button
                className={`pb-0 text-black ${
                  menuOpen ? "px-5" : "px-2"
                } h-[40px] py-2 flex items-center gap-3 rounded-xl origin-left duration-500`}
              >
                <FiLogOut/>
                <span
                  className={`${!menuOpen && "hidden"} text-base font-semibold`}
                >
                  Logout
                </span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardLayout;
