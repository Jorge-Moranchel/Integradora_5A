import React, { useEffect, useState } from 'react';
import {Search} from "lucide-react";

export default function Carreras() {
    const [carreras, setCarreras] = useState([]);

    useEffect(() => {
        // Aquí llamarás a tu API de Spring Boot
        // fetch('http://localhost:8080/api/carreras')
    }, []);

    return (
        <div>
            <h2 className="fw-bold mb-1 text-dark">Administración de Carreras</h2>
            <p className="text-muted small">Gestiona las carreras con las que los nuevos usuarios pueden seleccionar para registrarse</p>
            {/* Barra de Búsqueda y Filtros */}
            <div className="search-card p-3 mb-5 shadow-sm bg-white rounded-3 border">
                <div className="d-flex align-items-center gap-3">
                    <div className="input-group" style={{ maxWidth: '450px' }}>
            <span className="input-group-text bg-white border-end-0 text-muted">
              <Search size={18} />
            </span>
                        <input
                            type="text"
                            className="form-control border-start-0 shadow-none"
                            placeholder="Buscar por nombre o tipo..."
                        />
                    </div>

                    <div className="btn-group shadow-sm">
                        <button className="btn active fw-bold">Todas</button>
                        <button className="btn">Habilitadas</button>
                        <button className="btn">Inhaibilitadas</button>

                    </div>
                </div>
            </div>
            <button className="btn btn-primary my-3">Nueva Carrera</button>
            <table className="table table-dark table-hover">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                {/* Mapeo de datos */}
                </tbody>
            </table>
        </div>
    );
}