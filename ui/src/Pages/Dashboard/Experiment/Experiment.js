import { data } from "autoprefixer";
import axios from "axios";
import React, { useState } from "react";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";
import { useQuery } from "react-query";
import AddExperimentModal from "../../../components/AddExperimentModal";

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
  {
    name: "limitOpt",
    label: "Limit Opt",
    type: "number",
    placeholder: "Limit Opt",
  },
];
const Experiment = () => {
  const [open, setOpen] = useState(false);

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
  const { data: experiments, isLoading: experimentsLoading, refetch } = useQuery({
    queryKey: ["experiments"],
    queryFn: async () => {
      const res = await axios.get("http://localhost:9000/experiment");
      const data = await res.data;
      return data;
    },
  });

  console.log(experiments);

  const handleAddExperiment = async (e) => {
    e.preventDefault();
    const form = e.target;
    const name = form.name.value;
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

  if (datasetsLoading && configsLoading && experimentsLoading) {
    return <p>Loading...</p>;
  }
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
          handleAddExperiment={handleAddExperiment}
        ></AddExperimentModal>
      )}

      {
        experiments && experiments.length < 1 ? 
        (
          <p className="ps-5">You haven't added any experiments yet.</p>
        ) : 
        (
         <div className="grid grid-cols-2 md:grid-cols-4 gap-3 px-4 mx-auto my-4">
           {
            experiments &&
           experiments.map((experiment) => (
            <div className="card w-60 bg-purple-400 shadow-xl">
              <div className="card-body">
                <h2 className="text-white card-title">name: {experiment.name}</h2>
              </div>
            </div>
          ))}
         </div>
        )
      }
    </div>
  );
};

export default Experiment;