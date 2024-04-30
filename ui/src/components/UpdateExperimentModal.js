import axios from "axios";
import React, { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { MdClose } from "react-icons/md";

const UpdateExperimentModal = ({
  open,
  onClose,
  fields,
  datasets,
  configs,
  experiments,
  refetch,
  actionLabel,
  experiment,
  buttonValue,
  setSelectedExperiment,
}) => {

  const [selectedDatasetId, setSelectedDatasetId] = useState("");
  const [selectedConfigId, setSelectedConfigId] = useState("");

  useEffect(() => {
    if (experiment) {
      setSelectedDatasetId(experiment.datasetId?.id || "");
      setSelectedConfigId(experiment.databaseConfigId?.id || "");
    }
  }, [experiment]);


  const handleUpdateExperiment = async (e) => {
    e.preventDefault();
    const form = e.target;
    const name = form.name.value;
    const existingName = experiment.name;
    if (name !== existingName) {
      const isDuplicateName = experiments.some(
        (existingExperiment) => existingExperiment.name.toLowerCase() === name.toLowerCase()
      );
  
      if (isDuplicateName) {
        toast.error(`An experiment with the name "${name}" already exists.`);
        return;
      }
    }
    const writeBatchSize = Number(form.writeBatchSize.value);
    const experimentTimeInSeconds = Number(form.experimentTimeInSeconds.value);
    const concurrentQueries = Number(form.concurrentQueries.value);
    const limitOpt = Number(form.limitOpt.value);
    const datasetId = Number(form.datasetId.value);
    const databaseConfigId = Number(form.databaseConfigId.value);

    const data = {
      experimentId: {
        id: experiment.experimentId.id,
      },
      name,
      writeBatchSize,
      experimentTimeInSeconds,
      concurrentQueries,
      query: {
        limitOpt,
      },
      datasetId: {
        id: datasetId,
      },
      databaseConfigId: {
        id: databaseConfigId,
      },
    };

    try {
      const res = await axios.post(
        `http://localhost:9000/experiment/${experiment?.experimentId.id}`,
        data
      );
      console.log(res);
      if (res.status === 200) {
        toast.success("Experiment updated successfully");
        refetch();
        setSelectedExperiment(null);
        onClose();
        form.reset();
      }
    } catch (err) {
      console.log(err);
    }
  };
  return (
    <div
      onClick={onClose}
      className={`
        fixed inset-0 flex justify-center items-center transition-colors
        ${open ? "visible bg-black/20" : "invisible"} z-50 
      `}
    >
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
          <h2>{actionLabel}</h2>
          <form onSubmit={handleUpdateExperiment}>
            <label className="form-control w-full max-w-sm mt-3">
              <div className="label">
                <span className="label-text text-sm">{fields[0].label}</span>
              </div>
              <input
                type={fields[0].type}
                defaultValue={experiment ? experiment.name : ""}
                className="input input-bordered w-full"
                style={{ outline: "none" }}
                required
                name={fields[0].name}
                placeholder={fields[0].placeholder}
                autoComplete="off"
              />
            </label>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-3">
              {fields.slice(1).map(({ name, label, type, placeholder }) => (
                <label className="form-control w-full max-w-xs mb-2" key={name}>
                  <div className="label">
                    <span className="label-text text-sm">{label}</span>
                  </div>
                  <input
                    type={type}
                    className="input input-bordered w-full max-w-xs"
                    defaultValue={experiment ? experiment[name] : ""}
                    style={{ outline: "none" }}
                    required
                    name={name}
                    placeholder={placeholder}
                    autoComplete="off"
                  />
                </label>
              ))}
              <label className="form-control w-full max-w-xs mb-2">
                <div className="label">
                  <span className="label-text text-sm">Limit Opt</span>
                </div>
                <input
                  type="number"
                  defaultValue={experiment ? experiment.query.limitOpt : ""}
                  className="input input-bordered w-full max-w-xs"
                  style={{ outline: "none" }}
                  required
                  name="limitOpt"
                  placeholder="Enter Limit Opt"
                  autoComplete="off"
                />
              </label>
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
                  value={selectedDatasetId}
                  onChange={(e) => setSelectedDatasetId(e.target.value)}
                >
                  {datasets && datasets.length > 0 ? (
                    datasets.map((dataset) => (
                      <option
                        value={dataset.id.id}
                        key={dataset.id.id}
                        // selected={
                        //   experiment &&
                        //   experiment.datasetId &&
                        //   dataset.id.id === experiment.datasetId.id
                        // }
                      >
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
                  value={selectedConfigId}
                  onChange={(e) => setSelectedConfigId(e.target.value)}
                >
                  {configs && configs.length > 0 ? (
                    configs.map((config) => (
                      <option
                        value={config.databaseConfigId.id}
                        key={config.databaseConfigId.id}
                        // selected={
                        //   experiment &&
                        //   experiment.databaseConfigId &&
                        //   config.databaseConfigId.id ===
                        //     experiment.databaseConfigId.id
                        // }
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
                value={buttonValue}
                className="btn btn-accent btn-md text-white"
              />
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default UpdateExperimentModal;
