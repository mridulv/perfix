/* eslint-disable no-unused-vars */
import React from "react";
import { Link } from "react-router-dom";
import { FaPlus } from "react-icons/fa";

const SideBarAddButton = ({ value, url }) => {
  
  
  return (
    <div>
      <Link
        to={url}
        className="btn bg-[#A3D4FF] pe-8 h-[65px] rounded-2xl flex gap-4 hover:bg-[#57B1FF]"
      >
        <FaPlus color="#0A8DFF" size={20} />
        <span className="text-[16px]">{value}</span>
      </Link>
    </div>
  );
};

export default SideBarAddButton;
