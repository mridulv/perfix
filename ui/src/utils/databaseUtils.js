import toast from "react-hot-toast";

export const validateStepInputs = (
  currentStep,
  databaseName,
  selectedCategory,
  selectedDatabaseType,
  inputFields,
  inputValues,
) => {
  if (currentStep === 1) {
    if (
      !databaseName ||
      !selectedCategory.value ||
      !selectedDatabaseType.value
    ) {
      toast.error("Please fill in all required fields before proceeding.");
      return false;
    }
  } else if (currentStep > 1 && inputFields?.forms[currentStep - 2]?.inputs) {
    const stepInputs = inputFields.forms[currentStep - 2].inputs;

    for (const input of stepInputs) {
      const inputValue = inputValues[input.inputName];

      if (
        input.formInputType.isRequired &&
        (!inputValue || (Array.isArray(inputValue) && inputValue.length === 0))
      ) {
        toast.error(
          `Please fill in the required field: ${input.inputDisplayName}`
        );
        return false;
      }
    }
  }
  return true;
};

export const validateGSIFields = (showGSIFields, columns) => {
  if (!showGSIFields) return true;

  for (const column of columns) {
    if (!column.partitionKey) {
      toast.error("Please fill in the required Partition Key for GSI.");
      return false;
    }
    if (!column.sortKey) {
      toast.error("Please fill in the required Sort Key for GSI.");
      return false;
    }
  }
  return true;
};



