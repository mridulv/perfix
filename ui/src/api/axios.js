import axios from "axios";

const axiosApi = axios.create({
  withCredentials: true,
  baseURL: import.meta.env.VITE_BASE_URL,
});

export default axiosApi;
