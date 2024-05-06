import React from 'react';
import { Link } from 'react-router-dom';
import { FaPlus } from "react-icons/fa";

const SideBarAddButton = ({location}) => {
    return (
        <div>
            {location.pathname !== "/" &&
                location.pathname === "/experiment" && (
                  <Link to='/experiment' className="btn bg-[#fec2dd] pe-8 h-[65px] rounded-2xl flex gap-4 hover:bg-[#f9b9cd]">
                    <FaPlus color="#f02f87" size={20}/>
                    <span className="text-[16px]">Experiment</span>
                  </Link>
            )}
            {location.pathname !== "/" &&
                location.pathname === "/datasets" && (
                  <Link to='/datasets' className="btn bg-[#fec2dd] pe-8 h-[65px] rounded-2xl flex gap-4 hover:bg-[#f9b9cd]">
                    <FaPlus color="#f02f87" size={20}/>
                    <span className="text-[16px]">Datasets</span>
                  </Link>
            )}
            {location.pathname !== "/" &&
                location.pathname === "/db-configuration" && (
                  <Link to='/db-configuration' className="btn bg-[#fec2dd] pe-4 h-[65px] rounded-2xl flex gap-3 hover:bg-[#f9b9cd]">
                    <FaPlus color="#f02f87" size={20}/>
                    <span className="text-[16px]">DB Configuration</span>
                  </Link>
            )}
        </div>
    );
};

export default SideBarAddButton;