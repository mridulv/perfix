import axiosApi from "./axios";

const fetchExperimentData = async (id, setExperimentData, setLoading) => {
  setLoading(true);
  try {
    const res = await axiosApi.get(`/experiment/${id}`);
    const data = await res.data;

    if (res.status === 200) {
      setExperimentData(data);
      setLoading(false);
    }
  } catch (error) {
    console.error("Error fetching database types:", error);
  }
};

export default fetchExperimentData;
