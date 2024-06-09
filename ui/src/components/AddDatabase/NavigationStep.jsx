const NavigatinStep = ({
  currentStep,
  handleCurrentStep,
  handleCancel,
  maxSteps,
}) => {
  return (
    <div className="pe-4 flex justify-end gap-2">
      <button
        type="button"
        className="px-3 py-1 text-[14px] font-semibold border-2 border-gray-300 rounded"
        onClick={handleCancel}
      >
        Cancel
      </button>
      {currentStep === maxSteps ? (
        <button
          type="submit"
          className="btn bg-primary btn-sm border border-primary rounded text-white hover:bg-[#57B1FF]"
        >
          Submit
        </button>
      ) : (
        <button
          type="button"
          className="btn bg-primary btn-sm border border-primary rounded text-white hover:bg-[#57B1FF]"
          onClick={handleCurrentStep}
        >
          Next
        </button>
      )}
    </div>
  );
};

export default NavigatinStep;
