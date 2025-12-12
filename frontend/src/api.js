// src/api.js
import axios from "axios";

const api = axios.create({
  baseURL: "/api",
});

// Chuẩn hóa xử lý lỗi
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error?.response?.status;
    const url = error?.config?.url || "";
    // Không redirect đối với các endpoint auth (đặc biệt /auth/login)
    const isAuthEndpoint = url.includes("/auth/");

    if (!isAuthEndpoint && (status === 401 || status === 403)) {
      try {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
      } catch {}
      if (window.location?.pathname !== "/login") {
        window.location.assign("/login");
      }
    }
    const message =
      error?.response?.data?.message ||
      error?.response?.data?.error ||
      error?.message ||
      "Yêu cầu thất bại";
    return Promise.reject(new Error(message));
  }
);

// Đính kèm Authorization nếu có token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers = config.headers || {};
    config.headers["Authorization"] = `Bearer ${token}`;
  }
  return config;
});

export default api;
