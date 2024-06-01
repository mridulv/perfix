import React from "react";

const RangeInput = ({ value, setValue, startValue, lastValue, label }) => {
  const handleInput = (e) => {
    const newValue = e.target.value;
    setValue(newValue);
    const percentage =
      ((newValue - e.target.min) / (e.target.max - e.target.min)) * 100;
    e.target.style.background = `linear-gradient(to right, #3DA5FF ${percentage}%, #e2e8f0 ${percentage}%)`;
  };

  return (
    <div className="w-[60%]">
      <div className="flex flex-col gap-5 w-full">
        <label className="text-[12px] font-bold ">{label} <span>({value} mhz)</span></label>
        <input
          type="range"
          min="0"
          max="1500"
          value={value}
          className="range-input"
          onInput={handleInput}
        />
      </div>
      <div className="mt-3 flex justify-between">
        <span className="text-[11px] text-[#8E8E8E]">{startValue}</span>
        <span className="text-[11px] text-[#8E8E8E]">{lastValue}</span>
      </div>
    </div>
  );
};

export default RangeInput;
