/* eslint-disable no-unused-vars */
import React from "react";
import { FaPlus } from "react-icons/fa6";
import Select from "react-select";



const AddDataset = ({ columns, setColumns, handleAddColumn, columnTypesOptions }) => {

  //preventing to add space
  const handleKeyDown = (e) => {
    if (e.key === ' ') {
      e.preventDefault();
    }
  };

  return (
    <>
      <div className="flex flex-col mb-6">
        <label className="text-[12px] font-bold">Enter Name</label>
        <input
          className="search-input border-2 border-gray-300 focus:border-gray-400 max-w-[250px] px-2 py-1 rounded"
          type="text"
          placeholder="Enter Name"
          name="datasetName"
          required
        />
      </div>
      <div className="flex flex-col mb-5">
        <label className="text-[12px] font-bold">Enter Description</label>
        <textarea
          className="w-[400px] px-2 py-1 text-[13px] border-2 border-gray-300 rounded resize-none outline-gray-500"
          type="text"
          placeholder="Enter Name"
          rows={3}
          name="description"
          required
        />
      </div>
      <div className="mb-8">
        <h3 className="text-base font-bold mb-4">Setup Columns</h3>
        <div className="max-w-[290px]     rounded-md">
          {columns.map((column, i) => (
            <div className="mb-4 pt-5 pb-3 px-6 bg-accent rounded-lg" key={i}>
              <div className="flex flex-col mb-4">
                <div className="flex justify-between">
                  <label className="text-[12px] font-bold">Column Name</label>
                </div>
                <input
                  className="search-input border-2 border-gray-300 focus:border-gray-400 max-w-[250px] px-2 py-1 rounded"
                  placeholder="Name"
                  type="text"
                  name={`columnName${i}`}
                  id={`columnName${i}`}
                  value={column.columnName}
                  onChange={(e) => {
                    const newColumns = [...columns];
                    newColumns[i].columnName = e.target.value;
                    setColumns(newColumns);
                  }}
                  onKeyDown={handleKeyDown}
                  required
                />
              </div>
              <div className="flex flex-col mb-3">
                <label className="text-[12px] font-bold">Column Type:</label>
                <Select
                  styles={{
                    container: (provided) => ({
                      ...provided,
                      width: "250px", // Set your desired width here
                    }),
                    control: (base) => ({
                      ...base,
                      fontSize: "14px",
                    }),
                    menu: (base) => ({
                      ...base,
                      fontSize: "14px",
                    }),
                  }}
                  options={columnTypesOptions}
                  value={columnTypesOptions.find(
                    (option) => option.value === column.columnType
                  )}
                  onChange={(selectedOption) => {
                    const newColumns = [...columns];
                    newColumns[i].columnType = selectedOption.value;
                    setColumns(newColumns);
                  }}
                />
              </div>
            </div>
          ))}
        </div>
        <div className="flex mt-3">
          <button
            onClick={handleAddColumn}
            className="text-primary  text-[12px] font-semibold flex items-center gap-3"
            type="button"
          >
            <FaPlus size={12} />
            Add Column
          </button>
        </div>
      </div>
    </>
  );
};

export default AddDataset;
