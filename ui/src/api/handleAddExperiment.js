import toast from "react-hot-toast";
import axiosApi from "./axios";

const handleAddExperiment = async (
  form,
  writeBatchSizeValue,
  concurrentQueries,
  experimentTimeInSecond,
  selectedDatabases,
  navigate,
) => {
  const name = form.name.value;

  const limitOpt = Number(form.limitOpt.value);

  const values = {
    name,
    writeBatchSize: Number(writeBatchSizeValue),
    concurrentQueries: Number(concurrentQueries),
    experimentTimeInSeconds: Number(experimentTimeInSecond),
    query: {
      limitOpt,
    },
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

export default handleAddExperiment;