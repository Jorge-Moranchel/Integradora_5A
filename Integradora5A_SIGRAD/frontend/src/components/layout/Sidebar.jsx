import React, { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import {
    LayoutDashboard, MapPin, Users, History,
    BookOpen, ShieldCheck, ChevronDown, ChevronRight, LogOut, Building2
} from 'lucide-react';
import Swal from 'sweetalert2';

export default function Sidebar() {
    const [showCatalogos, setShowCatalogos] = useState(false);
    const navigate = useNavigate();

    const userData = JSON.parse(localStorage.getItem('user') || '{}');
    const nombreCompleto = userData.nombre || userData.name || userData.nombreCompleto || 'Administrador';
    const correo = userData.emailInstitucional || userData.email || userData.correo || 'admin@utez.edu.mx';
    const iniciales = nombreCompleto
        .split(' ')
        .slice(0, 2)
        .map(n => n[0]?.toUpperCase() || '')
        .join('');

    const handleLogout = () => {
        Swal.fire({
            title: '¿Cerrar sesión?',
            text: "Tendrás que ingresar de nuevo al sistema.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#2563eb',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Sí, salir',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                localStorage.removeItem('user');
                navigate('/login');
            }
        });
    };

    return (
        <div className="sidebar-container shadow-lg">
            <div className="p-4 mb-2">
                <h4 className="fw-bold m-0 text-white">RESERVA DEPORTIVA</h4>
                <div className="d-flex align-items-center gap-2 mt-2">
                    <span className="status-dot-online"></span>
                    <small className="text-white fw-bold">Panel de Administración</small>
                </div>
            </div>

            <nav className="nav flex-column gap-1 flex-grow-1 px-3">
                <NavLink to="/dashboard" className={({ isActive }) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                    <LayoutDashboard size={20} /> <span>Dashboard</span>
                </NavLink>
                <NavLink to="/areas" className={({ isActive }) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                    <MapPin size={20} /> <span>Áreas deportivas</span>
                </NavLink>
                <NavLink to="/usuarios" className={({ isActive }) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                    <Users size={20} /> <span>Usuarios</span>
                </NavLink>
                <NavLink to="/historial" className={({ isActive }) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                    <History size={20} /> <span>Historial</span>
                </NavLink>

                <div className="mt-4">
                    <div
                        className="catalog-header-pro d-flex align-items-center justify-content-between p-2 rounded-3"
                        onClick={() => setShowCatalogos(!showCatalogos)}
                        style={{ cursor: 'pointer', background: 'rgba(255,255,255,0.05)' }}
                    >
                        <small className="text-white fw-bold">CATÁLOGOS</small>
                        {showCatalogos ? <ChevronDown size={16} /> : <ChevronRight size={16} />}
                    </div>

                    {showCatalogos && (
                        <div className="ps-3 d-flex flex-column gap-1 mt-2">
                            <NavLink to="/catalogos/carreras" className={({ isActive }) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                                <BookOpen size={18} /> <span>Carreras</span>
                            </NavLink>
                            <NavLink to="/catalogos/roles" className={({ isActive }) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                                <ShieldCheck size={18} /> <span>Roles</span>
                            </NavLink>
                            <NavLink to="/catalogos/divisiones" className={({ isActive }) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                                <Building2 size={18} /> <span>Divisiones</span>
                            </NavLink>
                        </div>
                    )}
                </div>
            </nav>

            <div className="p-3 border-top border-secondary border-opacity-25 mt-auto">
                <div className="profile-box d-flex align-items-center justify-content-between p-2 rounded-3 bg-white bg-opacity-10">
                    <div className="d-flex align-items-center gap-2 overflow-hidden">
                        <div className="avatar-letter">{iniciales || 'AD'}</div>
                        <div className="overflow-hidden text-start">
                            <p className="m-0 small fw-bold text-white text-truncate">{nombreCompleto}</p>
                            <p className="m-0 text-white extra-small opacity-50 text-truncate">{correo}</p>
                        </div>
                    </div>
                    <button
                        onClick={handleLogout}
                        title="Cerrar Sesión"
                        style={{
                            background: 'rgba(239, 68, 68, 0.2)',
                            border: '1px solid rgba(239, 68, 68, 0.4)',
                            borderRadius: '8px',
                            padding: '6px 8px',
                            color: '#f87171',
                            cursor: 'pointer',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            transition: '0.2s',
                            flexShrink: 0
                        }}
                        onMouseEnter={e => e.currentTarget.style.background = 'rgba(239, 68, 68, 0.4)'}
                        onMouseLeave={e => e.currentTarget.style.background = 'rgba(239, 68, 68, 0.2)'}
                    >
                        <LogOut size={18} />
                    </button>
                </div>
            </div>
        </div>
    );
}