import React from "react";
import RangeInput from "../Common/RangeInput";

const AddExperimentInputFields = ({ params }) => {
  const {
    writeBatchSizeValue,
    setWriteBatchSizeValue,
    concurrentQueries,
    setConcurrentQueries,
    experimentTimeInSecond,
    setExperimentTimeInSecond,
  } = params;
  return (
    <div>
      <div className="flex flex-col mb-7">
        <label className="text-[12px] font-bold mb-[2px]">Name</label>
        <input
          className="search-input w-[250px] px-2 py-1 border border-[#E0E0E0] rounded"
          type="text"
          name="name"
          placeholder="Enter Experiment Name"
          required
        />
      </div>
      <div className="mb-4">
        <RangeInput
          value={writeBatchSizeValue}
          setValue={setWriteBatchSizeValue}
          startValue={"0mhz"}
          lastValue={"1500mhz"}
          label={"Write Batch Size"}
        />
      </div>
      <div className="mb-4">
        <RangeInput
          value={concurrentQueries}
          setValue={setConcurrentQueries}
          startValue={"0mhz"}
          lastValue={"1500mhz"}
          label={"Concurrent Queries"}
        />
      </div>
      <div className="mb-4">
        <RangeInput
          value={experimentTimeInSecond}
          setValue={setExperimentTimeInSecond}
          startValue={"0mhz"}
          lastValue={"1500mhz"}
          label={"Experiment Time In Seconds"}
        />
      </div>
      <div className="flex flex-col mb-7">
        <label className="text-[12px] font-bold mb-[2px]">Query</label>
        <input
          className="search-input w-[250px] px-2 py-1 border border-[#E0E0E0] rounded"
          type="number"
          name="limitOpt"
          placeholder="Enter Number"
          required
        />
      </div>
    </div>
  );
};

export default AddExperimentInputFields;
