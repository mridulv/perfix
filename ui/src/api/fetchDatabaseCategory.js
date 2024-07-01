import axiosApi from "./axios";

const fetchDatabaseCategory = async (setDatabaseCategories) => {
  try {
    const res = await axiosApi.get("/databases/categories");
    if (res.status === 200) {
      setDatabaseCategories(res.data);
    }
  } catch (error) {
    console.error("Error fetching database categories:", error);
  }
};

export default fetchDatabaseCategory;
