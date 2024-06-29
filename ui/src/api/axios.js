import axios from 'axios';

const axiosApi = axios.create({
  withCredentials: true,
  baseURL: process.env.REACT_APP_BASE_URL,
  timeout: 5000,
});

export default axiosApi;