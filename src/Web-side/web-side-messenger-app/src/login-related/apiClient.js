import axios from "axios";
import Cookies from "js-cookie";

const apiClient = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true,
    headers: {'Content-Type': 'application/json'}
});

apiClient.interceptors.request.use(config => {
    const token = Cookies.get('XSRF-TOKEN');
    if (token) {
        config.headers['X-XSRF-TOKEN'] = token;
    }
    return config;
}, error => Promise.reject(error));

export default apiClient;