import React, { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import {
    LayoutDashboard, MapPin, Users, History,
    BookOpen, ShieldCheck, ChevronDown, ChevronRight, LogOut
} from 'lucide-react';
import Swal from 'sweetalert2';

export default function Sidebar() {
    // ESTADO: Empieza en false (CERRADO) para que no se vea nada al cargar
    const [showCatalogos, setShowCatalogos] = useState(false);
    const navigate = useNavigate();

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
            if (result.isConfirmed) navigate('/login');
        });
    };

    return (
        <div className="sidebar-container shadow-lg">
            {/* LOGO */}
            <div className="p-4 mb-2">
                <h4 className="fw-bold m-0 text-white">RESERVA DEPORTIVA</h4>
                <div className="d-flex align-items-center gap-2 mt-2">
                    <span className="status-dot-online"></span>
                    <small className="text-white fw-bold">Panel de Administración</small>
                </div>
            </div>

            {/* NAV PRINCIPAL */}
            <nav className="nav flex-column gap-1 flex-grow-1 px-3">
                <NavLink to="/" className={({isActive}) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                    <LayoutDashboard size={20}/> <span>Dashboard</span>
                </NavLink>
                <NavLink to="/areas" className={({isActive}) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                    <MapPin size={20}/> <span>Áreas deportivas</span>
                </NavLink>
                <NavLink to="/usuarios" className={({isActive}) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                    <Users size={20}/> <span>Usuarios</span>
                </NavLink>
                <NavLink to="/historial" className={({isActive}) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                    <History size={20}/> <span>Historial</span>
                </NavLink>

                {/* DROPDOWN CATÁLOGOS */}
                <div className="mt-4">
                    <div
                        className="catalog-header-pro d-flex align-items-center justify-content-between p-2 rounded-3"
                        onClick={() => setShowCatalogos(!showCatalogos)}
                        style={{ cursor: 'pointer', background: 'rgba(255,255,255,0.05)' }}
                    >
                        <small className="text-white fw-bold">CATÁLOGOS</small>
                        {showCatalogos ? <ChevronDown size={16}/> : <ChevronRight size={16}/>}
                    </div>

                    {/* ESTO ES LO QUE HACE QUE SE ESCONDA: Si es false, el código NO EXISTE en el HTML */}
                    {showCatalogos && (
                        <div className="ps-3 d-flex flex-column gap-1 mt-2">
                            <NavLink to="/catalogos/carreras" className={({isActive}) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                                <BookOpen size={18}/> <span>Carreras</span>
                            </NavLink>
                            <NavLink to="/catalogos/roles" className={({isActive}) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                                <ShieldCheck size={18}/> <span>Roles</span>
                            </NavLink>
                        </div>
                    )}
                </div>
            </nav>

            {/* PERFIL Y LOGOUT (ACOMODADO PROFESIONAL) */}
            <div className="p-3 border-top border-secondary border-opacity-25 mt-auto">
                <div className="profile-box d-flex align-items-center justify-content-between p-2 rounded-3 bg-white bg-opacity-10">
                    <div className="d-flex align-items-center gap-2 overflow-hidden">
                        <div className="avatar-letter">AD</div>
                        <div className="overflow-hidden text-start">
                            <p className="m-0 small fw-bold text-white text-truncate"></p>
                            <p className="m-0 text-white extra-small opacity-50 text-truncate">admin@utez.edu.mx</p>
                        </div>
                    </div>
                    {/* BOTÓN DE SALIDA A LA DERECHA */}
                    <button className="logout-btn-minimal" onClick={handleLogout} title="Cerrar Sesión">
                        <LogOut size={18} />
                    </button>
                </div>
            </div>
        </div>
    );
}