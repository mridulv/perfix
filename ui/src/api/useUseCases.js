import { useQuery } from "react-query";
import axiosApi from "./axios";

const fetchUseCases = async (payload) => {
  const res = await axiosApi.post("/usecases", payload)
  return res.data;
};

const useUseCases = (payload) => {
  return useQuery(["use cases", payload], () => fetchUseCases(payload));
};

export default useUseCases;