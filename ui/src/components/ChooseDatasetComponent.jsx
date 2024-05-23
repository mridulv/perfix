import React from 'react';
import { Link } from 'react-router-dom';
import AddDataset from './AddDataset';

const ChooseDatasetComponent = ({activeDataset, setActiveDataset, handleSubmit, columns, handleAddColumn, datasets}) => {
    return (
        <div className="mt-7 ms-7 w-[320px]">
        <div className="bg-[#fbeaee] py-1 ps-3  flex items-center gap-3 rounded">
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
                <select
                  style={{ outline: "none" }}
                  name="datasetId"
                  className="select select-bordered block max-w-[250px] px-2 py-2 border-2 border-gray-300 rounded-md shadow-sm focus:outline-none focus:border-gray-500"
                  required
                >
                  {datasets && datasets.length > 0 ? (
                    datasets.map((dataset) => (
                      <option value={dataset.id.id} key={dataset.id.id}>
                        {dataset.name}
                      </option>
                    ))
                  ) : (
                    <option value="" disabled>
                      You haven't added any dataset
                    </option>
                  )}
                </select>
              </label>
            )}

            <div className="mt-[50px] flex gap-3 pb-4">
              <button
                className="btn bg-[#E5227A] btn-sm border border-[#E5227A] rounded text-white hover:bg-[#6b3b51d2]"
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