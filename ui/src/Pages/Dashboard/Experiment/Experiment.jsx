import axios from "axios";
import React, { useState } from "react";
import toast from "react-hot-toast";
import { useQuery } from "react-query";
import AddExperimentModal from "../../../components/AddExperimentModal";
import UpdateExperimentModal from "../../../components/UpdateExperimentModal";
import Loading from "../../../components/Loading";
import { Link } from "react-router-dom";
import AddButton from "../../../components/AddButton";
import CommonTable from "../../../components/CommonTable";

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

  console.log(experiments);

  const handleAddExperiment = async (e) => {
    e.preventDefault();
    const form = e.target;
    const name = form.name.value;

    const isDuplicateName = experiments.some(
      (existingExperiment) =>
        existingExperiment.name.toLowerCase() === name.toLowerCase()
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
        form.reset();
      }
    } catch (err) {
      console.log(err);
    }
  };

  const handleStartExperiment = async (id) => {
    const res = await axios.post(
      `http://localhost:9000/experiment/${id}/execute`,
      {}
    );
    console.log(res);
  };

  if (datasetsLoading && configsLoading && experimentsLoading)
    return <Loading />;
  return (
    <div className="">
      <div className="pt-7 ps-7">
        <h3 className="text-2xl font-semibold">Experiments</h3>
      </div>
      <div className="w-[95%] h-[1px] bg-[#fcf8f8] my-6"></div>
      <div className="mb-3 ps-7 pe-9 flex justify-between">
        <div className="flex gap-x-4">
          <input
            className="w-[200px] p-1  border-2 border-gray-200 rounded search-input"
            type="text"
            name=""
            id=""
            placeholder="Search"
          />

          <select className="select-type w-[90px] px-2 py-2 border-2 border-gray-300 rounded-2xl text-gray-900 text-sm focus:ring-gray-500 focus:border-gray-500 ">
            <option>Owner</option>
          </select>
          <select className="select-type w-[90px] px-2 py-2 border-2 border-gray-300 rounded-2xl text-gray-900 text-sm focus:ring-gray-500 focus:border-gray-500 ">
            <option>Status</option>
          </select>
          <select className="select-type w-[110px] px-2 py-2 border-2 border-gray-300 rounded-2xl text-gray-900 text-sm focus:ring-gray-500 focus:border-gray-500 ">
            <option>Visible to</option>
          </select>
        </div>
        <div>
          <AddButton
            value={"New Experiment"}
            link={"/add-experiment-dataset"}
          />
        </div>
      </div>

      <div className="ps-7 pe-9 ">
        <CommonTable tableHead={"Experiment"}/>
      </div>
    </div>
  );
};

export default Experiment;
