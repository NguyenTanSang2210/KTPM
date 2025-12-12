// src/pages/LecturerRegistrationsPage.jsx
import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { registrationApi } from "../api/registrationApi";
import RegistrationTable from "../components/RegistrationTable";

export default function LecturerRegistrationPage() {
  const location = useLocation();
  const [topicId, setTopicId] = useState("1");
  const [data, setData] = useState([]);
  const [loadingId, setLoadingId] = useState(null);
  const [rejectReason, setRejectReason] = useState("");
  const [search, setSearch] = useState("");
  const [sortBy, setSortBy] = useState("registered_desc");

  // state cho modal
  const [showModal, setShowModal] = useState(false);
  const [modalAction, setModalAction] = useState(null); // "approve" | "reject"
  const [selectedReg, setSelectedReg] = useState(null);

  const load = async () => {
    const result = await registrationApi.getByTopic(topicId);
    setData(result);
  };

  // Đọc topicId từ query string khi mở trang hoặc khi thay đổi URL
  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const qId = params.get("topicId");
    if (qId) {
      setTopicId(qId);
    }
  }, [location.search]);

  // mở modal duyệt
  const openApproveModal = (reg) => {
    setSelectedReg(reg);
    setModalAction("approve");
    setShowModal(true);
  };

  // mở modal từ chối
  const openRejectModal = (reg) => {
    setSelectedReg(reg);
    setModalAction("reject");
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setSelectedReg(null);
    setModalAction(null);
  };

  const handleConfirm = async () => {
    if (!selectedReg || !modalAction) return;

    setLoadingId(selectedReg.id);

    try {
      if (modalAction === "approve") {
        await registrationApi.approve(selectedReg.id);
      } else {
        await registrationApi.reject(selectedReg.id, rejectReason);
      }

      await load(); // reload danh sách
    } catch (e) {
      console.error(e);
      alert("Có lỗi xảy ra. Vui lòng thử lại.");
    } finally {
      setLoadingId(null);
      closeModal();
    }
  };

  // Dữ liệu sau khi lọc và sắp xếp
  const filteredData = React.useMemo(() => {
    const q = search.trim().toLowerCase();
    let arr = data || [];
    if (q) {
      arr = arr.filter((r) => {
        const student = r.student || {};
        const user = student.user || {};
        return (
          String(r.id).includes(q) ||
          (student.studentCode || "").toLowerCase().includes(q) ||
          (student.className || "").toLowerCase().includes(q) ||
          (user.fullName || "").toLowerCase().includes(q)
        );
      });
    }

    const cmp = {
      registered_desc: (a, b) => new Date(b.registeredAt) - new Date(a.registeredAt),
      registered_asc: (a, b) => new Date(a.registeredAt) - new Date(b.registeredAt),
      studentCode_asc: (a, b) => String(a.student?.studentCode || "").localeCompare(String(b.student?.studentCode || "")),
      fullName_asc: (a, b) => String(a.student?.user?.fullName || "").localeCompare(String(b.student?.user?.fullName || "")),
    };
    const sorter = cmp[sortBy] || cmp.registered_desc;
    return [...arr].sort(sorter);
  }, [data, search, sortBy]);

  // Tiêu đề đề tài và thống kê nhanh
  const topicTitle = React.useMemo(() => {
    const t = data?.[0]?.topic;
    return t?.title ? t.title : `Đề tài #${topicId}`;
  }, [data, topicId]);
  const stats = React.useMemo(() => {
    const total = data.length;
    let pending = 0, approved = 0, rejected = 0;
    data.forEach((r) => {
      if (r.approved === null) pending++;
      else if (r.approved === true) approved++;
      else rejected++;
    });
    return { total, pending, approved, rejected };
  }, [data]);

  const exportCsv = () => {
    const rows = [
      [
        "Reg ID",
        "MSSV",
        "Lớp",
        "Họ tên",
        "Ngày đăng ký",
        "Người xử lý",
        "Thời gian xử lý",
        "Trạng thái",
        "Lý do từ chối",
      ],
      ...filteredData.map((r) => {
        const student = r.student || {};
        const user = student.user || {};
        const stt = r.approved === null ? "CHUA_DUYET" : r.approved ? "DA_DUYET" : "TU_CHOI";
        const dateStr = new Date(r.registeredAt).toLocaleString();
        const reviewerName = r.reviewer?.user?.fullName || "";
        const reviewedStr = r.reviewedAt ? new Date(r.reviewedAt).toLocaleString() : "";
        return [
          r.id,
          student.studentCode || "",
          student.className || "",
          user.fullName || "",
          dateStr,
          reviewerName,
          reviewedStr,
          stt,
          r.rejectReason || "",
        ];
      }),
    ];

    const csv = rows
      .map((row) => row.map((cell) => `"${String(cell).replace(/"/g, '""')}"`).join(","))
      .join("\r\n");
    const blob = new Blob(["\ufeff" + csv], { type: "text/csv;charset=utf-8;" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `dang-ky-topic-${topicId}.csv`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };

  return (
    <div className="container py-4">
      <h3>Giảng viên duyệt đăng ký đề tài</h3>
      <div className="mt-2">
        <strong>Đề tài:</strong> {topicTitle}
        <span className="ms-3 badge bg-dark" title="Tổng">{stats.total}</span>
        <span className="ms-1 badge bg-warning" title="Chờ">{stats.pending}</span>
        <span className="ms-1 badge bg-success" title="Duyệt">{stats.approved}</span>
        <span className="ms-1 badge bg-danger" title="Từ chối">{stats.rejected}</span>
      </div>

      <div className="row mb-3">
        <div className="col-md-3">
          <input
            className="form-control"
            value={topicId}
            onChange={(e) => setTopicId(e.target.value)}
          />
        </div>
        <div className="col-md-2">
          <button className="btn btn-primary" onClick={load}>
            Tải danh sách
          </button>
        </div>
        <div className="col-md-2">
          <button className="btn btn-outline-secondary" onClick={exportCsv}>
            Xuất CSV
          </button>
        </div>
        <div className="col-md-4">
          <input
            className="form-control"
            placeholder="Tìm theo MSSV, Họ tên, Lớp..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <div className="col-md-3">
          <select
            className="form-select"
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value)}
          >
            <option value="registered_desc">Đăng ký mới nhất</option>
            <option value="registered_asc">Đăng ký cũ nhất</option>
            <option value="studentCode_asc">MSSV A → Z</option>
            <option value="fullName_asc">Họ tên A → Z</option>
          </select>
        </div>
      </div>

      <RegistrationTable
        data={filteredData}
        loadingId={loadingId}
        onApproveClick={openApproveModal}
        onRejectClick={openRejectModal}
      />

      {/* Modal xác nhận đơn giản bằng React + CSS */}
      {showModal && selectedReg && (
        <div className="custom-modal-backdrop">
          <div className="custom-modal">
            <h5 className="mb-3">
              {modalAction === "approve" ? "Xác nhận duyệt" : "Xác nhận từ chối"}
            </h5>

            <p className="mb-1">
              Sinh viên: <strong>{selectedReg.student?.user?.fullName}</strong>
            </p>
            <p className="mb-1">
              MSSV: <strong>{selectedReg.student?.studentCode}</strong>
            </p>
            <p className="mb-3">
              Bạn có chắc muốn{" "}
              <strong>{modalAction === "approve" ? "DUYỆT" : "TỪ CHỐI"}</strong> đăng ký này
              không?
            </p>

            {modalAction === "reject" && (
              <div className="mb-3">
                <label className="form-label">Lý do từ chối</label>
                <textarea
                  className="form-control"
                  rows={3}
                  value={rejectReason}
                  onChange={(e) => setRejectReason(e.target.value)}
                  placeholder="Nhập lý do từ chối (tùy chọn)"
                />
              </div>
            )}

            <div className="text-end">
              <button className="btn btn-secondary me-2" onClick={closeModal}>
                Hủy
              </button>
              <button
                className={
                  modalAction === "approve" ? "btn btn-success" : "btn btn-danger"
                }
                onClick={handleConfirm}
              >
                Xác nhận
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
