import axios from "axios";
import React, { useState } from "react";
import toast from "react-hot-toast";
import { useQuery } from "react-query";
import AddExperimentModal from "../../../components/AddExperimentModal";
import UpdateExperimentModal from "../../../components/UpdateExperimentModal";
import Loading from "../../../components/Loading";

const fields = [
  {
    name: "name",
    label: "Experiment Name",
    type: "text",
    placeholder: "Experiment Name",
  },
  {
    name: "writeBatchSize",
    label: "Write Batch Size",
    type: "number",
    placeholder: "Write Batch Size",
  },
  {
    name: "experimentTimeInSeconds",
    label: "Experiment Time",
    type: "number",
    placeholder: "In Seconds",
  },
  {
    name: "concurrentQueries",
    label: "Concurrent Queries",
    type: "number",
    placeholder: "Concurrent Queries",
  },
];
const Experiment = () => {
  const [open, setOpen] = useState(false);
  const [openForUpdate, setOpenForUpdate] = useState(false);
  const [selectedExperiment, setSelectedExperiment] = useState(null);

  const { data: datasets, isLoading: datasetsLoading } = useQuery({
    queryKey: ["datasets"],
    queryFn: async () => {
      const res = await axios.get("http://localhost:9000/dataset");
      const data = await res.data;
      return data;
    },
  });
  const { data: configs, isLoading: configsLoading } = useQuery({
    queryKey: ["configs"],
    queryFn: async () => {
      const res = await axios.get("http://localhost:9000/config");
      const data = await res.data;
      return data;
    },
  });
  const {
    data: experiments,
    isLoading: experimentsLoading,
    refetch,
  } = useQuery({
    queryKey: ["experiments"],
    queryFn: async () => {
      const res = await axios.get("http://localhost:9000/experiment");
      const data = await res.data;
      return data;
    },
  });


  const handleAddExperiment = async (e) => {
    e.preventDefault();
    const form = e.target;
    const name = form.name.value;


    const isDuplicateName = experiments.some(
      (existingExperiment) => existingExperiment.name.toLowerCase() === name.toLowerCase()
    );
  
    if (isDuplicateName) {
      toast.error(`An experiment with the name "${name}" already exists.`);
      return;
    }
    
    const writeBatchSize = Number(form.writeBatchSize.value);
    const experimentTimeInSeconds = Number(form.experimentTimeInSeconds.value);
    const concurrentQueries = Number(form.concurrentQueries.value);
    const limitOpt = Number(form.limitOpt.value);
    const datasetId = Number(form.datasetId.value);
    const databaseConfigId = Number(form.databaseConfigId.value);

    const data = {
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
    console.log(data);

    try {
      const res = await axios.post("http://localhost:9000/experiment", data, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      console.log(res);
      if (res.status === 200) {
        toast.success("Experiment created successfully");
        refetch();
        setOpen(false);
        form.reset();
      }
      
    } catch (err) {
      console.log(err);
    }
  };

  
  const handleOpenModalForUpdate = (experiment) => {
    // setSelectedExperiment(experiment);
    setOpenForUpdate(true);
    setSelectedExperiment(experiment)
  };

  if (datasetsLoading && configsLoading && experimentsLoading)
    return <Loading />;
  return (
    <div>
      <h3 className="text-center text-xl my-4 font-semibold">Experiments</h3>

      <div className="flex justify-end pe-8 mb-3">
        <button
          className="btn btn-error btn-md text-white"
          onClick={() => setOpen(true)}
        >
          Add Experiment
        </button>
      </div>
      {datasets && configs && (
        <AddExperimentModal
          open={open}
          onClose={() => setOpen(false)}
          fields={fields}
          datasets={datasets}
          configs={configs}
          handleAction={handleAddExperiment}
          actionLabel={"Add Experiment"}
          buttonValue={"Add"}
        ></AddExperimentModal>
      )}

      {experiments && experiments.length < 1 ? (
        <p className="ps-5">You haven't added any experiments yet.</p>
      ) : (
        <div className="grid grid-cols-2 md:grid-cols-4 gap-3 px-4 mx-auto my-4">
          {experiments &&
            experiments.map((experiment) => (
              <div
                key={experiment.name}
                className="card w-60 bg-purple-400 shadow-xl"
               >
                <div className="card-body">
                  <h2 className="text-white card-title">
                    name: {experiment.name}
                  </h2>
                  <div>
                    <button
                      onClick={() => handleOpenModalForUpdate(experiment)}
                      className="btn btn-success btn-sm text-sm"
                    >
                      Update
                    </button>
                  </div>
                </div>
                {datasets && configs && (
                  <UpdateExperimentModal
                  open={openForUpdate}
                  onClose={() => setOpenForUpdate(false)}
                    fields={fields}
                    datasets={datasets}
                    configs={configs}
                    experiments={experiments}
                    refetch={refetch}
                    experiment={selectedExperiment}
                    setSelectedExperiment={setSelectedExperiment}
                    actionLabel={"Update Experiment"}
                    buttonValue={"Update"}
                  ></UpdateExperimentModal>
                )}
              </div>
            ))}
        </div>
      )}
    </div>
  );
};

export default Experiment;
