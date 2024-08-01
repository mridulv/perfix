import { useQuery } from "react-query";
import axiosApi from "./axios";

const fetchRelevantDatabases = async (payload) => {
  const res = await axiosApi.post(`/experiment/config`, payload);
  return res.data;
};

const useRelevantDatabases = (payload, options) => {
  return useQuery(
    ["relevantDatabases", payload],
    () => fetchRelevantDatabases(payload),
    options
  );
};

export default useRelevantDatabases;
