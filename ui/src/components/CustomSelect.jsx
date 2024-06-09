import React, { useEffect, useRef, useState } from "react";
import { FaCaretDown, FaCaretUp } from "react-icons/fa6";
import { MdOutlineCheckBoxOutlineBlank } from "react-icons/md";
import { RiCheckboxFill } from "react-icons/ri";

function CustomSelect({ selected, setSelected, options, width}) {
  const [isActive, setIsActive] = useState(false);
  const dropdownRef = useRef(null);

  const handleOpenOptions = () => {
    setIsActive(!isActive);
  };

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

  const handleOptionSelect = (selectedOption) => {
    setSelected(selectedOption);
    setIsActive(false);
  };
  return (
    <div className="dropdown" ref={dropdownRef}>
      <div
        className={`dropdown-btn ${width} ${isActive ? "border-2 border-blue-400" : "border-2  border-[#E0E0E0]"}`}
        onClick={handleOpenOptions}
      >
        <span className="mr-4">{selected?.option}</span>
        <span className="mb-[2px]">{isActive ? <FaCaretUp /> : <FaCaretDown />}</span>
      </div>
      {isActive && (
        <div className="w-[200px] bg-white shadow-lg absolute">
          {options?.map(({ option, value }, i) => (
            <div
              key={i}
              onClick={() => handleOptionSelect({ option, value })}
              className="dropdown-item flex items-center gap-2 px-4 py-2 hover:bg-gray-200 cursor-pointer"
            >
              <span className="mb-[2px]">
                {selected?.option === option ? (
                  <RiCheckboxFill size={17} color="#3DA5FF" />
                ) : (
                  <MdOutlineCheckBoxOutlineBlank size={17} color="#3DA5FF" />
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