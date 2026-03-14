import React, { useState } from 'react'; // Importa useState
import { Plus, Search } from "lucide-react";

export default function Carreras() {
    const [showModal, setShowModal] = useState(false); // Estado del modal

    return (
        <div className="p-5 animate__animated animate__fadeIn">
            {/* Header Alineado */}
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold mb-1 text-dark">Administración de Carreras</h2>
                    <p className="text-muted small">Gestiona las carreras disponibles para el registro de usuarios</p>
                </div>
                <button
                    className="btn btn-success d-flex align-items-center gap-2 px-4 py-2 fw-bold"
                    onClick={() => setShowModal(true)}
                >
                    <Plus size={18} /> Nueva Carrera
                </button>
            </div>

            {/* Barra de Búsqueda igual a Áreas */}
            <div className="search-card p-3 mb-5 shadow-sm bg-white rounded-3 border">
                <div className="d-flex align-items-center gap-3">
                    <div className="input-group" style={{ maxWidth: '450px' }}>
                        <span className="input-group-text bg-white border-end-0 text-muted">
                            <Search size={18} />
                        </span>
                        <input type="text" className="form-control border-start-0 shadow-none" placeholder="Buscar carrera..." />
                    </div>
                    <div className="btn-group shadow-sm">
                        <button className="btn active fw-bold">Todas</button>
                        <button className="btn">Habilitadas</button>
                        <button className="btn">Inhabilitadas</button>
                    </div>
                </div>
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