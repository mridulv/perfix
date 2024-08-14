import toast from "react-hot-toast";
import axiosApi from "./axios";

const handleAddExperiment = async (
  experimentName,
  writeBatchSizeValue,
  concurrentQueries,
  experimentTimeInSecond,
  selectedDatabases,
  dbQuery,
  navigate,
) => {


  const values = {
    name: experimentName,
    writeBatchSize: Number(writeBatchSizeValue),
    concurrentQueries: Number(concurrentQueries),
    experimentTimeInSeconds: Number(experimentTimeInSecond),
    dbQuery,
    databaseConfigs: selectedDatabases.map((database) => ({
      databaseConfigId: { id: database.value },
    })),
  };

  if (selectedDatabases.length < 0)
    return toast.error("Please select a database.");

  const res = await axiosApi.post("/experiment/create", values);
  if (res.status === 200) {
    navigate("/experiment");
    toast.success("Experiment created successfully!");
  }
};
const handleUpdateExperiment = async (
  experimentId,
  experimentName,
  writeBatchSizeValue,
  concurrentQueries,
  experimentTimeInSecond,
  selectedDatabases,
  dbQuery,
  navigate,
) => {


  const values = {
    name: experimentName,
    writeBatchSize: Number(writeBatchSizeValue),
    concurrentQueries: Number(concurrentQueries),
    experimentTimeInSeconds: Number(experimentTimeInSecond),
    dbQuery,
    databaseConfigs: selectedDatabases.map((database) => ({
      databaseConfigId: { id: database.value },
    })),
  };

  if (selectedDatabases.length < 0)
    return toast.error("Please select a database.");

  const res = await axiosApi.post(`/experiment/${experimentId}`, values);
  if (res.status === 200) {
    navigate("/experiment");
    toast.success("Experiment updated successfully!");
  }
};

export const handleExperiment = {
  handleAddExperiment,
  handleUpdateExperiment
};
