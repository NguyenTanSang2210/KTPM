export default function TopicCard({ topic, onRegister, loading }) {
  const isOpen = topic.status === "OPEN";

  return (
    <div className="card shadow-sm h-100">
      <div className="card-body d-flex flex-column">
        <h5 className="card-title">{topic.title}</h5>
        <p className="text-muted small">{topic.description}</p>

        <p className="small">
          <strong>Giảng viên: </strong>
          {topic.lecturer?.user?.fullName}
        </p>

        <button
          className="btn btn-primary mt-auto"
          disabled={!isOpen || loading}
          onClick={() => onRegister(topic.id)}
        >
          {loading ? "Đang đăng ký..." : "Đăng ký đề tài"}
        </button>
      </div>
    </div>
  );
}
