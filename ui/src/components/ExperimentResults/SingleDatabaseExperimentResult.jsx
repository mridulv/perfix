/* eslint-disable no-unused-vars */
import React from "react";
import ReactECharts from "echarts-for-react";
import TableForSingleDatabase from "./TableForSingleDatabase";

const SingleDatabaseExperimentResult = ({ view, handleView, data }) => {
  const { experimentResults, databaseConfigs } = data;

  const results = experimentResults[0];

  const resultValues = [
    { text: "Overall Query Time", value: `${results.experimentResult.overallQueryTime}s` },
    { text: "Overall Write Time", value: `${results.experimentResult.overallWriteTimeTaken}s` },
    { text: "Total Number of Calls", value: results.experimentResult.numberOfCalls },
    { text: "Error", value: 9 },
  ];

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

  const readLatenciesValues = [1, 2, 3, 5, 6, 7, 9, 10];
  const writeLatenciesValues = [1, 2, 3, 5, 6, 7, 9, 10];

  const optionsForRead = {
    title: {
      text: "Read Latencies",
    },
    tooltip: {
      trigger: "axis",
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    toolbox: {
      feature: {
        saveAsImage: {},
      },
    },
    xAxis: {
      type: "category",
      data: yAxisValues,
    },
    yAxis: {
      type: "value",
    },
    series: [
      {
        name: `${data.name} - Read Latencies`,
        type: "line",
        stack: "Total",
        data: readLatenciesValues,
        itemStyle: {
          color: "#059212", // Set color for the line
        },
      },
    ],
  };
  const optionsForWrite = {
    title: {
      text: "Write Latencies",
    },
    tooltip: {
      trigger: "axis",
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    toolbox: {
      feature: {
        saveAsImage: {},
      },
    },
    xAxis: {
      type: "category",
      data: yAxisValues,
    },
    yAxis: {
      type: "value",
    },
    series: [
      {
        name: `${data.name} - Write Latencies`,
        type: "line",
        stack: "Total",
        data: writeLatenciesValues,
        itemStyle: {
          color: "#EE6666", // Set color for the line
        },
      },
    ],
  };
  return (
    <div className="px-7">
      <div className="flex items-center justify-between mb-7">
        <p className="text-xl font-semibold">
          {databaseConfigs[0].storeType}:{" "}
          {databaseConfigs[0].databaseConfigName}
        </p>
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
        <div className="flex justify-between mb-4">
          {resultValues.map((value) => (
            <div
              key={value.text}
              className="w-[280px] p-5 text-start border border-[#E0E0E0] rounded-xl hover:scale-110 transition-all duration-500"
            >
              <p className="text-xs mb-2 font-semibold">{value.text}</p>
              <p className="text-xl">{value.value}</p>
            </div>
          ))}
        </div>

        <div className="mt-8 flex flex-col gap-y-5">
          <ReactECharts option={optionsForRead} />
          <ReactECharts option={optionsForWrite} />
        </div>
      </div>

      <div
        className={`transition-opacity duration-500 ${
          view === "table" ? "opacity-100" : "opacity-0 h-0 overflow-hidden"
        }`}
      >
        <TableForSingleDatabase
          percentileHeaders={yAxisValues}
          readLatencies={readLatenciesValues}
          writeLatencies={writeLatenciesValues}
          resultValues={resultValues}
        />
      </div>
    </div>
  );
};

export default SingleDatabaseExperimentResult;
