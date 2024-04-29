import React from "react";
import { MdClose } from "react-icons/md";

const AddExperimentModal = ({
  open,
  onClose,
  fields,
  datasets,
  configs,
  handleAddExperiment,
}) => {
  return (
    <div
      onClick={onClose}
      className={`
        fixed inset-0 flex justify-center items-center transition-colors
        ${open ? "visible bg-black/20" : "invisible"} z-50 
      `}
    >
      {/* modal */}
      <div
        onClick={(e) => e.stopPropagation()}
        className={`
          bg-white rounded-xl shadow p-6 transition-all
          ${open ? "scale-100 opacity-100" : "scale-125 opacity-0"}
        `}
        style={{ maxHeight: "80vh", overflow: "auto" }}
      >
        <button
          onClick={onClose}
          className="absolute top-2 right-2 p-1 rounded-lg text-gray-400 bg-white hover:bg-gray-50 hover:text-gray-600"
        >
          <MdClose size={20} />
        </button>
        <div className="flex flex-col justify-center items-center">
          <h2>Add Experiment</h2>
          <form onSubmit={handleAddExperiment}>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-9">
              {fields.map(({ name, label, type, placeholder }) => (
                <label className="form-control w-full max-w-xs mb-2" key={name}>
                  <div className="label">
                    <span className="label-text text-sm">{label}</span>
                  </div>
                  <input
                    type={type}
                    className="input input-bordered w-full max-w-xs"
                    style={{ outline: "none" }}
                    required
                    name={name}
                    placeholder={placeholder}
                    autoComplete="off"
                  />
                </label>
              ))}
            </div>
            <div className="flex justify-around gap-x-4 mt-5">
              <label className="form-control w-full max-w-xs">
                <div className="label">
                  <span className="label-text">Select Dataset</span>
                </div>
                <select
                  style={{ outline: "none" }}
                  name="datasetId"
                  className="select select-bordered"
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
              <label className="form-control w-full max-w-xs">
                <div className="label">
                  <span className="label-text">Select Config</span>
                </div>
                <select
                  style={{ outline: "none" }}
                  name="databaseConfigId"
                  className="select select-bordered"
                  required
                >
                  {configs && configs.length > 0 ? (
                    configs.map((config) => (
                      <option
                        value={config.databaseConfigId.id}
                        key={config.databaseConfigId.id}
                      >
                        {config.name}
                      </option>
                    ))
                  ) : (
                    <option value="" disabled>
                      You haven't added any config
                    </option>
                  )}
                </select>
              </label>
            </div>
            <div className="my-3 flex justify-center">
              <input
                type="submit"
                value={"Submit"}
                className="btn btn-accent btn-md text-white"
              />
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AddExperimentModal;