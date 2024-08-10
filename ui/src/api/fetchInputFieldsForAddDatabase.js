import { useQuery } from "react-query";
import axiosApi from "./axios";

const fetchInputFields = async (selectedDatabaseType) => {
  if(selectedDatabaseType === "") return;

  const res = await axiosApi.get(
    `/databases/inputs?databaseType=${selectedDatabaseType}`
  );
  return res.data;
};

const useInputFields = (selectedDatabaseType) => {
  return useQuery(["inputFields", selectedDatabaseType], () =>
    fetchInputFields(selectedDatabaseType)
  );
};

export default useInputFields;
