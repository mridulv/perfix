import React, { useState } from "react";
import axios from "axios";
import { useQuery } from "react-query";
import Loading from "../../../components/Loading";
import AddButton from "../../../components/AddButton";
import CommonTable from "../../../components/CommonTable";


const columnHeads = ["Experiment Name", "Database Config Name", "Created At", "Experiment State", "Run"];


const Experiment = () => {
  const [isRunStart, setIsRunStart] = useState(false);

  const {data: experiments,isLoading: experimentsLoading} = useQuery({
    queryKey: ["experiments"],
    queryFn: async () => {
      const values = []
      const res = await axios.post(`${process.env.REACT_APP_BASE_URL}/experiment`, values, {
        headers: {
          "Content-Type": "application/json"
        }
      });
      const data = await res.data;
      return data;
    },
  });

  console.log(experiments);

  const dataForTable = experiments?.map(experiment => (
    {
      experimentName: experiment.name,
      databaseConfigName: "Pending",
      createdAt: new Date(experiment.createdAt).toLocaleDateString(),
      experimentState: experiment.experimentState,
      isRunStart,
      setIsRunStart
    }
  ));

  const dataForRun = {
    overallQueryTime: 5,
    overallWriteTimeTaken: 39,
    numberOfCalls: 13522,
    queryLatencies: [
      { percentile: 5, latency: 1 },
      { percentile: 10, latency: 2 },
      { percentile: 25, latency: 3 },
      { percentile: 50, latency: 3 },
      { percentile: 75, latency: 5 },
      { percentile: 90, latency: 6 },
      { percentile: 95, latency: 7 },
      { percentile: 99, latency: 9 },
    ],
    writeLatencies: [
      { percentile: 5, latency: 39 },
      { percentile: 10, latency: 39 },
      { percentile: 25, latency: 39 },
      { percentile: 50, latency: 39 },
      { percentile: 75, latency: 39 },
      { percentile: 90, latency: 39 },
      { percentile: 95, latency: 39 },
      { percentile: 99, latency: 39 },
    ],
  };


  if (experimentsLoading) return <Loading />;
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
            <option className="">Owner</option>
            <option className="">Owner1</option>
            <option className="">Owner2</option>
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
        <CommonTable data={dataForTable} tableHead={"Experiment"} columnHeads={columnHeads} dataForRun={dataForRun}/>
      </div>
    </div>
  );
};

export default Experiment;
