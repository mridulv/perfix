/* eslint-disable no-unused-vars */
import React from "react";
import AddDatabaseInputFields from "./AddDatabaseInputFields";

const ConfigurationStep = ({
  currentStep,
  inputFields,
  handleInputChange,
  datasetColumnsOptions,
  inputFieldsLoading,
}) => (
  <div className="min-h-[350px]">
    {inputFields?.forms[currentStep - 2]?.inputs.map((input, index) => (
      <AddDatabaseInputFields
        key={index}
        input={input}
        handleInputChange={handleInputChange}
        options={datasetColumnsOptions}
        isLoading={inputFieldsLoading}
      />
    ))}
  </div>
);

export default ConfigurationStep;