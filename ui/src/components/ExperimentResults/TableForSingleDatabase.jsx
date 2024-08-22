/* eslint-disable no-unused-vars */
import React from "react";

const TableForSingleDatabase = ({
  percentileHeaders,
  readLatencies,
  writeLatencies,
  resultValues,
  databaseConfigs
}) => {
  return (
    <div className="p-6">
      <div className="overflow-x-auto rounded-l-lg shadow">
        <table className="min-w-full bg-white ">
          <thead className="border">
            <tr>
              <th className="text-sm py-3 ps-3 border-r text-start">
                Indicator
              </th>
              <th className="text-sm py-3 ps-3 border-r text-start">
                {" "}
                {databaseConfigs[0].storeType}:{" "}
                {databaseConfigs[0].databaseConfigName}
              </th>
            </tr>
          </thead>
          <tbody>
              {resultValues.map((value, i) => (
                <tr key={i}>
                  <td className="py-2 px-4 text-xs font-semibold border-b border-r">{value.text}</td>
                  <td className="py-2 px-4 text-sm border-b border-r">{value.value}</td>
                </tr>
              ))}
            
          </tbody>
        </table>
      </div>

      <div className="mt-6 overflow-x-auto rounded-t-lg shadow">
        <table className="min-w-full bg-white rounded-lg">
          <thead className="border">
            <tr>
              <th className="text-xs py-3 ps-3 border-r text-start">
                PerFormance Percentile
              </th>
              {percentileHeaders.map((head, i) => (
                <th key={i} className="px-2 text-xs border-r">{head}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            <tr>
              <th className="text-xs py-3 ps-3  border-b border-r text-start">
                Read Latencies
              </th>
              {readLatencies.map((latency, i) => (
                <td key={i} className="py-2 px-4 text-sm border-b border-r">
                  {latency}
                </td>
              ))}
            </tr>
            <tr>
              <th className="text-xs py-3 ps-3  border-b border-r text-start">
                Write Latencies
              </th>
              {writeLatencies.map((latency, i) => (
                <td key={i} className="py-2 px-4 text-sm border-b border-r">
                  {latency}
                </td>
              ))}
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default TableForSingleDatabase;
