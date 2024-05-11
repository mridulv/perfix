import React, { useState } from "react";
import { Link, Outlet, useLocation } from "react-router-dom";
import { FaUserAlt } from "react-icons/fa";
import { IoMenu } from "react-icons/io5";
import SideBarAddButton from "../components/SideBarAddButton";
// import icon1 from "../../src/assets/icon1.png"
import icon2 from "../../src/assets/icon2.png"
import icon3 from "../../src/assets/icon3.png"
import icon4 from "../../src/assets/icon4.png"

const menus = [
  {
    name: "Database",
    link: "/",
    icon: icon2,
  },
  {
    name: "Datasets",
    link: "/datasets",
    icon: icon2
  },
  {
    name: "Experiment",
    link: "/experiment",
    icon: icon3,
  },
];

const userProfile = {
  name: "John Doe",
  profilePic: <FaUserAlt />,
};

const DashboardLayout = () => {
  const [menuOpen, setMenuOpen] = useState(true);
  const location = useLocation();

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

            {
              (location.pathname === "/" || location.pathname === "/datasets" || location.pathname === "/experiment") ?
              <div className="mt-8 ms-4 flex">
              <SideBarAddButton value={`${location.pathname === "/" ? "Database" : location.pathname === "/datasets" ? "Datasets" : "Experiment"}`}
                url={`${location.pathname === "/" ? "/add-database" : location.pathname === "/datasets" ? "/add-dataset" : "/add-experiment-dataset"}`}/>
              </div>
              : (
                <div className="mt-8"></div>
              )
            
            }
            <div className="mt-5 ms-1">
              {menus.map((menu) => (
                <Link
                  key={menu.name}
                  className={`font-bold hover:bg-purple-300 mt-1 ${
                    location.pathname === menu.link ? "bg-[#fdd3db]" : ""
                  }  ${
                    menuOpen ? "w-56" : "w-12"
                  } px-5 py-2 rounded-3xl flex gap-4 items-center`}
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
            <div className="flex items-center mb-4 ms-8">
              <span>{userProfile.profilePic}</span>
              <span
                className={`ml-3 text-black ${
                  !menuOpen && "hidden"
                } origin-left duration-200`}
              >
                {userProfile.name}
              </span>
            </div>
            <div className="mt-2 ms-4">
              <button
                className={`pb-0 text-black ${
                  menuOpen ? "px-5" : "px-2"
                } h-[40px] py-2 flex items-center gap-3 rounded-xl origin-left duration-500`}
              >
                <img src={icon4} alt="" />
                <span
                  className={`${!menuOpen && "hidden"} text-base font-semibold`}
                >
                  Setting
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
