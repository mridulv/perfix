import React from 'react';

const StepCounter = ({currentStep}) => {
    return (
        <div className="mb-5 flex justify-center gap-3">
            <div
              className={`bg-primary w-[10px] h-[10px] rounded-full ${
                currentStep === 1 ? "opacity-100" : "opacity-30"
              }`}
            ></div>
            <div
              className={`bg-primary w-[10px] h-[10px] rounded-full ${
                currentStep === 2 ? "opacity-100" : "opacity-30"
              }`}
            ></div>
            <div
              className={`bg-primary w-[10px] h-[10px] rounded-full ${
                currentStep === 3 ? "opacity-100" : "opacity-30"
              }`}
            ></div>
          </div>
    );
};

export default StepCounter;