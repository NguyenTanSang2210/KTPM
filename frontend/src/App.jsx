// src/App.jsx
import "./App.css";
import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import StudentTopicsPage from "./pages/StudentTopicPage";
import StudentMyRegistrationsPage from "./pages/StudentMyRegistrationsPage";
import LecturerRegistrationsPage from "./pages/LecturerRegistrationPage";
import LecturerTopicsPage from "./pages/LecturerTopicsPage";
import LoginPage from "./pages/LoginPage";
import RequireRole from "./components/RequireRole";

export default function App() {
  const user = (() => {
    try {
      const raw = localStorage.getItem("user");
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  })();

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    window.location.assign("/login");
  };
  return (
    <BrowserRouter>
      <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
        <div className="container">
          <Link className="navbar-brand fw-bold" to="/">
            Đề tài LTMMT
          </Link>
          <div className="collapse navbar-collapse show">
            <ul className="navbar-nav me-auto">
              <li className="nav-item">
                <Link className="nav-link" to="/">
                  Sinh viên
                </Link>
              </li>
              <li className="nav-item">
                <Link className="nav-link" to="/my-registrations">
                  Đăng ký của tôi
                </Link>
              </li>
              <li className="nav-item">
                <Link className="nav-link" to="/lecturer">
                  Giảng viên
                </Link>
              </li>
              <li className="nav-item">
                <Link className="nav-link" to="/lecturer-topics">
                  Quản lý đề tài
                </Link>
              </li>
            </ul>
            <div className="d-flex">
              {user ? (
                <>
                  <span className="text-light me-2 small">
                    {user.fullName} ({user.role})
                  </span>
                  <button className="btn btn-outline-light btn-sm" onClick={handleLogout}>
                    Đăng xuất
                  </button>
                </>
              ) : (
                <Link className="btn btn-outline-light btn-sm" to="/login">
                  Đăng nhập
                </Link>
              )}
            </div>
          </div>
        </div>
      </nav>

      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/" element={<StudentTopicsPage />} />
        <Route
          path="/my-registrations"
          element={
            <RequireRole role="STUDENT">
              <StudentMyRegistrationsPage />
            </RequireRole>
          }
        />
        <Route
          path="/lecturer"
          element={
            <RequireRole role="LECTURER">
              <LecturerRegistrationsPage />
            </RequireRole>
          }
        />
        <Route
          path="/lecturer-topics"
          element={
            <RequireRole role="LECTURER">
              <LecturerTopicsPage />
            </RequireRole>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}
