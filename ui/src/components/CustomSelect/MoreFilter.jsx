/* eslint-disable no-unused-vars */
import  React, { useRef, useEffect, useState } from "react";
import { FaCaretDown, FaCaretUp } from "react-icons/fa6";
import { IoIosArrowForward } from "react-icons/io";
import { MdOutlineCheckBoxOutlineBlank } from "react-icons/md";
import { RiCheckboxFill } from "react-icons/ri";

const MoreDropdown = ({ onSelect, selectedOptions }) => {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const toggleDropdown = () => {
    setIsOpen(!isOpen);
  };

  const options = {
    States: ["Created", "InProgress", "Completed", "Failed"],
  };

  const isSelected = (category, item) => {
    return selectedOptions[category] === item;
  };

  return (
    <div ref={dropdownRef} className="relative">
      <button
        onClick={toggleDropdown}
        className="px-1 py-[8px] text-[#c0aeae] text-[13px] flex items-center gap-1 font-semibold border-2 border-[#E0E0E0] rounded"
      >
        More
        {isOpen ? <FaCaretUp/> : <FaCaretDown />}
      </button>
      {isOpen && (
        <div className="w-[100px] lg:w-[200px] absolute top-[80%] mt-2 bg-white border rounded shadow-lg z-10">
          {Object.entries(options).map(([category, items]) => (
            <div key={category} className="relative group">
              <div className="px-4 py-2 text-[10px] lg:text-[13px] flex justify-between items-center hover:bg-accent cursor-pointer">
                {category}{" "} <IoIosArrowForward/>
              </div>
              <div className="w-[100px] lg:w-[200px] absolute left-full top-0 hidden group-hover:block bg-white border rounded shadow-lg">
                {items.map((item) => (
                  <div
                    key={item}
                    className="px-2 lg:px-4 py-2 text-[10px] lg:text-[13px] hover:bg-gray-100 cursor-pointer flex items-center gap-2"
                    onClick={() => {
                      onSelect(category, item);
                      setIsOpen(false);
                    }}
                  >
                    <span className="mb-[2px]">
                      {isSelected(category, item) ? (
                        <RiCheckboxFill size={17} color="#3DA5FF" />
                      ) : (
                        <MdOutlineCheckBoxOutlineBlank size={17} color="#3DA5FF" />
                      )}
                    </span>
                    {item}
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default MoreDropdown;