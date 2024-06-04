// src/api/axiosInstance.js
import axios from 'axios';

// Create an instance of Axios
const axiosInstance = axios.create();

// Request interceptor
axiosInstance.interceptors.request.use(request => {
  console.log('Starting Request', {
    url: request.url,
    method: request.method,
    headers: request.headers,
    data: request.data
  });
  return request;
});

export default axiosInstance;
