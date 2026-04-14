import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Areas from './pages/Areas';
import Usuarios from './pages/Usuarios';
import Historial from './pages/Historial';
import Carreras from './pages/Carreras.jsx';
import Roles from './pages/Roles';
import Divisiones from './pages/Divisiones.jsx';
import Sidebar from './components/layout/Sidebar';
// ✅ IMPORTAMOS EL GUARDIÁN
import ProtectedRoute from './components/config/ProtectedRoute';

function AdminLayout({ children }) {
    return (
        // ✅ ENVOLVEMOS TODO EL LAYOUT CON EL GUARDIÁN
        // Si no hay token, el usuario nunca verá el Sidebar ni el contenido
        <ProtectedRoute>
            <div className="d-flex">
                <div style={{ width: '280px', position: 'fixed', height: '100vh', zIndex: 100 }}>
                    <Sidebar />
                </div>
                <div style={{ marginLeft: '280px', width: '100%', minHeight: '100vh' }} className="p-5">
                    {children}
                </div>
            </div>
        </ProtectedRoute>
    );
}

function App() {
    return (
        <Router>
            <Routes>
                {/* RUTAS PÚBLICAS */}
                <Route path="/login" element={<Login />} />

                {/* RUTAS PROTEGIDAS (AdminLayout hace la magia) */}
                <Route path="/dashboard" element={<AdminLayout><Dashboard /></AdminLayout>} />
                <Route path="/areas" element={<AdminLayout><Areas /></AdminLayout>} />
                <Route path="/usuarios" element={<AdminLayout><Usuarios /></AdminLayout>} />
                <Route path="/historial" element={<AdminLayout><Historial /></AdminLayout>} />
                <Route path="/catalogos/carreras" element={<AdminLayout><Carreras /></AdminLayout>} />
                <Route path="/catalogos/roles" element={<AdminLayout><Roles /></AdminLayout>} />
                <Route path="/catalogos/divisiones" element={<AdminLayout><Divisiones /></AdminLayout>} />

                {/* Si pone una ruta que no existe, lo mandamos al login */}
                <Route path="*" element={<Navigate to="/login" />} />
            </Routes>
        </Router>
    );
}

export default App;