import { useEffect, useRef, useState } from "react";
import { FaCaretDown, FaCaretUp } from "react-icons/fa6";
import { MdOutlineCheckBoxOutlineBlank } from "react-icons/md";
import { RiCheckboxFill } from "react-icons/ri";

function CustomSelectMultipleOptions({ selected, setSelected, options, width, name }) {
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
    const { option, value } = selectedOption;
    const isOptionSelected = selected.some((opt) => opt.option === option);

    if (isOptionSelected) {
      setSelected(selected.filter((opt) => opt.option !== option));
    } else {
      setSelected([...selected, { option, value }]);
    }
  };

  return (
    <div className="dropdown relative" ref={dropdownRef}>
      <div
        className={`dropdown-btn ${width} ${
          isActive ? "border-2 border-blue-400" : "border-2  border-[#E0E0E0]"
        }`}
        onClick={handleOpenOptions}
      >
        <span className="mr-4">
          {selected.length > 0
            ? `Selected (${selected.length})`
            : `${name}`}
        </span>
        <span className="mb-[2px]">
          {isActive ? <FaCaretUp /> : <FaCaretDown />}
        </span>
      </div>
      {isActive && (
        <div className="absolute top-full left-0 right-0 bg-white shadow-md z-30">
          {options.length === 0 ? (
            <div className="dropdown-item flex items-center gap-2 px-4 py-2 bg-gray-100 cursor-default">
              No options available
            </div>
          ) : (
            options.map(({ option, value }, i) => (
              <div
                key={i}
                onClick={() => handleOptionSelect({ option, value })}
                className="dropdown-item flex items-center gap-2 px-4 py-2 hover:bg-gray-200 cursor-pointer"
              >
                <span className="mb-[2px]">
                  {selected.some((selectedOption) => selectedOption.option === option) ? (
                    <RiCheckboxFill size={17} color="#3DA5FF" />
                  ) : (
                    <MdOutlineCheckBoxOutlineBlank size={17} color="#3DA5FF" />
                  )}
                </span>
                {option}
              </div>
            ))
          )}
        </div>
      )}
    </div>
  );
}

export default CustomSelectMultipleOptions;