/* eslint-disable no-unused-vars */
import React, { useState, useCallback } from "react";
import { BiMinus, BiPlus } from "react-icons/bi";
import QueryComponentForRDBMS from "./QueryComponentForRDBMS";
import QueryComponentForNoSQL from "./QueryComponentForNoSQL";
import toast from "react-hot-toast";

const AddExperimentInputFields = ({ params }) => {
  const {
    experimentName,
    setExperimentName,
    writeBatchSizeValue,
    setWriteBatchSizeValue,
    concurrentQueries,
    setConcurrentQueries,
    experimentTimeInSecond,
    setExperimentTimeInSecond,
    selectedDatabaseCategory,
    sqlPlaceholder,
    setDbQuery,
    dataset,
    initialDbQuery,
  } = params;

  const [selectedRDBMSOption, setSelectedRDBMSOption] = useState(
    initialDbQuery?.type === "sql-builder" ? "non-sql" : "sql"
  );
  console.log(initialDbQuery?.filters);
  const [columns, setColumns] = useState(
    initialDbQuery?.filtersOpt
      ? initialDbQuery.filtersOpt.map((filter) => ({
          key: filter.field,
          value: filter.fieldValue,
        }))
      : initialDbQuery?.filters
      ? initialDbQuery.filters.map((d) => ({
        
          key: d.field,
          value: d.fieldValue,
        }))
      : [{ key: "", value: "" }]
  );

  const handleAddColumn = useCallback(() => {
    setColumns((prevColumns) => [...prevColumns, { key: "", value: "" }]);
  }, []);

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
      max: 100,
    },
    {
      label: "Experiment Time In Seconds",
      value: experimentTimeInSecond,
      setValue: setExperimentTimeInSecond,
    },
  ];

  const handleIncrease = useCallback((value, setValue, max) => {
    const numberValue = Number(value);
    if (max !== undefined && numberValue >= max) {
      toast.error(`Value cannot be greater than ${max}`);
    } else {
      setValue(numberValue + 1);
    }
  }, []);

  const handleDecrease = useCallback((value, setValue) => {
    setValue((prevValue) => (prevValue > 0 ? prevValue - 1 : 0));
  }, []);

  const handleInputChange = useCallback((e, setValue, max) => {
    const newValue = parseInt(e.target.value, 10);
    if (max !== undefined && newValue > max) {
      toast.error(`Value cannot be greater than ${max}`);
    } else {
      setValue(newValue);
    }
  }, []);

  const datasetColumnsOptions =
    dataset &&
    dataset?.columns?.map((col) => ({
      value: col.columnName,
      label: col.columnName,
    }));

  const memoizedSetDbQuery = useCallback(
    (newQuery) => {
      setDbQuery((prevQuery) => ({
        ...prevQuery,
        ...newQuery,
      }));
    },
    [setDbQuery]
  );

  return (
    <div>
      <div className="flex flex-col mb-7">
        <label className="text-[12px] font-bold mb-1">Name</label>
        <input
          className="search-input px-2 py-1 border border-[#E0E0E0] rounded"
          type="text"
          name="name"
          placeholder="Enter Experiment Name"
          value={experimentName}
          onChange={(e) => setExperimentName(e.target.value)}
          required
        />
      </div>

      <div className="w-full md:w-[70%] grid grid-cols-1 md:grid-cols-3 gap-8">
        {fieldsData?.map(({ label, value, setValue, max }) => (
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
                onChange={(e) => handleInputChange(e, setValue, max)}
              />
              <button
                type="button"
                onClick={() => handleIncrease(value, setValue, max)}
                className="text-green-500 hover:text-green-600 focus:outline-none"
              >
                <BiPlus size={25} />
              </button>
            </div>
          </div>
        ))}
      </div>

      {(typeof selectedDatabaseCategory === "string"
        ? selectedDatabaseCategory
        : selectedDatabaseCategory.value
      ).includes("RDBMS") ? (
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
            setDbQuery={memoizedSetDbQuery}
            datasetColumnsOptions={datasetColumnsOptions}
            initialDbQuery={initialDbQuery}
          />
        </div>
      ) : (
        <QueryComponentForNoSQL
          columns={columns}
          setColumns={setColumns}
          handleAddColumn={handleAddColumn}
          datasetColumnsOptions={datasetColumnsOptions}
          setDbQuery={memoizedSetDbQuery}
          initialDbQuery={initialDbQuery}
        />
      )}
    </div>
  );
};

export default AddExperimentInputFields;
