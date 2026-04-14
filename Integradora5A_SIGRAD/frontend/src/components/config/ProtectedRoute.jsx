import React from 'react';
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children }) => {
    // Revisa si existe el pase en la memoria del navegador
    const isAuthenticated = localStorage.getItem('adminToken') !== null;

    if (!isAuthenticated) {
        // Si no está logueado, lo patea a la pantalla de login
        return <Navigate to="/login" replace />;
    }

    return children;
};

export default ProtectedRoute;