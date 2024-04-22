import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const useAxiosAuth = () => {
    const navigate = useNavigate();
    const apiToken = sessionStorage.getItem("token");
    console.log(apiToken)
    const api = axios.create({
        baseURL: import.meta.env.VITE_BACKEND_URL,
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${apiToken}`,
        },
    });

    useEffect(() => {
        const requestInterceptor = api.interceptors.request.use((config) => {
            if (!config.headers.Authorization) {
                config.headers.Authorization = `Bearer ${apiToken}`;
            }
            return config;
        });

        const responseInterceptor = api.interceptors.response.use(
            (response) => response,
            (error) => {
                const config = error.config;
                if (error?.response?.status === 403 && !config.sent) {
                    config.sent = true;
                    api.get("/auth/authToken").then((response) => {
                        sessionStorage.setItem("token", response.data.access);
                        config.headers.Authorization = `Bearer ${response.data.access}`;
                        return api(config);
                    }).catch((error) => {
                        console.log(error);
                        sessionStorage.removeItem("token");
                        navigate("/login");
                    });
                }

                return Promise.reject(error);
            }
        );

        return () => {
            api.interceptors.request.eject(requestInterceptor);
            api.interceptors.response.eject(responseInterceptor);
        };
    });
    return api;
};

export default useAxiosAuth;