import api from "../api";

export const authApi = {
  login: async (username, password) => {
    const res = await api.post("/auth/login", { username, password });
    return res.data; // { token, userId, username, fullName, role }
  },
};

