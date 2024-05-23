import { useState, useEffect } from "react";
import { FaCaretDown, FaCheck } from "react-icons/fa";

function CustomSelect({ selected, setSelected, options }) {
  const [isActive, setIsActive] = useState(false);
  

  const handleOpenOptions = () => {
    setIsActive(!isActive);
  };

  const handleClickOutside = (event) => {
    const dropdown = document.querySelector(".dropdown");
    if (dropdown && !dropdown.contains(event.target)) {
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
    <div className="dropdown">
      <div className="dropdown-btn" onClick={handleOpenOptions}>
        {selected} <FaCaretDown />
      </div>
      {isActive && (
        <div className="w-[250px] bg-[#fcf8f8] mt-2 shadow-lg absolute">
          {options.map((option, i) => (
            <div
              key={i}
              onClick={(e) => {
                setSelected(option);
                setIsActive(false);
              }}
              className="dropdown-item flex items-center justify-between  px-4 py-2 hover:bg-gray-200 cursor-pointer"
            >
              {option}
              {selected === option && <FaCheck size={12}/>}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default CustomSelect;