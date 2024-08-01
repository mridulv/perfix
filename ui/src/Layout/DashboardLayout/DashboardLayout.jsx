import { useContext } from "react";
import { Outlet } from "react-router-dom";
import { FaUserAlt } from "react-icons/fa";
import { IoMenu } from "react-icons/io5";
import { AuthContext } from "../../contexts/AuthProvider";
import Sidebar from "./SideBar";

const DashboardLayout = () => {
  const { user } = useContext(AuthContext);

  const userProfile = {
    name: user?.name,
    email: user?.email,
    profilePic: <FaUserAlt />,
  };

  return (
    <div className="drawer lg:drawer-open bg-accent">
      <input id="my-drawer-2" type="checkbox" className="drawer-toggle" />
      <div className="drawer-content lg:ml-[280px]">
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
      <div className="drawer-side">
        <label
          htmlFor="my-drawer-2"
          aria-label="close sidebar"
          className="drawer-overlay"
        ></label>
        <div
          className="menu p-4 w-[280px] h-screen bg-accent flex flex-col justify-between overflow-y-auto lg:fixed lg:left-0 lg:top-0"
        >
          {/* Sidebar content here */}
          <Sidebar user={user} userProfile={userProfile} />
        </div>
      </div>
    </div>
  );
};

export default DashboardLayout;