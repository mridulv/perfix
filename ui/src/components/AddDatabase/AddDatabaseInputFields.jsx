/* eslint-disable no-unused-vars */
import React, { useState } from "react";
import { FaPlus } from "react-icons/fa6";
import Select from "react-select";
import Loading from "../Common/Loading";
import StyledReactSelect from "../CustomSelect/StyledReactSelect";

const AddDatabaseInputFields = ({
  input,
  handleInputChange,
  options,
  isLoading,
}) => {
  const [selectOption, setSelectOption] = useState(null);
  const [selectedMultipleOptions, setSelectedMultipleOptions] = useState([]);
  const [columns, setColumns] = useState([{ partitionKey: "", sortKey: "" }]);
  const [showGSIFields, setShowGSIFields] = useState(false); // State to track if GSI fields are shown

  const { inputName, inputDisplayName, formInputType } = input;
  const { dataType } = formInputType;

  const handleChange = (e) => {
    const value =
      dataType === "IntType" ? Number(e.target.value) : e.target.value;
    handleInputChange(inputName, value);
  };

  const handleAddColumn = () => {
    setColumns([...columns, { partitionKey: "", sortKey: "" }]);
  };

  const handleSelectChange = (selectedOption) => {
    setSelectOption(selectedOption);
    handleInputChange(inputName, selectedOption.value);
  };

  const handleMultiSelectChange = (selectedOptions) => {
    setSelectedMultipleOptions(selectedOptions);
    const values = selectedOptions.map((option) => option.value);
    handleInputChange(inputName, values);
  };

  const handleColumnChange = (index, field, value) => {
    const updatedColumns = columns.map((column, i) =>
      i === index ? { ...column, [field]: value } : column
    );
    setColumns(updatedColumns);
    handleInputChange(inputName, updatedColumns);
  };

  const handleKeyDown = (e) => {
    const invalidChars = /[^a-zA-Z0-9]/;
    if (e.key === " " || invalidChars.test(e.key)) {
      e.preventDefault();
    }
  };

  if (isLoading) return <Loading />;

  return (
    <div className="flex flex-col mb-4">
      <label
        className={`${
          dataType === "GSIType" ? "hidden" : "block"
        } text-[12px] font-bold mb-[2px]`}
      >
        {inputDisplayName}
      </label>
      <div>
        {dataType === "StringType" && (
          <input
            type="text"
            className="search-input border-2 border-gray-300 focus:border-gray-400 w-[250px] px-2 py-1 rounded"
            name={inputName}
            onChange={handleChange}
            onKeyDown={handleKeyDown}
          />
        )}
        {dataType === "IntType" && (
          <input
            className="search-input border-2 border-gray-300 focus:border-gray-400 w-[250px] px-2 py-1 rounded"
            placeholder={inputDisplayName}
            type="number"
            name={inputName}
            onChange={handleChange}
          />
        )}
        {dataType === "SingleColumnSelectorType" && (
          <StyledReactSelect
            value={selectOption}
            onChange={handleSelectChange}
            options={options}
          />
        )}
        {dataType === "MultiColumnSelectorType" && (
          <Select
            value={selectedMultipleOptions}
            onChange={handleMultiSelectChange}
            options={options}
            isMulti
            styles={{
              container: (provided) => ({
                ...provided,
                width: "250px", // Set your desired width here
              }),
              control: (base) => ({
                ...base,
                fontSize: "13px",
              }),
              menu: (base) => ({
                ...base,
                fontSize: "13px",
              }),
            }}
          />
        )}
        {dataType === "GSIType" && (
          <div>
            {!showGSIFields ? (
              <button
                onClick={() => setShowGSIFields(true)}
                className="text-primary text-[12px] font-semibold flex items-center gap-3"
                type="button"
              >
                <FaPlus size={12} />
                Add GSI
              </button>
            ) : (
              <div>
                <label className={` text-[12px] font-bold mb-[2px]`}>
                  {inputDisplayName}
                </label>
                {columns.map((column, i) => (
                  <div
                    className="max-w-[300px] mt-2 mb-4 pt-5 pb-3 px-6 bg-accent rounded-lg"
                    key={i}
                  >
                    <div className="flex flex-col mb-4">
                      <div className="flex justify-between">
                        <label className="text-[12px] font-bold">
                          Partition Key
                        </label>
                      </div>
                      <StyledReactSelect
                        placeholder="Select"
                        onChange={(selectedOption) =>
                          handleColumnChange(
                            i,
                            "partitionKey",
                            selectedOption.value
                          )
                        }
                        options={options}
                        className="max-w-[250px]"
                      />
                    </div>
                    <div className="flex flex-col mb-3">
                      <label className="text-[12px] font-bold">Sort Key</label>
                      <StyledReactSelect
                        placeholder="Select"
                        onChange={(selectedOption) =>
                          handleColumnChange(i, "sortKey", selectedOption.value)
                        }
                        options={options}
                        className="max-w-[250px]"
                      />
                    </div>
                  </div>
                ))}
                <div className="flex mt-3">
                  <button
                    onClick={handleAddColumn}
                    className="text-primary text-[12px] font-semibold flex items-center gap-3"
                    type="button"
                  >
                    <FaPlus size={12} />
                    Add Column
                  </button>
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default AddDatabaseInputFields;
