/* eslint-disable no-unused-vars */
import React from "react";
import { Link } from "react-router-dom";
import AddDataset from "../AddDataset/AddDataset";
import CustomSelect from "../CustomSelect/CustomSelect";

const ChooseDatasetComponent = ({
  activeDataset,
  setActiveDataset,
  handleSubmit,
  columns,
  handleAddColumn,
  datasets,
  selectedDataset,
  setSelectedDataset,
  reduceHeight,
}) => {
  let options = [];

  datasets?.forEach((dataset) =>
    options.push({ option: dataset.name, value: dataset.id.id })
  );

  return (
    <div className="mt-7 ms-7 max-w-[300px] flex flex-col" style={{ minHeight: `calc(100vh - ${reduceHeight})` }}>
      <div className="bg-secondary py-1 ps-3 flex items-center gap-3 rounded tracking-tight">
        <button
          onClick={() => setActiveDataset("existing")}
          className={`py-2 px-2 text-[12px] ${
            activeDataset === "existing" && "bg-white"
          } font-semibold rounded transition ease-in-out delay-150`}
        >
          Choose existing dataset
        </button>
        <button
          onClick={() => setActiveDataset("new")}
          className={`py-2 px-2 text-[12px] ${
            activeDataset === "new" && "bg-white"
          } font-semibold rounded transition ease-in-out delay-100`}
        >
          Create new dataset
        </button>
      </div>

      <form onSubmit={handleSubmit} className="flex flex-col flex-grow">
        <div className="mt-6 flex-grow">
          {activeDataset === "new" ? (
            <AddDataset columns={columns} handleAddColumn={handleAddColumn} />
          ) : (
            <label className="form-control max-w-[200px]">
              <div className="label">
                <span className="label-text">Select Dataset</span>
              </div>
              <CustomSelect
                selected={selectedDataset}
                setSelected={setSelectedDataset}
                options={options}
                width="w-[200px]"
              ></CustomSelect>
            </label>
          )}
        </div>

        <div className="mt-auto pt-4 flex gap-3">
          <button
            className="btn bg-primary btn-sm border border-primary rounded text-white hover:bg-[#57B1FF]"
            type="submit"
          >
            Next
          </button>
          <Link
            to="/experiment"
            className="px-3 py-1 text-[14px] font-bold border-2 border-gray-300 rounded"
          >
            Cancel
          </Link>
        </div>
      </form>
    </div>
  );
};

export default ChooseDatasetComponent;