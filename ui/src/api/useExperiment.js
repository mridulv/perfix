import { useQuery } from "react-query";
import axiosApi from "./axios";

const fetchExperiments = async (payload) => {
  const res = await axiosApi.post("/experiment", payload)
  return res.data;
};

const useExperiments = (payload) => {
  return useQuery(["experiments", payload], () => fetchExperiments(payload));
};

export default useExperiments;