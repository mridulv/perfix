import React from "react";
import { Link } from "react-router-dom";
import { FaPlus } from "react-icons/fa";

const SideBarAddButton = ({ value, url }) => {
  
  
  return (
    <div>
      <Link
        to={url}
        className="btn bg-[#fec2dd] pe-8 h-[65px] rounded-2xl flex gap-4 hover:bg-[#f9b9cd]"
      >
        <FaPlus color="#f02f87" size={20} />
        <span className="text-[16px]">{value}</span>
      </Link>
    </div>
  );
};

export default SideBarAddButton;
