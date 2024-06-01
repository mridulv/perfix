import React, { useState, useEffect, useRef } from "react";
import { FaCaretDown, FaCaretUp } from "react-icons/fa";
import { MdOutlineCheckBoxOutlineBlank } from "react-icons/md";
import { RiCheckboxFill } from "react-icons/ri";

function CustomSelect({ selected, setSelected, options }) {
  const [isActive, setIsActive] = useState(false);
  const dropdownRef = useRef(null);

  const handleOpenOptions = () => {
    setIsActive(!isActive);
  };

  // const handleClickOutside = (event) => {
  //   const dropdown = document.querySelector(".dropdown");
  //   if (dropdown && !dropdown.contains(event.target)) {
  //     setIsActive(false);
  //   }
  // };

  const handleClickOutside = (event) => {
    if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
      setIsActive(false);
    }
  };

  useEffect(() => {
    document.addEventListener("click", handleClickOutside);
    return () => {
      document.removeEventListener("click", handleClickOutside);
    };
  }, []);

  return (
    <div className="dropdown" ref={dropdownRef}>
      <div className={`dropdown-btn  ${isActive ? "border-2 border-blue-400" : "border-2  border-[#E0E0E0]"}`} onClick={handleOpenOptions}>
        <span className="mr-4">{selected.option}</span> 
        <span className="mb-[2px]">{isActive ? <FaCaretUp/> : <FaCaretDown />}</span>
      </div>
      {isActive && (
        <div className="w-[180px] bg-white shadow-lg absolute">
          {options.map(({option, value}, i) => (
            <div
              key={i}
              onClick={(e) => {
                setSelected({option, value});
                setIsActive(false);
              }}
              className="dropdown-item flex items-center gap-2 px-4 py-2 hover:bg-gray-200 cursor-pointer"
            >
              <span className="mb-[2px]">
                {selected.option === option ? (
                  <RiCheckboxFill size={17} color="#3DA5FF" />
                ) : (
                  <MdOutlineCheckBoxOutlineBlank size={17}  color="#3DA5FF"/>
                )}
              </span>
              {option}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default CustomSelect;
