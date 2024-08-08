import axiosApi from "./axios";

const fetchDatabaseData = async (id, setDatasetData, setLoading) => {
    setLoading(true)
  try {
    const res = await axiosApi.get(`/dataset/${id}/data`);
    const data = await res.data.datasets;

    if (res.status === 200) {
      setDatasetData(data);
      setLoading(false)
    }
  } catch (error) {
    console.error("Error fetching database types:", error);
  }
};

export default fetchDatabaseData;
