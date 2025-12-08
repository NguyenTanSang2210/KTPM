const API_BASE_URL = "http://localhost:8080/api";

export const registrationApi = {
  registerTopic: async (studentId, topicId) => {
    const res = await fetch(
      `${API_BASE_URL}/registration/register?studentId=${studentId}&topicId=${topicId}`,
      { method: "POST" }
    );
    if (!res.ok) throw new Error("Đăng ký đề tài thất bại");
    return res.json();
  },

  getByTopic: async (topicId) => {
    const res = await fetch(`${API_BASE_URL}/registration/topic/${topicId}`);
    if (!res.ok) throw new Error("Không tải được danh sách đăng ký");
    return res.json();
  },

  approve: async (regId) => {
    const res = await fetch(`${API_BASE_URL}/registration/approve/${regId}`, {
      method: "POST",
    });
    if (!res.ok) throw new Error("Duyệt thất bại");
    return res.json();
  },

  reject: async (regId) => {
    const res = await fetch(`${API_BASE_URL}/registration/reject/${regId}`, {
      method: "POST",
    });
    if (!res.ok) throw new Error("Từ chối thất bại");
    return res.json();
  },
};
