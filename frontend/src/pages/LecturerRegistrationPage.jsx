// src/pages/LecturerRegistrationsPage.jsx
import React, { useState } from "react";
import { registrationApi } from "../api/registrationApi";
import RegistrationTable from "../components/RegistrationTable";

export default function LecturerRegistrationPage() {
  const [topicId, setTopicId] = useState("1");
  const [data, setData] = useState([]);
  const [loadingId, setLoadingId] = useState(null);

  // state cho modal
  const [showModal, setShowModal] = useState(false);
  const [modalAction, setModalAction] = useState(null); // "approve" | "reject"
  const [selectedReg, setSelectedReg] = useState(null);

  const load = async () => {
    const result = await registrationApi.getByTopic(topicId);
    setData(result);
  };

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
        await registrationApi.reject(selectedReg.id);
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

  return (
    <div className="container py-4">
      <h3>Giảng viên duyệt đăng ký đề tài</h3>

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
      </div>

      <RegistrationTable
        data={data}
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
