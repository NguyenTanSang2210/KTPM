import React from "react";
import { Navigate } from "react-router-dom";

export default function RequireRole({ role, children }) {
  const user = (() => {
    try {
      const raw = localStorage.getItem("user");
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  })();

  if (!user || user.role !== role) {
    return <Navigate to="/login" replace />;
  }

  return children;
}

