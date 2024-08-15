import axiosApi from "./axios";

const fetchDatasetData = async (id, setDatasetData, setLoading) => {
  setLoading(true);
  try {
    const res = await axiosApi.get(`/dataset/${id}/data`);
    const data = await res.data.datasets;

    if (res.status === 200) {
      setDatasetData(data);
      setLoading(false);
    }
  } catch (error) {
    console.error("Error fetching database data", error);
  }
};

export default fetchDatasetData;
