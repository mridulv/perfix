import React, { useState } from "react";
import axios from "axios";
import { useQuery } from "react-query";
import Loading from "../../../components/Loading";
import AddButton from "../../../components/AddButton";
import CommonTable from "../../../components/CommonTable";
import CustomSelect from "../../../components/CustomSelect";

const columnHeads = [
  "Experiment Name",
  "Database Config Name",
  "Created At",
  "Experiment State",
  "Run",
];

const demoOptions = ["Owner1", "Owner2", "Owner3"];
const demoOptions2 = ["Status1", "Status2", "Status3"];
const demoOptions3 = ["Visible1", "Visible2", "Visible3"];

const Experiment = () => {
  const [isRunStart, setIsRunStart] = useState(false);
  const [selectOwner, setSelectOwner] = useState("Choose");
  const [selectStatus, setSelectStatus] = useState("Choose");
  const [selectVisible, setSelectVisible] = useState("Choose");

  const { data: experiments, isLoading: experimentsLoading } = useQuery({
    queryKey: ["experiments"],
    queryFn: async () => {
      const values = [];
      const res = await axios.post(
        `${process.env.REACT_APP_BASE_URL}/experiment`,
        values,
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        }
      );
      const data = await res.data;
      return data;
    },
  });

  console.log(experiments);

  const dataForTable = experiments?.map((experiment) => ({
    experimentName: experiment.name,
    databaseConfigName: "Pending",
    createdAt: new Date(experiment.createdAt).toLocaleDateString(),
    experimentState: experiment.experimentState,
    isRunStart,
    setIsRunStart,
  }));

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
      <div className="w-[95%] h-[1px] bg-accent my-6"></div>
      <div className="mb-3 ps-7 pe-9 flex justify-between">
        <div className="flex gap-x-4">
          <input
            className="w-[200px] p-1  border-2 border-gray-200 rounded search-input"
            type="text"
            name=""
            id=""
            placeholder="Search"
          />

          <CustomSelect
            selected={selectOwner}
            setSelected={setSelectOwner}
            options={demoOptions}
          />
          <CustomSelect
            selected={selectStatus}
            setSelected={setSelectStatus}
            options={demoOptions2}
          />
          <CustomSelect
            selected={selectVisible}
            setSelected={setSelectVisible}
            options={demoOptions3}
          />
        </div>
        <div>
          <AddButton
            value={"New Experiment"}
            link={"/add-experiment-dataset"}
          />
        </div>
      </div>

      <div className="ps-7 pe-9 ">
        <CommonTable
          data={dataForTable}
          tableHead={"Experiment"}
          columnHeads={columnHeads}
          dataForRun={dataForRun}
        />
      </div>
    </div>
  );
};

export default Experiment;
