import axiosApi from "./axios";

const fetchDatasetColumnTypes = async (setFunction) => {
  try {
    const res = await axiosApi.get("/dataset/columnTypes");
    if (res.status === 200) {
        setFunction(res.data);
    }
  } catch (error) {
    console.error("Error fetching database categories:", error);
  }
};

export default fetchDatasetColumnTypes;
