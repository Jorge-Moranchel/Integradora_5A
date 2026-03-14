import React, { useState } from 'react';
import { Plus } from "lucide-react";

export default function Roles() {
    const [showModal, setShowModal] = useState(false);

    return (
        <div className="p-5 animate__animated animate__fadeIn">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold mb-1 text-dark">Administración de Roles</h2>
                    <p className="text-muted small">Define los permisos y accesos de los usuarios</p>
                </div>
                <button
                    className="btn btn-success d-flex align-items-center gap-2 px-4 py-2 fw-bold"
                    onClick={() => setShowModal(true)}
                >
                    <Plus size={18} /> Nuevo Rol
                </button>
            </div>

            <table className="table table-hover shadow-sm">
                <thead className="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody>{/* Mapeo aquí */}</tbody>
            </table>
        </div>
    );
}