import  { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaArrowLeft } from "react-icons/fa6";
import SingleDatabaseExperimentResult from "../../../components/ExperimentResults/SingleDatabaseExperimentResult";
import MultiDatabaseExperimentResult from "../../../components/ExperimentResults/MultiDatabaseExperimentResult";

const ExperimentResultPage = () => {
  const [view, setView] = useState("graph");
  const navigate = useNavigate();

  const handleView = (value) => {
    setView(value);
  };

  const experimentData = {
    name: "TPC-H Experiment",
    writeBatchSize: 100,
    experimentTimeInSeconds: 5,
    concurrentQueries: 10,
    query: {
      limitOpt: 100,
    },
    databaseConfigs: [
      {
        databaseConfigId: {
          id: 37,
        },
        databaseConfigName: "TPC-H",
        storeType: "MongoDB",
        datasetName: "First Dataset",
      },
      {
        databaseConfigId: {
          id: 38,
        },
        databaseConfigName: "TPC-C",
        storeType: "Redis",
        datasetName: "First Dataset",
      },
      {
        databaseConfigId: {
          id: 39,
        },
        databaseConfigName: "TPC-F",
        storeType: "MySQL",
        datasetName: "First Dataset",
      },
    ],
    experimentState: "Created",
    experimentResults: [
      {
        databaseConfigDetails: { databaseConfigId: { id: 1 }, databaseConfigName: "TPC-H" },
        experimentResult: {
          overallQueryTime: 120,
          overallWriteTimeTaken: 5,
          numberOfCalls: 9200,
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
            { percentile: 5, latency: 1 },
            { percentile: 10, latency: 2 },
            { percentile: 25, latency: 3 },
            { percentile: 50, latency: 3 },
            { percentile: 75, latency: 5 },
            { percentile: 90, latency: 6 },
            { percentile: 95, latency: 7 },
            { percentile: 99, latency: 9 },
          ],
        },
      },
      {
        databaseConfigDetails: { databaseConfigId: { id: 2 }, databaseConfigName: "TPC-C" },
        experimentResult: {
          overallQueryTime: 150,
          overallWriteTimeTaken: 7,
          numberOfCalls: 8500,
          queryLatencies: [
            { percentile: 5, latency: 1 },
            { percentile: 10, latency: 3 },
            { percentile: 25, latency: 3 },
            { percentile: 50, latency: 4 },
            { percentile: 75, latency: 7 },
            { percentile: 90, latency: 7 },
            { percentile: 95, latency: 9 },
            { percentile: 99, latency: 10 },
          ],
          writeLatencies: [
            { percentile: 5, latency: 1 },
            { percentile: 10, latency: 2 },
            { percentile: 25, latency: 3 },
            { percentile: 50, latency: 3 },
            { percentile: 75, latency: 5 },
            { percentile: 90, latency: 6 },
            { percentile: 95, latency: 7 },
            { percentile: 99, latency: 9 },
          ],
        },
      },
      {
        databaseConfigDetails: { databaseConfigId: { id: 3 }, databaseConfigName: "TPC-F" },
        experimentResult: {
          overallQueryTime: 180,
          overallWriteTimeTaken: 6,
          numberOfCalls: 9000,
          queryLatencies: [
            { percentile: 5, latency: 1 },
            { percentile: 10, latency: 3 },
            { percentile: 25, latency: 4 },
            { percentile: 50, latency: 4 },
            { percentile: 75, latency: 5 },
            { percentile: 90, latency: 5 },
            { percentile: 95, latency: 8 },
            { percentile: 99, latency: 9 },
          ],
          writeLatencies: [
            { percentile: 5, latency: 1 },
            { percentile: 10, latency: 2 },
            { percentile: 25, latency: 3 },
            { percentile: 50, latency: 3 },
            { percentile: 75, latency: 5 },
            { percentile: 90, latency: 6 },
            { percentile: 95, latency: 7 },
            { percentile: 99, latency: 9 },
          ],
        },
      },
    ],
    createdAt: 1718892346144,
  };

  return (
    <div className="py-8">
      <div className="ps-7 mb-5 flex items-center gap-3 -tracking-tighter">
        <FaArrowLeft
          className="cursor-pointer"
          onClick={() => navigate("/experiment")}
          size={20}
        />
        <h2 className="text-[#8e8e8e] text-xl font-semibold">
           Experiments /
        </h2>
        <h2 className="text-xl font-semibold">TPC-H Experiment</h2>
      </div>
      <div className="w-[95%] h-[1px] bg-accent my-6"></div>

      {/* <div>
        <SingleDatabaseExperimentResult
          view={view}
          handleView={handleView}
          data={experimentData}
        />
      </div> */}

      <div>
        <MultiDatabaseExperimentResult
          experimentData={experimentData}
          view={view}
          handleView={handleView}
        />
      </div>
    </div>
  );
};

export default ExperimentResultPage;
