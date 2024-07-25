import { useEffect, useRef, useState } from "react";
import { FaCaretDown, FaCaretUp } from "react-icons/fa6";
import { MdOutlineCheckBoxOutlineBlank } from "react-icons/md";
import { RiCheckboxFill } from "react-icons/ri";

function CustomSelect({ selected, setSelected, options, width }) {
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
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const handleOptionSelect = (selectedOption) => {
    setSelected(selectedOption);
    setIsActive(false);
  };

  return (
    <div className="relative dropdown" ref={dropdownRef}>
      <div
        className={`dropdown-btn ${width} ${isActive ? "border-2 border-blue-400" : "border-2 border-[#E0E0E0]"} flex items-center justify-between`}
        onClick={handleOpenOptions}
      >
        <span className="truncate flex-grow mr-[2px]">{selected?.option}</span>
        <span className="flex-shrink-0">{isActive ? <FaCaretUp /> : <FaCaretDown />}</span>
      </div>
      {isActive && (
        <div className=" absolute top-full left-0 bg-white shadow-md z-30" style={{ minWidth: '100%' }}>
          {options.length === 0 ? (
            <div className="dropdown-item flex items-center gap-2 px-4 py-2 bg-gray-100 cursor-default">
              No options available
            </div>
          ) : (
            options.map(({ option, value }, i) => (
              <div
                key={i}
                onClick={() => handleOptionSelect({ option, value })}
                className="dropdown-item flex items-center gap-2 px-4 py-2 hover:bg-gray-200 cursor-pointer z-50 whitespace-nowrap"
              >
                <span className="flex-shrink-0">
                  {selected?.option === option ? (
                    <RiCheckboxFill size={17} color="#3DA5FF" />
                  ) : (
                    <MdOutlineCheckBoxOutlineBlank size={17} color="#3DA5FF" />
                  )}
                </span>
                <span>{option}</span>
              </div>
            ))
          )}
        </div>
      )}
    </div>
  );
}

export default CustomSelect;