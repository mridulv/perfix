import { Link, useLocation } from "react-router-dom";
import SideBarAddButton from "../../components/Common/SideBarAddButton";
import { FiLogOut } from "react-icons/fi";
import { handleLogout, menus } from "../../utils/dashboardUtils";


const Sidebar = ({ user, userProfile }) => {
  const location = useLocation();

  return (
    <>
    <div className="">
            <h1 className={`mt-6 ms-4 font-medium text-[24px]`}>PerFix</h1>

            <div className="mt-8 ms-4 flex">
              <SideBarAddButton value="Experiment" url="/add-experiment" />
            </div>
            <div className="mt-5 ms-1">
              {menus.map((menu) => (
                <Link
                  //bg-[#fdd3db] [#A3D4FF]
                  key={menu.name}
                  className={` px-3 py-2  mt-1 flex gap-4 items-center ${
                    location.pathname === menu.link && " bg-[#BDE0FF]"
                  } hover:bg-[#BDE0FF] font-semibold rounded-3xl `}
                  to={`${menu.link}`}
                >
                  <img className="w-5" src={menu.icon} alt="" />
                  <span> {menu.name}</span>
                </Link>
              ))}
            </div>
          </div>

          <div className="flex flex-col  mb-2 border-t border-gray-600 py-5">
            <div className="flex items-center mb-4 ">
              <span className="p-3 bg-secondary rounded-full">
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
    </>
  );
};

export default Sidebar;