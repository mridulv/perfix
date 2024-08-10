/* eslint-disable no-unused-vars */
import React, { useState, lazy, Suspense } from "react";
import StatBlock from "./StatBlock";
import { chartOptions } from "../../hooks/chartOptions";
import {
  generateSeriesData,
  generateStatBlockData,
} from "../../hooks/dataHelpers";
import TableForMultiDatabase from "./TableForMultiDatabase";

const ReactECharts = lazy(() => import("echarts-for-react"));

const MultiDatabaseExperimentResult = ({
  experimentData,
  view,
  handleView,
}) => {
  const { experimentResults, databaseConfigs } = experimentData;

  const databaseInfo = databaseConfigs.map(
    (config) => `${config.storeType}: ${config.databaseConfigName}`
  );

  const readLatenciesValues = experimentResults.map((result) =>
    result.experimentResult.queryLatencies.map((latency) => latency.latency)
  );

  const writeLatenciesValues = experimentResults.map((result) =>
    result.experimentResult.writeLatencies.map((latency) => latency.latency)
  );

  const colors = ["#09E9AB", "#D37FFF", "#4B89FF", "#5470C6", "#91CC75"];

  const readSeries = generateSeriesData(
    readLatenciesValues,
    databaseInfo,
    colors
  );
  const writeSeries = generateSeriesData(
    writeLatenciesValues,
    databaseInfo,
    colors
  );

  const yAxisValues = [
    "5th Percentile",
    "10th Percentile",
    "25th Percentile",
    "50th Percentile",
    "60th Percentile",
    "75th Percentile",
    "90th Percentile",
    "99th Percentile",
  ];

  const initialSelected = Object.fromEntries(
    databaseInfo.map((db) => [db, true])
  );
  const [selected, setSelected] = useState(initialSelected);

  const toggleSelected = (dataName) => {
    setSelected((prevSelected) => ({
      ...prevSelected,
      [dataName]: !prevSelected[dataName],
    }));
  };

  const optionsForRead = chartOptions.getChartOptionsForMultiDatabase(
    "Read Latencies",
    readSeries,
    yAxisValues,
    selected
  );

  const optionsForWrite = chartOptions.getChartOptionsForMultiDatabase(
    "Write Latencies",
    writeSeries,
    yAxisValues,
    selected
  );

  const overallData = generateStatBlockData(
    experimentResults,
    "Overall Query Time",
    300,
    "overallQueryTime"
  );
  const writeTimeData = generateStatBlockData(
    experimentResults,
    "Overall Write Time",
    10,
    "overallWriteTimeTaken"
  );
  const numberOfCallsData = generateStatBlockData(
    experimentResults,
    "Total number of calls",
    15000,
    "numberOfCalls"
  );
  const errorsData = [{ name: "Errors", maxValue: 20, values: [10, 8, 3] }];

  return (
    <div className="px-7">
      <div className={`flex ${view === "graph" ? "justify-between" : "justify-end"}  items-center mb-8`}>
        {view === "graph" && (
          <div className="flex gap-x-4">
            {databaseInfo.map((info, i) => (
              <button
                key={info}
                className={`p-3 ${
                  selected[info] ? "bg-white" : "bg-gray-300"
                } text-sm font-semibold shadow-lg rounded flex items-center gap-x-2`}
                onClick={() => toggleSelected(info)}
              >
                <span
                  className="w-3 h-3 rounded-full inline-block"
                  style={{ backgroundColor: colors[i] }}
                ></span>
                {info}
              </button>
            ))}
          </div>
        )}
        <div className="bg-accent p-1 flex rounded-md cursor-pointer">
          <p
            onClick={() => handleView("table")}
            className={`${
              view === "table" && "bg-white"
            } p-2 text-sm font-semibold rounded transition-all duration-300`}
          >
            Table View
          </p>
          <p
            onClick={() => handleView("graph")}
            className={`${
              view === "graph" && "bg-white"
            } p-2 text-sm font-semibold rounded transition-all duration-300`}
          >
            Graph View
          </p>
        </div>
      </div>

      <div
        className={`transition-opacity duration-500 ${
          view === "graph" ? "opacity-100" : "opacity-0 h-0 overflow-hidden"
        }`}
      >
        <div className="grid grid-cols-2 gap-6 mb-6">
          <StatBlock data={overallData} colors={colors} selected={selected} />
          <StatBlock data={writeTimeData} colors={colors} selected={selected} />
          <StatBlock
            data={numberOfCallsData}
            colors={colors}
            selected={selected}
          />
          <StatBlock data={errorsData} colors={colors} selected={selected} />
        </div>
        <div className="flex flex-col gap-y-5">
          <Suspense fallback={<div>Loading charts...</div>}>
            <ReactECharts option={optionsForRead} />
            <ReactECharts option={optionsForWrite} />
          </Suspense>
        </div>
      </div>

      <div
        className={`transition-opacity duration-500 ${
          view === "table" ? "opacity-100" : "opacity-0 h-0 overflow-hidden"
        }`}
      >
        <TableForMultiDatabase experimentData={experimentData} />
      </div>
    </div>
  );
};

export default MultiDatabaseExperimentResult;
