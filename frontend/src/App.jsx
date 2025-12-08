// src/App.jsx
import "./App.css";
import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import StudentTopicsPage from "./pages/StudentTopicPage";
import LecturerRegistrationsPage from "./pages/LecturerRegistrationPage";

export default function App() {
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
                <Link className="nav-link" to="/lecturer">
                  Giảng viên
                </Link>
              </li>
            </ul>
          </div>
        </div>
      </nav>

      <Routes>
        <Route path="/" element={<StudentTopicsPage />} />
        <Route path="/lecturer" element={<LecturerRegistrationsPage />} />
      </Routes>
    </BrowserRouter>
  );
}
