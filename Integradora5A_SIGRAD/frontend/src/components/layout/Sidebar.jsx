import React from 'react';
import { NavLink } from 'react-router-dom';
import { LayoutDashboard, MapPin, Users, History, BookOpen, ShieldCheck } from 'lucide-react';

export default function Sidebar() {
    return (
        <div className="sidebar-container d-flex flex-column">
            <div className="p-4">
                <h4 className="fw-bold m-0 text-white">Reserva Deportiva</h4>
                <p className="text-muted small">Panel de Administración</p>
            </div>

            <nav className="nav flex-column gap-1 flex-grow-1">
                <NavLink to="/" className={({isActive}) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                    <LayoutDashboard size={20}/> Dashboard
                </NavLink>
                <NavLink to="/areas" className={({isActive}) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                    <MapPin size={20}/> Áreas deportivas
                </NavLink>
                <NavLink to="/usuarios" className={({isActive}) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                    <Users size={20}/> Usuarios
                </NavLink>
                <NavLink to="/historial" className={({isActive}) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                    <History size={20}/> Historial
                </NavLink>
            </nav>

            <div className="p-4 border-top border-secondary">
                <div className="d-flex align-items-center gap-2">
                    <div className="bg-secondary rounded-circle d-flex align-items-center justify-content-center fw-bold" style={{width:40, height:40}}>AD</div>
                    <div className="overflow-hidden">
                        <p className="m-0 small fw-bold text-white text-truncate">Administrador</p>
                        <p className="m-0 text-muted extra-small text-truncate">admin@reserva.com</p>
                    </div>
                </div>
            </div>
            <div className="mt-3 px-3">
                <small className="text-uppercase text-muted fw-bold" style={{fontSize: '0.7rem'}}>Catálogos</small>
            </div>

            <NavLink to="/catalogos/carreras" className={({isActive}) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                <BookOpen size={20}/> Carreras
            </NavLink>

            <NavLink to="/catalogos/roles" className={({isActive}) => isActive ? "nav-link-custom active" : "nav-link-custom"}>
                <ShieldCheck size={20}/> Roles
            </NavLink>
        </div>
    );
}