import toast from "react-hot-toast";

export const validateStepInputs = (
  currentStep,
  databaseName,
  selectedCategory,
  selectedDatabaseType,
  inputFields,
  inputValues,
  isGSIAdded, // Track if GSI fields have been added
  showToast = true
) => {
  if (currentStep === 1) {
    if (
      !databaseName ||
      !selectedCategory.value ||
      !selectedDatabaseType.value
    ) {
      if (showToast) {
        toast.error("Please fill in all required fields before proceeding.");
      }
      return false;
    }
  } else if (currentStep > 1 && inputFields?.forms[currentStep - 2]?.inputs) {
    const stepInputs = inputFields.forms[currentStep - 2].inputs;

    for (const input of stepInputs) {
      const inputValue = inputValues[input.inputName];

      if (input.formInputType.dataType === "GSIType" && isGSIAdded) {
        const gsiParams = inputValue || [];

        // Validate each GSI that has been added
        for (const gsi of gsiParams) {
          if (!gsi.partitionKey) {
            if (showToast) {
              toast.error("Please fill in the required Partition Key for GSI.");
            }
            return false;
          }
          if (!gsi.sortKey && input.formInputType.isRequired) {
            if (showToast) {
              toast.error("Please fill in the required Sort Key for GSI.");
            }
            return false;
          }
        }
        continue; // Skip further validation for this GSI field
      }

      // Validate other required fields
      if (
        input.formInputType.isRequired &&
        (!inputValue ||
          (Array.isArray(inputValue) && inputValue.length === 0))
      ) {
        if (showToast) {
          toast.error(
            `Please fill in the required field: ${input.inputDisplayName}`
          );
        }
        return false;
      }
    }
  }
  return true;
};
