import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, NavLink } from 'react-router-dom';
import { LayoutDashboard, MapPin, Users, History, BookOpen, ShieldCheck } from 'lucide-react';

// Importación de Páginas
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Areas from './pages/Areas';
import Usuarios from './pages/Usuarios';
import Historial from './pages/Historial';
import Carreras from './pages/Carreras.jsx';
import Roles from './pages/Roles';

// Componente para organizar el Layout con Sidebar
function AdminLayout({ children }) {
    return (
        <div className="d-flex">
            {/* SIDEBAR FIXED */}
            <div className="sidebar-container" style={{ width: '280px', position: 'fixed', height: '100vh' }}>
                <div className="p-4">
                    <h4 className="fw-bold m-0 text-white">Reserva Deportiva</h4>
                    <small className="text-muted">Panel de Administración</small>
                </div>

                <nav className="mt-4">
                    <NavLink to="/dashboard" className={({ isActive }) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                        <LayoutDashboard size={20} /> Dashboard
                    </NavLink>
                    <NavLink to="/areas" className={({ isActive }) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                        <MapPin size={20} /> Áreas deportivas
                    </NavLink>
                    <NavLink to="/usuarios" className={({ isActive }) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                        <Users size={20} /> Usuarios
                    </NavLink>
                    <NavLink to="/historial" className={({ isActive }) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                        <History size={20} /> Historial
                    </NavLink>

                    {/* Sección de Catálogos */}
                    <div className="mt-4 px-4">
                        <small className="text-muted text-uppercase fw-bold" style={{ fontSize: '0.7rem'}}>Catálogos</small>
                    </div>
                    <NavLink to="/catalogos/carreras" className={({ isActive }) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                        <BookOpen size={20} /> Carreras
                    </NavLink>
                    <NavLink to="/catalogos/roles" className={({ isActive }) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                        <ShieldCheck size={20} /> Roles
                    </NavLink>
                </nav>

                <div className="position-absolute bottom-0 w-100 p-4 border-top border-secondary">
                    <div className="d-flex align-items-center gap-2">
                        <div className="bg-secondary rounded-circle p-2 px-3 fw-bold text-white">AD</div>
                        <div>
                            <p className="m-0 small fw-bold text-white">Administrador</p>
                            <p className="m-0 text-muted" style={{ fontSize: '11px' }}>admin@utez.edu.mx</p>
                        </div>
                    </div>
                </div>
            </div>

            {/* CONTENIDO PRINCIPAL */}
            <div style={{ marginLeft: '280px', width: '100%', minHeight: '100vh' }} className="p-5">
                {children}
            </div>
        </div>
    );
}

function App() {
    return (
        <Router>
            <Routes>
                {/* Ruta de Login: Sin Sidebar */}
                <Route path="/login" element={<Login />} />

                {/* Rutas de Administración: Con Sidebar */}
                <Route path="/dashboard" element={<AdminLayout><Dashboard /></AdminLayout>} />
                <Route path="/areas" element={<AdminLayout><Areas /></AdminLayout>} />
                <Route path="/usuarios" element={<AdminLayout><Usuarios /></AdminLayout>} />
                <Route path="/historial" element={<AdminLayout><Historial /></AdminLayout>} />

                {/* Nuevas rutas de catálogos */}
                <Route path="/catalogos/carreras" element={<AdminLayout><Carreras /></AdminLayout>} />
                <Route path="/catalogos/roles" element={<AdminLayout><Roles /></AdminLayout>} />

                {/* Redirección por defecto al Login */}
                <Route path="*" element={<Navigate to="/login" />} />
            </Routes>
        </Router>
    );
}

export default App;