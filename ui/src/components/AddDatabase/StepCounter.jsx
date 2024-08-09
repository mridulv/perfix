/* eslint-disable no-unused-vars */
import React from "react";

const StepCounter = ({ currentStep, totalSteps }) => {
  return (
    <div className="mb-3 flex justify-center gap-3">
      {[...Array(totalSteps)].map((_, index) => (
        <div
          key={index}
          className={`bg-primary w-[10px] h-[10px] rounded-full ${
            currentStep === index + 1 ? "opacity-100" : "opacity-30"
          }`}
        ></div>
      ))}
    </div>
  );
};

export default StepCounter;