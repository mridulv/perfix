import { useState } from "react";
import CustomSelectMultipleOptions from "../CustomSelectMultipleOptions";
import { FaPlus } from "react-icons/fa6";

const AddDatabaseInputFields = ({ input, handleInputChange, options }) => {
  const [selectedOptions, setSelectedOptions] = useState([]);
  //for gsi params
  const [columns, setColumns] = useState([{ partitionKey: "", sortKey: "" }]);

  // adding more colums for gsi
  const handleAddColumn = () => {
    setColumns([...columns, { partitionKey: "", sortKey: "" }]);
  };

  const key = Object.keys(input)[0];
  const { inputName, inputDisplayName, formInputType } = input[key];

  const { dataType } = formInputType;

  const handleChange = (e) => {
    const value =
      dataType === "IntType" ? Number(e.target.value) : e.target.value;
    handleInputChange(inputName, value);
  };

  const handleMultiSelectChange = (options) => {
    const values = options.map((option) => option.value);
    setSelectedOptions(options);
    handleInputChange(inputName, values);
  };

  
  const handleColumnChange = (index, field, value) => {
    const updatedColumns = columns.map((column, i) =>
      i === index ? { ...column, [field]: value } : column
    );
    setColumns(updatedColumns);
    handleInputChange(inputName, updatedColumns);
  };
  

  return (
    <div className="flex flex-col mb-4">
      <label className="text-[12px] font-bold mb-[2px]">
        {inputDisplayName}
      </label>
      <div>
        {dataType === "StringType" && (
          <input
            type="text"
            className="search-input border-2 border-gray-300 focus:border-gray-400 w-[250px] px-2 py-1 rounded"
            name={inputName}
            onChange={handleChange}
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
        {dataType === "MultiColumnSelectorType" && (
          <CustomSelectMultipleOptions
            selected={selectedOptions}
            setSelected={handleMultiSelectChange}
            options={options}
            width="w-[250px]"
          />
        )}
         {dataType === "GSIType" && (
          <div>
            {columns.map((column, i) => (
              <div className="max-w-[300px] mb-4 pt-5 pb-3 px-6 bg-accent rounded-lg" key={i}>
                <div className="flex flex-col mb-4">
                  <div className="flex justify-between">
                    <label className="text-[12px] font-bold">
                      Partition Key
                    </label>
                  </div>
                  <input
                    className="search-input border-2 border-gray-300 focus:border-gray-400 max-w-[250px] px-2 py-1 rounded"
                    type="text"
                    name={`partitionKey${i}`}
                    id={`partitionKey${i}`}
                    value={column.partitionKey}
                    onChange={(e) => handleColumnChange(i, 'partitionKey', e.target.value)}
                    required
                  />
                </div>
                <div className="flex flex-col mb-3">
                  <label className="text-[12px] font-bold">Sort Key</label>
                  <input
                    className="search-input border-2 border-gray-300 focus:border-gray-400 max-w-[250px] px-2 py-1 rounded"
                    type="text"
                    name={`sortKey${i}`}
                    id={`sortKey${i}`}
                    value={column.sortKey}
                    onChange={(e) => handleColumnChange(i, 'sortKey', e.target.value)}
                    required
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
    </div>
  );
};

export default AddDatabaseInputFields;
