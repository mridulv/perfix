/* eslint-disable no-unused-vars */
import React, { useRef, useState, useEffect } from "react";
import toast from "react-hot-toast";
import useInputFields from "../../api/fetchInputFieldsForAddDatabase";
import DatabaseInfoStep from "./DatabaseInfoStep";
import ConfigurationStep from "./ConfigurationStep";
import NavigationStep from "./NavigationStep";
import StepCounter from "./StepCounter";
import useDatabaseCategories from "../../hooks/useDatabaseCategories";
import {
  validateGSIFields,
  validateStepInputs,
} from "../../utils/databaseUtils";
import { handleDatabase } from "../../api/handleDatabase";

const DatabaseForm = ({
  databases,
  dataset,
  cancelFunction,
  successFunction,
  creationFor,
  isUpdate = false,
  existingDatabase = null,
}) => {
  console.log(dataset);
  const [currentStep, setCurrentStep] = useState(1);
  const [databaseName, setDatabaseName] = useState(existingDatabase?.name || "");
  const [selectedCategory, setSelectedCategory] = useState(
    existingDatabase
      ? { value: existingDatabase.databaseCategory, label: existingDatabase.databaseCategory }
      : { value: "", label: "Select a Category" }
  );
  const [selectedDatabaseType, setSelectedDatabaseType] = useState(
    existingDatabase
      ? { value: existingDatabase.dataStore, label: existingDatabase.dataStore }
      : { value: "", label: "Select a Database Type" }
  );
  const [inputValues, setInputValues] = useState({});
  const [columns, setColumns] = useState([]);
  const [showGSIFields, setShowGSIFields] = useState(false);
  const delayTimer = useRef(null);

  const { databaseCategoriesOptions, databaseTypesOptions } =
    useDatabaseCategories(selectedCategory);
  const { data: inputFields, isLoading: inputFieldsLoading } = useInputFields(
    selectedDatabaseType.value
  );

  const totalSteps = inputFields?.forms?.length + 1 || 3;

  const datasetColumnsOptions = dataset?.columns.map((column) => ({
    value: column.columnName,
    label: column.columnName,
  }));

  useEffect(() => {
    if (isUpdate && existingDatabase) {
      setDatabaseName(existingDatabase.name);
      setSelectedCategory({ value: existingDatabase.databaseCategory, label: existingDatabase.databaseCategory });
      setSelectedDatabaseType({ value: existingDatabase.dataStore, label: existingDatabase.dataStore });
      
      const gsiParams = existingDatabase.databaseSetupParams.gsiParams;
      if (gsiParams && gsiParams.length > 0) {
        setColumns(gsiParams.map(gsi => ({
          partitionKey: gsi.partitionKey || "",
          sortKey: gsi.sortKey || ""
        })));
        setShowGSIFields(true);
      } else {
        setColumns([{ partitionKey: "", sortKey: "" }]);
        setShowGSIFields(false);
      }

      setInputValues({
        ...existingDatabase.databaseSetupParams,
        gsiParams: gsiParams || []
      });
    }
  }, [isUpdate, existingDatabase]);

  const handleNextStep = (e = null) => {
    if (e) {
      e.preventDefault();
    }

    if (
      !validateStepInputs(
        currentStep,
        databaseName,
        selectedCategory,
        selectedDatabaseType,
        inputFields,
        inputValues
      )
    ) {
      return;
    }

    const isDuplicateName = databases?.some(
      (database) => database.name === databaseName
    );
    if (isDuplicateName && !isUpdate) {
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
    setInputValues((prev) => {
      if (inputName === 'gsiParams') {
        setColumns(value.map(gsi => ({
          partitionKey: gsi.partitionKey || "",
          sortKey: gsi.sortKey || ""
        })));
        setShowGSIFields(value.length > 0);
      }
      return { ...prev, [inputName]: value };
    });

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

    if (
      !validateStepInputs(
        currentStep,
        databaseName,
        selectedCategory,
        selectedDatabaseType,
        inputFields,
        inputValues
      ) ||
      !validateGSIFields(showGSIFields, columns)
    ) {
      return;
    }

    const formData = {
      name: databaseName,
      dataStore: selectedDatabaseType.value,
      DatabaseCategory: selectedCategory.value,
      datasetDetails: { datasetId: { id: dataset.id.id } },
      databaseSetupParams: {
        ...inputValues,
        type: selectedDatabaseType.value,
      },
      databaseState: "NotStarted",
    };

    if (isUpdate) {
      console.log(formData);
      await handleDatabase.handleUpdateDatabase(existingDatabase.databaseConfigId.id, formData, successFunction);
    } else {
      await handleDatabase.handleAddDatabase(formData, creationFor, successFunction);
    }

    //reset after submit
    setDatabaseName("");
    setSelectedCategory({ value: "", label: "Select a Category" });
    setSelectedDatabaseType({ value: "", label: "Select a Database Type" });
    setInputValues({});
    setColumns([{ partitionKey: "", sortKey: "" }]);
    setShowGSIFields(false);
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
            isUpdate={isUpdate}
          />
        ) : (
          <ConfigurationStep
            currentStep={currentStep}
            inputFields={inputFields}
            handleInputChange={handleInputChange}
            datasetColumnsOptions={datasetColumnsOptions}
            inputFieldsLoading={inputFieldsLoading}
            columns={columns}
            setColumns={setColumns}
            showGSIFields={showGSIFields}
            setShowGSIFields={setShowGSIFields}
            inputValues={inputValues}
          />
        )}
        <StepCounter currentStep={currentStep} totalSteps={totalSteps} />
        <NavigationStep
          handleCancel={cancelFunction}
          handleNextStep={handleNextStep}
          inputFields={inputFields}
          currentStep={currentStep}
          isUpdate={isUpdate}
        />
      </form>
    </div>
  );
};

export default DatabaseForm;