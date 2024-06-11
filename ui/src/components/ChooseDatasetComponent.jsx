import React from "react";
import { Link } from "react-router-dom";
import AddDataset from "./AddDataset";
import CustomSelect from "./CustomSelect";

const ChooseDatasetComponent = ({
  activeDataset,
  setActiveDataset,
  handleSubmit,
  columns,
  handleAddColumn,
  datasets,
  selectedDataset,
  setSelectedDataset,
}) => {
  let options = [];

  datasets?.forEach((dataset) =>
    options.push({ option: dataset.name, value: dataset.id.id })
  );

  return (
    <div className="mt-7 ms-7 w-[320px]">
      <div className="bg-secondary py-1 ps-3  flex items-center gap-3 rounded">
        <button
          onClick={() => setActiveDataset("existing")}
          className={`py-1 px-2 text-[12px] ${
            activeDataset === "existing" && "bg-white"
          } font-semibold rounded transition ease-in-out delay-150`}
        >
          Choose existing dataset
        </button>
        <button
          onClick={() => setActiveDataset("new")}
          className={`py-1 px-2 text-[12px] ${
            activeDataset === "new" && "bg-white"
          } font-semibold rounded transition ease-in-out delay-100`}
        >
          Create new dataset
        </button>
      </div>

      <div className="mt-6">
        <form onSubmit={handleSubmit}>
          {activeDataset === "new" ? (
            <AddDataset columns={columns} handleAddColumn={handleAddColumn} />
          ) : (
            <label className="form-control w-full max-w-xs">
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

          <div className="mt-[200px] flex gap-3 pb-4">
            <button
              className="btn bg-primary btn-sm border border-primary rounded text-white hover:bg-[#6b3b51d2]"
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
    </div>
  );
};

export default ChooseDatasetComponent;
