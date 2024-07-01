import { useQuery } from "react-query";
import axiosApi from "./axios";

const fetchDatabases = async (payload) => {
  const res = await axiosApi.post('/config', payload)
  return res.data;
};

const useDatabases = (payload) => {
  return useQuery(["databases", payload], () => fetchDatabases(payload));
};

export default useDatabases;