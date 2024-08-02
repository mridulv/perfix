import { useState } from "react";
import { BiMinus, BiPlus } from "react-icons/bi";

import QueryComponentForRDBMS from "./QueryComponentForRDBMS";
import QueryComponentForNoSQL from "./QueryComponentForNoSQL";

const AddExperimentInputFields = ({ params }) => {
  const {
    writeBatchSizeValue,
    setWriteBatchSizeValue,
    concurrentQueries,
    setConcurrentQueries,
    experimentTimeInSecond,
    setExperimentTimeInSecond,
    selectedDatabaseCategory,
    sqlPlaceholder,
    setDbQuery,
    dataset
  } = params;

  const [selectedRDBMSOption, setSelectedRDBMSOption] = useState("sql");
  const [columns, setColumns] = useState([{ key: "", value: "" }]);
  const handleAddColumn = () => {
    setColumns([...columns, { key: "", value: "" }]);
  };

  const fieldsData = [
    {
      label: "Write Batch Size",
      value: writeBatchSizeValue,
      setValue: setWriteBatchSizeValue,
    },
    {
      label: "Concurrent Queries",
      value: concurrentQueries,
      setValue: setConcurrentQueries,
    },
    {
      label: "Experiment Time In Seconds",
      value: experimentTimeInSecond,
      setValue: setExperimentTimeInSecond,
    },
  ];

  const handleIncrease = (value, setValue) => {
    const numberValue = Number(value);
    setValue(numberValue + 1);
  };

  const handleDecrease = (value, setValue) => {
    setValue(value > 0 ? value - 1 : 0);
  };

  const handleInputChange = (e, setValue) => {
    setValue(parseInt(e.target.value, 10));
  };

  const datasetColumnsOptions = dataset && dataset?.columns?.map(col => ({
    value: col.columnName,
    label: col.columnName,
  }));

  console.log(datasetColumnsOptions);

  return (
    <div>
      <div className="flex flex-col mb-7">
        <label className="text-[12px] font-bold mb-1">Name</label>
        <input
          className="search-input px-2 py-1 border border-[#E0E0E0] rounded"
          type="text"
          name="name"
          placeholder="Enter Experiment Name"
          required
        />
      </div>

      <div className="w-full md:w-[70%] grid grid-cols-1 md:grid-cols-3 gap-8">
        {fieldsData?.map(({ label, value, setValue }) => (
          <div key={label} className="mb-4">
            <div className="mb-2">
              <label className="block text-xs font-semibold text-gray-700">
                {label}
              </label>
            </div>
            <div className="flex items-center px-2 pb-2 border-b border-r border-l border-gray-200 ">
              <button
                type="button"
                onClick={() => handleDecrease(value, setValue)}
                className="text-blue-500 hover:text-blue-600 focus:outline-none"
              >
                <BiMinus size={25} />
              </button>
              <input
                type="number"
                className="mx-3 w-full text-center text-sm font-medium bg-transparent focus:outline-none"
                value={value}
                onChange={(e) => handleInputChange(e, setValue)}
              />
              <button
                type="button"
                onClick={() => handleIncrease(value, setValue)}
                className="text-green-500 hover:text-green-600 focus:outline-none"
              >
                <BiPlus size={25} />
              </button>
            </div>
          </div>
        ))}
      </div>

      {selectedDatabaseCategory.value.includes("RDBMS") ? (
        <div>
          <label className="text-[12px] font-bold mb-1">Query</label>
          <div className="mt-2 mb-4 max-w-[160px] px-auto bg-secondary py-1 ps-3 flex items-center gap-3 rounded tracking-tight">
            <button
              type="button"
              onClick={() => setSelectedRDBMSOption("sql")}
              className={`py-1 px-2 text-[13px] ${
                selectedRDBMSOption === "sql" && "bg-white"
              } font-semibold rounded`}
            >
              SQL
            </button>
            <button
              type="button"
              onClick={() => setSelectedRDBMSOption("non-sql")}
              className={`py-1 px-2 text-[13px] ${
                selectedRDBMSOption === "non-sql" && "bg-white"
              } font-semibold rounded`}
            >
              SQL Builder
            </button>
          </div>
          <QueryComponentForRDBMS
            sqlPlaceholder={sqlPlaceholder}
            selectedOption={selectedRDBMSOption}
            columns={columns}
            setColumns={setColumns}
            handleAddColumn={handleAddColumn}
            setDbQuery={setDbQuery}
            datasetColumnsOptions={datasetColumnsOptions}
          />
        </div>
      ) : (
        <QueryComponentForNoSQL
          columns={columns}
          setColumns={setColumns}
          handleAddColumn={handleAddColumn}
          datasetColumnsOptions={datasetColumnsOptions}
          setDbQuery={setDbQuery}
        />
      )}
    </div>
  );
};

export default AddExperimentInputFields;
