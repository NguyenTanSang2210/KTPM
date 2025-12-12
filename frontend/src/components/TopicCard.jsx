export default function TopicCard({ topic, onRegister, loading }) {
  const isOpen = topic.status === "OPEN";
  const status = topic.registrationStatus; // lấy trạng thái đăng ký

  // Render phần trạng thái thay cho nút nếu không phải CHƯA ĐĂNG KÝ
  const renderStatus = () => {
    switch (status) {
      case "CHO_DUYET":
        return <span className="badge bg-warning text-dark">Chờ duyệt</span>;
      case "DA_DUYET":
        return <span className="badge bg-success">Đã duyệt</span>;
      case "TU_CHOI":
        return <span className="badge bg-danger">Bị từ chối</span>;
      default:
        return (
          <button
            className="btn btn-primary mt-auto"
            disabled={!isOpen || loading}
            onClick={() => onRegister(topic.id)}
          >
            {loading ? "Đang đăng ký..." : "Đăng ký đề tài"}
          </button>
        );
    }
  };

  return (
    <div className="card shadow-sm h-100">
      <div className="card-body d-flex flex-column">
        <h5 className="card-title">{topic.title}</h5>
        <p className="text-muted small">{topic.description}</p>

        <p className="small">
          <strong>Giảng viên: </strong>
          {topic.lecturer?.user?.fullName}
        </p>

        <div className="mt-auto">{renderStatus()}</div>
      </div>
    </div>
  );
}
