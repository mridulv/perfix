import axiosApi from "./axios";

const fetchDatabaseTypes = async (setDatabaseTypes) => {
  try {
    const res = await axiosApi.get("/databases");
    const data = res.data;

    if (res.status === 200) {
      setDatabaseTypes(data);
    }
  } catch (error) {
    console.error("Error fetching database types:", error);
  }
};

export default fetchDatabaseTypes;
