import React, { useEffect, useState } from "react";
import { topicApi } from "../api/topicApi";
import { registrationApi } from "../api/registrationApi";
import TopicCard from "../components/TopicCard";

const STUDENT_ID = 2;

export default function StudentTopicPage() {
  const [topics, setTopics] = useState([]);
  const [loadingId, setLoadingId] = useState(null);

  useEffect(() => {
    topicApi.getAllTopics().then(setTopics).catch(console.error);
  }, []);

  const handleRegister = async (topicId) => {
    setLoadingId(topicId);
    await registrationApi.registerTopic(STUDENT_ID, topicId);
    alert("Đăng ký thành công!");
    setLoadingId(null);
  };

  return (
    <div className="container py-4">
      <h3>Danh sách đề tài</h3>

      <div className="row mt-3 g-3">
        {topics.map((t) => (
          <div key={t.id} className="col-md-4">
            <TopicCard topic={t} loading={loadingId === t.id} onRegister={handleRegister} />
          </div>
        ))}
      </div>
    </div>
  );
}
