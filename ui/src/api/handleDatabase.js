import toast from "react-hot-toast";
import axiosApi from "./axios";

const handleAddDatabase = async (values, creationFor, successFunction) => {
  try {
    const res = await axiosApi.post("/config/create", values);
    if (res.status === 200) {
      if (creationFor === "database") {
        successFunction();
      } else {
        successFunction(res);
      }
    } else {
      toast.error("Failed to add database.");
    }
  } catch (error) {
    toast.error("An error occurred. Please try again.");
  }
};
const handleUpdateDatabase = async (databaseId, values, successFunction) => {
  try {
    const res = await axiosApi.post(`/config/${databaseId}`, values);
    if (res.status === 200) {
      successFunction();
    } else {
      toast.error("Failed to update database.");
    }
  } catch (error) {
    toast.error("An error occurred. Please try again.");
  }
};

export const handleDatabase = {
  handleAddDatabase,
  handleUpdateDatabase,
};
