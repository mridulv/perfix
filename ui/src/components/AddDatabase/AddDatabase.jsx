/* eslint-disable no-unused-vars */
import React, { useRef, useState } from "react";
import toast from "react-hot-toast";
import useInputFields from "../../api/fetchInputFieldsForAddDatabase";
import handleAddDatabase from "../../api/handleAddDatabase";
import DatabaseInfoStep from "./DatabaseInfoStep";
import ConfigurationStep from "./ConfigurationStep";
import NavigationStep from "./NavigationStep";
import StepCounter from "./StepCounter";
import useDatabaseCategories from "../../hooks/useDatabaseCategories";

const AddDatabase = ({
  databases,
  dataset,
  cancelFunction,
  successFunction,
  creationFor,
}) => {
  const [currentStep, setCurrentStep] = useState(1);
  const [databaseName, setDatabaseName] = useState("");
  const [selectedCategory, setSelectedCategory] = useState({
    value: "",
    label: "Select a Category",
  });
  const [selectedDatabaseType, setSelectedDatabaseType] = useState({
    value: "",
    label: "Select a Database Type",
  });
  const [inputValues, setInputValues] = useState({});
  const delayTimer = useRef(null);

  const {
    databaseCategoriesOptions,
    databaseTypesOptions,
  } = useDatabaseCategories(selectedCategory);
  const { data: inputFields, isLoading: inputFieldsLoading } = useInputFields(
    selectedDatabaseType.value
  );

  const totalSteps = inputFields?.forms?.length + 1 || 3;

  const datasetColumnsOptions = dataset?.columns.map((column) => ({
    value: column.columnName,
    label: column.columnName,
  }));

  const validateStepInputs = () => {
    if (currentStep === 1) {
      // Validation for step 1 (Initial inputs)
      if (
        !databaseName ||
        !selectedCategory.value ||
        !selectedDatabaseType.value
      ) {
        toast.error("Please fill in all required fields before proceeding.");
        return false;
      }
    } else if (currentStep > 1 && inputFields?.forms[currentStep - 2]?.inputs) {
      // Validation for subsequent steps
      const stepInputs = inputFields.forms[currentStep - 2].inputs;

      for (const input of stepInputs) {
        const inputValue = inputValues[input.inputName];

        // Skip validation if it's GSI and the user hasn't added it
        if (input.formInputType.dataType === "GSIType" && !inputValue) {
          continue;
        }

        if (
          input.formInputType.isRequired &&
          (!inputValue ||
            (Array.isArray(inputValue) && inputValue.length === 0))
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

  const handleNextStep = (e = null) => {
    if (e) {
      e.preventDefault();
    }

    if (!validateStepInputs()) {
      return;
    }

    const isDuplicateName = databases?.some(
      (database) => database.name === databaseName
    );
    if (isDuplicateName) {
      return toast.error(
        `A Database exists with the same ${databaseName} name`
      );
    }

    if (inputFields && currentStep < inputFields.forms.length + 1) {
      setCurrentStep(currentStep + 1);
    }
  };

  const handleInputChange = (inputName, value) => {
    clearTimeout(delayTimer.current);
    setInputValues((prev) => ({ ...prev, [inputName]: value }));

    delayTimer.current = setTimeout(() => {
      const stepInputs = inputFields?.forms[currentStep - 2]?.inputs || [];
      const allRequiredFieldsFilled = stepInputs.every((input) => {
        if (!input.formInputType.isRequired) return true;
        const inputValue = inputValues[input.inputName];
        return (
          inputValue && (!Array.isArray(inputValue) || inputValue.length > 0)
        );
      });

      if (allRequiredFieldsFilled) {
        handleNextStep();
      }
    }, 2000);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (currentStep !== inputFields?.forms?.length + 1) {
      return;
    }

    if (!validateStepInputs()) {
      return;
    }

    // Construct the formData object
    const formData = {
      name: databaseName,
      dataStore: selectedDatabaseType.value,
      datasetDetails: { datasetId: { id: dataset.id.id } },
      databaseSetupParams: {
        ...inputValues,
        type: selectedDatabaseType.value,
      },
      databaseState: "NotStarted",
    };

    // Submit the form data
    console.log("Form data submitted:", formData);
    await handleAddDatabase(formData, creationFor, successFunction);

    // Reset the form state
    setDatabaseName("");
    setSelectedCategory({ value: "", label: "Select a Category" });
    setSelectedDatabaseType({ value: "", label: "Select a Database Type" });
    setInputValues({});
    setCurrentStep(1);
  };

  return (
    <div className="ps-7 mt-4 flex flex-col relative">
      <form onSubmit={handleSubmit}>
        {currentStep === 1 ? (
          <DatabaseInfoStep
            databaseName={databaseName}
            setDatabaseName={setDatabaseName}
            selectedCategory={selectedCategory}
            setSelectedCategory={setSelectedCategory}
            selectedDatabaseType={selectedDatabaseType}
            setSelectedDatabaseType={setSelectedDatabaseType}
            databaseCategoriesOptions={databaseCategoriesOptions}
            databaseTypesOptions={databaseTypesOptions}
          />
        ) : (
          <ConfigurationStep
            currentStep={currentStep}
            inputFields={inputFields}
            handleInputChange={handleInputChange}
            datasetColumnsOptions={datasetColumnsOptions}
            inputFieldsLoading={inputFieldsLoading}
          />
        )}
        <StepCounter currentStep={currentStep} totalSteps={totalSteps} />
        <NavigationStep
          handleCancel={cancelFunction}
          handleNextStep={handleNextStep}
          inputFields={inputFields}
          currentStep={currentStep}
        />
      </form>
    </div>
  );
};

export default AddDatabase;
