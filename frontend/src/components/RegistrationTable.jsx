// src/components/RegistrationTable.jsx
export default function RegistrationTable({ data, onApproveClick, onRejectClick, loadingId }) {
  return (
    <table className="table table-hover">
      <thead className="table-light">
        <tr>
          <th>Reg ID</th>
          <th>MSSV</th>
          <th>Lớp</th>
          <th>Họ tên</th>
          <th>Ngày đăng ký</th>
          <th>Trạng thái</th>
          <th>Hành động</th>
        </tr>
      </thead>
      <tbody>
        {data.length === 0 && (
          <tr>
            <td colSpan="7" className="text-center text-muted">
              Chưa có sinh viên đăng ký
            </td>
          </tr>
        )}

        {data.map((r) => {
          const student = r.student;
          const user = student?.user;

          return (
            <tr key={r.id}>
              <td>{r.id}</td>
              <td>{student?.studentCode}</td>
              <td>{student?.className}</td>
              <td>{user?.fullName}</td>
              <td>{r.registeredAt}</td>
              <td>
                {r.approved ? (
                  <span className="badge bg-success">ĐÃ DUYỆT</span>
                ) : (
                  <span className="badge bg-secondary">CHƯA DUYỆT</span>
                )}
              </td>
              <td>
                <button
                  className="btn btn-success btn-sm me-1"
                  disabled={loadingId === r.id}
                  onClick={() => onApproveClick(r)}
                >
                  Duyệt
                </button>

                <button
                  className="btn btn-danger btn-sm"
                  disabled={loadingId === r.id}
                  onClick={() => onRejectClick(r)}
                >
                  Từ chối
                </button>
              </td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
}
