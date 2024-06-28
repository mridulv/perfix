import { useQuery } from "react-query";
import axiosApi from "./axios";

const fetchDatasets = async (payload) => {
  const res = await axiosApi.post("/dataset", payload);
  return res.data;
};

const useDatasets = (payload) => {
  return useQuery(["datasets", payload], () => fetchDatasets(payload));
};

export default useDatasets;
