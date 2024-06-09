import React, { useState } from "react";
import axios from "axios";
import { useQuery } from "react-query";
import Loading from "../../../components/Loading";
import AddButton from "../../../components/AddButton";
import CommonTable from "../../../components/CommonTable";
import CustomSelect from "../../../components/CustomSelect";
import AddExperimentModal from "../../../components/AddExperimentModal";

const columnHeads = [
  "Experiment Name",
  "Database Config Name",
  "Created At",
  "Experiment State",
  "Run",
];

const demoOptions = [
  { option: "Owner1", value: "Owner1" },
  { option: "Owner2", value: "Owner2" },
  { option: "Owner3", value: "Owner3" },
];
const demoOptions2 = [
  { option: "Status1", value: "Status1" },
  { option: "Status2", value: "Status2" },
  { option: "Status3", value: "Status3" },
];

const demoOptions3 = [
  { option: "Visible1", value: "Visible1" },
  { option: "Visible2", value: "Visible2" },
  { option: "Visible3", value: "Visible3" },
];

const Experiment = () => {
  const [open, setOpen] = useState(false);

  const [selectOwner, setSelectOwner] = useState({
    option: "Choose",
    value: "choose",
  });
  const [selectStatus, setSelectStatus] = useState({
    option: "Choose",
    value: "choose",
  });
  const [selectVisible, setSelectVisible] = useState({
    option: "Choose",
    value: "choose",
  });

  const { data: experiments, isLoading: experimentsLoading } = useQuery({
    queryKey: ["experiments"],
    queryFn: async () => {
      const values = [];
      const res = await axios.post(
        `${process.env.REACT_APP_BASE_URL}/experiment`,
        values,
        {
          headers: {
            "Content-Type": "application/json"
          },
          withCredentials: true,
        }
      );
      const data = await res.data;
      return data;
    },
  });

  
  console.log(experiments);

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

  // if (experimentsLoading) return <Loading />;
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
            width="w-[150px]"
          />
          <CustomSelect
            selected={selectStatus}
            setSelected={setSelectStatus}
            options={demoOptions2}
            width="w-[150px]"
          />
          <CustomSelect
            selected={selectVisible}
            setSelected={setSelectVisible}
            options={demoOptions3}
            width="w-[150px]"
          />
        </div>
        <div>
          <AddButton value={"New Experiment"} setOpen={setOpen} />
        </div>
      </div>
      <AddExperimentModal open={open} onClose={() => setOpen(false)} experiments={experiments} />

      <div className="ps-7 pe-9 ">
        <CommonTable
          data={experiments}
          tableHead={"Experiment"}
          columnHeads={columnHeads}
          dataForRun={dataForRun}
        />
      </div>
    </div>
  );
};

export default Experiment;
