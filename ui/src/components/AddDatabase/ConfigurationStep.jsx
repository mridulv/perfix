/* eslint-disable no-unused-vars */
import React from "react";
import AddDatabaseInputFields from "./AddDatabaseInputFields";

const ConfigurationStep = ({
  currentStep,
  inputFields,
  handleInputChange,
  datasetColumnsOptions,
  inputFieldsLoading,
  columns,
  setColumns,
  showGSIFields,
  setShowGSIFields,
  inputValues, // New prop for pre-filled values
}) => {
  console.log(inputFields);
  return (
    <div className="min-h-[350px]">
      {inputFields?.forms[currentStep - 2]?.inputs.map((input, index) => (
        <AddDatabaseInputFields
          key={index}
          input={input}
          handleInputChange={handleInputChange}
          options={datasetColumnsOptions}
          isLoading={inputFieldsLoading}
          columns={columns}
          setColumns={setColumns}
          showGSIFields={showGSIFields}
          setShowGSIFields={setShowGSIFields}
          value={inputValues[input.inputName]} // Pass the pre-filled value
        />
      ))}
    </div>
  );
};

export default ConfigurationStep;
