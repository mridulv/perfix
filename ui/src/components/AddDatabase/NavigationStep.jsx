/* eslint-disable no-unused-vars */
import React from "react";

const NavigationStep = ({
  currentStep,
  handleNextStep,
  handleCancel,
  inputFields
}) => {
  return (
    
    <div className="flex justify-end gap-3 mt-4 me-4">
    <button
      type="button"
      onClick={handleCancel}
      className="px-3 py-1 text-[14px] font-semibold border-2 border-gray-300 rounded"
    >
      Cancel
    </button>
    {currentStep === inputFields?.forms?.length + 1 ? (
      <button
        type="submit"
        className="btn bg-primary btn-sm border border-primary rounded text-white hover:bg-[#57B1FF]"
      >
        Submit
      </button>
    ) : (
      <button
        type="button"
        onClick={handleNextStep}
        className="btn bg-primary btn-sm border border-primary rounded text-white hover:bg-[#57B1FF]"
      >
        Next
      </button>
    )}
  </div>
  );
};

export default NavigationStep;
