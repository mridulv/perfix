import React from "react";
import { FaPlus } from "react-icons/fa6";

const AddDataset = ({ columns, handleAddColumn }) => {
  return (
    <>
      <div className="flex flex-col mb-6">
        <label className="text-[12px] font-bold">Name</label>
        <input
          className="search-input border-2 border-gray-300 focus:border-gray-400 max-w-[250px] px-2 py-1 rounded"
          type="text"
          placeholder="Enter Name"
          name="datasetName"
          required
        />
      </div>
      <div className="flex flex-col mb-5">
        <label className="text-[12px] font-bold">Description</label>
        <textarea
          className="search-input border-2 border-gray-300 w-[400px] px-2 py-1 rounded resize-none"
          type="text"
          placeholder="Enter Name"
          rows={2}
          name="description"
          required
        />
      </div>
      <div>
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
                  required
                />
              </div>
              <div className="flex flex-col mb-3">
                <label className="text-[12px] font-bold">Column Type:</label>

                <select
                  name={`columnType${i}`}
                  id={`columnType${i}`}
                  className="block max-w-[250px] px-2 py-2 border-2 border-gray-300 rounded-md  focus:outline-none focus:border-gray-500"
                  style={{
                    fontSize: "14px",
                    color: "#8E8E8E",
                  }}
                  required
                >
                  <option value="NameType">NameType</option>
                  <option value="AddressType">AddressType</option>
                </select>
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
