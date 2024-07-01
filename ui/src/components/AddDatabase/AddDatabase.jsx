import React, { useEffect, useState } from "react";
import toast from "react-hot-toast";
import AddDatabaseInputFields from "./AddDatabaseInputFields";
import NavigationStep from "./NavigationStep";
import fetchInputFields from "../../api/fetchInputFieldsForAddDatabase";
import handleAddDatabase from "../../api/handleAddDatabase";
import AddDatabaseFirsStep from "./AddDatabaseFirsStep";
import StepCounter from "./StepCounter";
import fetchDatabaseCategory from "../../api/fetchDatabaseCategory";

const AddDatabase = ({
  databases,
  dataset,
  cancelFunction,
  successFunction,
  creationFor,
}) => {
  const [currentStep, setCurrentStep] = useState(1);
  const [maxSteps, setMaxSteps] = useState(2);

  const [databaseName, setDatabaseName] = useState("");
  const [databaseCategories, setDatabaseCategories] = useState(null);
  const [selectedDatabaseCategory, setSelectedDatabaseCategory] = useState({
    option: "Choose Category",
    value: "Choose",
  });
  const [selectedDatabaseType, setSelectedDatabaseType] = useState({
    option: "Choose Database",
    value: "Choose",
  });
  const [inputFields, setInputFields] = useState([]);
  const [inputValues, setInputValues] = useState({});

  // fetch the database categories
  useEffect(() => {
    fetchDatabaseCategory(setDatabaseCategories);
  }, []);

  //categories options
  const databaseCategoriesOptions =
    databaseCategories &&
    Object.keys(databaseCategories).map((category) => ({
      option: category,
      value: category,
    }));

  // for showing the input fields
  const getCurrentStepForInputs = (currentStep) => {
    return currentStep - 2;
  };

  const handleCurrentStep = () => {
    if (
      currentStep === 1 &&
      (databaseName === "" ||
        selectedDatabaseType.value === "Choose" ||
        selectedDatabaseCategory.value === "Choose")
    ) {
      return toast.error("Please input all the values");
    }

    const isDuplicateName = databases?.some(
      (database) => database.name === databaseName
    );
    if (isDuplicateName) {
      return toast.error(
        `A Database exists with the same ${databaseName} name`
      );
    }

    if (currentStep > 1) {
      const currentStepForInputs = getCurrentStepForInputs(currentStep);
      if (
        Object.keys(inputValues).length !==
          inputFields[currentStepForInputs].inputs.length ||
        Object.values(inputValues).some((value) => value === "" || value === 0)
      ) {
        return toast.error("Please fill in all required fields");
      }
    }
    setCurrentStep(currentStep + 1);
  };

  //after the database type selected and in second step we will get the correct input fields
  useEffect(() => {
    if (currentStep === 2 && selectedDatabaseType.value !== "Choose") {
      fetchInputFields(selectedDatabaseType, setInputFields, setMaxSteps);
    }
  }, [selectedDatabaseType, currentStep]);

  //set the columns of the dataset in an array to show them in options
  const datasetColumnsOptions = dataset?.columns.map((column) => ({
    option: column.columnName,
    value: column.columnName,
  }));

  // when click cancel button
  const handleCancel = () => {
    cancelFunction();
  };

  //add database submit function
  const handleSubmit = async (e) => {
    e.preventDefault();

    const values = {
      name: databaseName && databaseName,
      dataStore: selectedDatabaseType.value,
      datasetDetails: { datasetId: { id: dataset.id.id } },
      databaseSetupParams: { ...inputValues, type: selectedDatabaseType.value },
      databaseState: "NotStarted",
    };

    const getTotalInputFields = () => {
      return inputFields?.reduce((total, inputSet) => {
        const inputsCount = Object.keys(inputSet.inputs).length;
        return total + inputsCount;
      }, 0);
    };
    const totalInputFields = getTotalInputFields(inputFields);

    if (
      maxSteps > 2 &&
      currentStep === maxSteps &&
      Object.keys(inputValues).length === totalInputFields
    ) {
      await handleAddDatabase(values, creationFor, successFunction);
    }

    //the next button working like the submit button even when i added the type
  };

  // setting the inputValues of the inputs of form
  const handleInputChange = (key, value) => {
    setInputValues((prevValues) => ({
      ...prevValues,
      [key]: value,
    }));
  };

  return (
    <div className="ps-7 mt-4 flex flex-col relative">
      <form className="" onSubmit={handleSubmit}>
        <div>
          {/* step-1 */}
          <div>
            {currentStep === 1 && (
              <AddDatabaseFirsStep
                setDatabaseName={setDatabaseName}
                databaseCategoriesOptions={databaseCategoriesOptions}
                selectedDatabaseType={selectedDatabaseType}
                setSelectedDatabaseType={setSelectedDatabaseType}
                selectedDatabaseCategory={selectedDatabaseCategory}
                setSelectedDatabaseCategory={setSelectedDatabaseCategory}
                databaseCategories={databaseCategories}
              />
            )}
          </div>

          {/* after step-1 we are showing the input fields dynamically */}
          <div>
            {inputFields.length > 0 && currentStep > 1
              ? inputFields?.map((inputSet, index) => (
                  <div key={index}>
                    {getCurrentStepForInputs(currentStep) === index && (
                      <div>
                        {inputSet.inputs &&
                          Object.entries(inputSet.inputs).map(
                            ([key, value]) => (
                              <AddDatabaseInputFields
                                key={key}
                                input={{ [key]: value }}
                                handleInputChange={handleInputChange}
                                options={datasetColumnsOptions}
                              />
                            )
                          )}
                      </div>
                    )}
                  </div>
                ))
              : null}
          </div>
        </div>
        <div className="mt-[180px]">
          <StepCounter currentStep={currentStep} />

          <div>
            <NavigationStep
              currentStep={currentStep}
              handleCurrentStep={handleCurrentStep}
              handleCancel={handleCancel}
              maxSteps={maxSteps}
            />
          </div>
        </div>
      </form>
    </div>
  );
};

export default AddDatabase;
