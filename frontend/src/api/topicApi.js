const API_BASE_URL = "http://localhost:8080/api";

export const topicApi = {
  getAllTopics: async () => {
    const res = await fetch(`${API_BASE_URL}/topics`);
    if (!res.ok) throw new Error("Không tải được danh sách đề tài");
    return res.json();
  },
};
