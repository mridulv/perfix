import React from "react";

const TableForMultiDatabase = ({ experimentData }) => {
  const headers = experimentData.databaseConfigs.map(
    (config) => `${config.storeType}: ${config.databaseConfigName}`
  );

  // Create a mapping of databaseConfigName to result objects
  const resultMap = {};
  experimentData.experimentResults.forEach((result) => {
    resultMap[result.databaseConfigDetails.databaseConfigName] = result.experimentResult;
  });

  console.log(resultMap);

  const percentileHeaders = [
    "5th Percentile",
    "10th Percentile",
    "25th Percentile",
    "50th Percentile",
    "75th Percentile",
    "90th Percentile",
    "95th Percentile",
    "99th Percentile",
  ];

  const latencyData = experimentData.databaseConfigs.map((config) => {
    const result = experimentData.experimentResults.find(
      (res) => res.databaseConfigDetails.databaseConfigName === config.databaseConfigName
    );

    return {
      name: `${config.storeType}: ${config.databaseConfigName}`,
      read: result ? result.experimentResult.queryLatencies.map((lat) => lat.latency) : [],
      write: result ? result.experimentResult.writeLatencies.map((lat) => lat.latency) : [],
    };
  });
  console.log(latencyData);

  return (
    <div>
      <div className="overflow-x-auto rounded-lg shadow">
        <table className="min-w-full bg-white ">
          <thead className="border">
            <tr>
              <th className="text-sm py-3 ps-3 border-r text-start">
                Indicator
              </th>
              {headers.map((head, index) => (
                <th
                  key={index}
                  className="text-sm py-3 ps-3 border-r text-start"
                >
                  {head}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            <tr>
              <td className="py-2 px-4 text-xs font-semibold border-b border-r">
                Overall Query Time
              </td>
              {headers.map((header, index) => {
                const dbConfigName = header.split(": ")[1];
                return (
                  <td
                    key={index}
                    className="py-2 px-4 text-sm border-b border-r"
                  >
                    {resultMap[dbConfigName]?.overallQueryTime || "N/A"}
                  </td>
                );
              })}
            </tr>
            <tr>
              <td className="py-2 px-4 text-xs font-semibold border-b border-r">
                Overall Write Time
              </td>
              {headers.map((header, index) => {
                const dbConfigName = header.split(": ")[1];
                return (
                  <td
                    key={index}
                    className="py-2 px-4 text-sm border-b border-r"
                  >
                    {resultMap[dbConfigName]?.overallWriteTimeTaken || "N/A"}
                  </td>
                );
              })}
            </tr>
            <tr>
              <td className="py-2 px-4 text-xs font-semibold border-b border-r">
                Total Number of Calls
              </td>
              {headers.map((header, index) => {
                const dbConfigName = header.split(": ")[1];
                return (
                  <td
                    key={index}
                    className="py-2 px-4 text-sm border-b border-r"
                  >
                    {resultMap[dbConfigName]?.numberOfCalls || "N/A"}
                  </td>
                );
              })}
            </tr>
          </tbody>
        </table>
      </div>

      <div className="mt-6 overflow-x-auto rounded-t-lg shadow">
        <table className="min-w-full bg-white rounded-lg">
          <thead className="border">
            <tr>
              <th className="text-xs py-3 ps-3 border-r text-start">
                Performance Percentile
              </th>
              {percentileHeaders.map((head, i) => (
                <th key={i} className="px-2 text-xs border-r">
                  {head}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {latencyData.map((latency) => (
              <React.Fragment key={latency.name}>
                <tr>
                  <td className="py-2 px-4 text-base font-semibold border-b border-r">
                    {latency.name}
                  </td>
                </tr>
                <tr>
                  <td className="py-2 px-4 text-sm border-b border-r">
                    Read Latencies
                  </td>
                  {latency.read.map((data, i) => (
                    <td key={i} className="py-2 px-4 text-sm border-b border-r border-t">
                      {data}
                    </td>
                  ))}
                </tr>
                <tr>
                  <td className="py-2 px-4 text-sm border-b border-r">
                    Write Latencies
                  </td>
                  {latency.write.map((data, i) => (
                    <td key={i} className="py-2 px-4 text-sm border-b border-r">
                      {data}
                    </td>
                  ))}
                </tr>
              </React.Fragment>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default TableForMultiDatabase;