import React from "react";

const RunExperimentsDetails = ({data}) => {
  

  const tableStyle = {
    borderCollapse: 'collapse',
    width:"40%",
    marginBottom: '20px',
  };
  const tableStyleForSecond = {
    borderCollapse: 'collapse',
    width: '100%',
    marginBottom: '20px',
  };

  const thTdStyle = {
    border: '1px solid black',
    padding: '8px',
    textAlign: 'center',
  };

  return (
    <div className="transition-all duration-1000 ease-in-out experiment-details">
      <h1>UX for Experiment Result</h1>
      <table className="experiment-details" style={tableStyle}>
        <tbody>
          <tr>
            <td style={thTdStyle}>Overall Query Time</td>
            <td style={thTdStyle}>{data.overallQueryTime}</td>
          </tr>
          <tr>
            <td style={thTdStyle}>Overall Write Time</td>
            <td style={thTdStyle}>{data.overallWriteTimeTaken}</td>
          </tr>
          <tr>
            <td style={thTdStyle}>Total Number of Calls</td>
            <td style={thTdStyle}>{data.numberOfCalls}</td>
          </tr>
          <tr>
            <td style={thTdStyle}>Errors</td>
            <td style={thTdStyle}>0</td>
          </tr>
        </tbody>
      </table>
      <table className="experiment-details" style={tableStyleForSecond}>
        <thead>
          <tr>
            <th style={thTdStyle}>Performance Percentiles</th>
            <th style={thTdStyle}>5th Percentile</th>
            <th style={thTdStyle}>10th Percentile</th>
            <th style={thTdStyle}>25th Percentile</th>
            <th style={thTdStyle}>50th Percentile</th>
            <th style={thTdStyle}>75th Percentile</th>
            <th style={thTdStyle}>90th Percentile</th>
            <th style={thTdStyle}>95th Percentile</th>
            <th style={thTdStyle}>99th Percentile</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td style={thTdStyle}>Read Latencies</td>
            {data.queryLatencies.map((latency) => (
              <td key={latency.percentile} style={thTdStyle}>{latency.latency}</td>
            ))}
          </tr>
          <tr>
            <td style={thTdStyle}>Write Latencies</td>
            {data.writeLatencies.map((latency) => (
              <td key={latency.percentile} style={thTdStyle}>{latency.latency}</td>
            ))}
          </tr>
        </tbody>
      </table>
    </div>
  );
};

export default RunExperimentsDetails;
