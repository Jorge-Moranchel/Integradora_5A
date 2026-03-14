import React, { useEffect, useState } from 'react';

export default function Carreras() {
    const [carreras, setCarreras] = useState([]);

    useEffect(() => {
        // Aquí llamarás a tu API de Spring Boot
        // fetch('http://localhost:8080/api/carreras')
    }, []);

    return (
        <div>
            <h2 className="text-white">Administración de Carreras</h2>
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