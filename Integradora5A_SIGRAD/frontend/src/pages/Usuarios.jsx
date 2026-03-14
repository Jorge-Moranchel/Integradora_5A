import React, { useState } from 'react';
import UserTable from '../components/users/UserTable';
import UserModal from '../components/users/UserModal';
import { Search, Plus, SearchIcon } from 'lucide-react';

export default function Usuarios() {
    const [showModal, setShowModal] = useState(false);

    return (
        <div className="animate__animated animate__fadeIn">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold">Usuarios</h2>
                    <p className="text-muted">Administra los usuarios registrados en el sistema</p>
                </div>
                <button className="btn btn-success d-flex align-items-center gap-2 px-4 py-2 fw-bold" onClick={() => setShowModal(true)}>
                    <Plus size={20} /> Nuevo Usuario
                </button>
            </div>

            <div className="row g-3 mb-4">
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm p-4 h-100 text-center text-md-start">
                        <p className="text-muted small fw-bold mb-2">Usuarios Activos</p>
                        <h2 className="fw-bold text-success m-0">0</h2>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm p-4 h-100 text-center text-md-start">
                        <p className="text-muted small fw-bold mb-2">Usuarios Inactivos</p>
                        <h2 className="fw-bold text-warning m-0">0</h2>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm p-4 h-100 text-center text-md-start">
                        <p className="text-muted small fw-bold mb-2">Usuarios Bloqueados</p>
                        <h2 className="fw-bold text-danger m-0">0</h2>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card border-0 shadow-sm p-4 h-100 text-center text-md-start">
                        <p className="text-muted small fw-bold mb-2">Total de Reservas</p>
                        <h2 className="fw-bold text-primary m-0">0</h2>
                    </div>
                </div>
            </div>

            <div className="card border-0 shadow-sm p-4">
                <div className="d-flex gap-2 mb-4">
                    <div className="input-group" style={{ maxWidth: '400px' }}>
                        <span className="input-group-text bg-white border-end-0"><Search size={18} className="text-muted" /></span>
                        <input type="text" className="form-control border-start-0 shadow-none" placeholder="Buscar por nombre o email..." />
                    </div>
                </div>

                <UserTable users={[]} />

                <div className="text-center py-5">
                    <SearchIcon size={50} className="text-muted mb-3 opacity-25" />
                    <p className="text-muted">No se encontraron usuarios</p>
                </div>
            </div>

            <UserModal show={showModal} onClose={() => setShowModal(false)} />
        </div>
    );
}