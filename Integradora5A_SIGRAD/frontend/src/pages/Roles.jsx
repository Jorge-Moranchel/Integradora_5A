import React, { useEffect, useState } from 'react';

export default function Carreras() {
    const [carreras, setCarreras] = useState([]);

    useEffect(() => {
        // Aquí llamarás a tu API de Spring Boot
        // fetch('http://localhost:8080/api/carreras')
    }, []);

    return (
        <div>
            <h2 className="fw-bold mb-1 text-dark">Administración de Roles</h2>
            <p className="text-muted small">Gestiona los roles con las que los nuevos usuarios pueden seleccionar para registrarse</p>
            <button className="btn btn-primary my-3">Nuevo Rol</button>
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