import React, { useEffect, useState } from 'react';
import { Plus, Ban, CheckCircle } from "lucide-react";

export default function Roles() {
    const [roles, setRoles] = useState([]);

    const cargarRoles = () => {
        fetch('http://localhost:8080/api/roles')
            .then(res => res.json())
            .then(data => setRoles(data))
            .catch(err => console.error("Error al cargar roles:", err));
    };

    useEffect(() => { cargarRoles(); }, []);

    const toggleEstado = async (id) => {
        try {
            const res = await fetch(`http://localhost:8080/api/roles/${id}/estado`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' }
            });
            if (res.ok) cargarRoles(); // Refresca la tabla al instante
        } catch (err) {
            console.error("Error al actualizar:", err);
        }
    };

    return (
        <div className="p-5 animate__animated animate__fadeIn">
            {/* Header consistente */}
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold mb-1 text-dark">Gestión de Roles</h2>
                    <p className="text-muted small">Administra los roles de acceso al sistema</p>
                </div>
                <button className="btn btn-success d-flex align-items-center gap-2 px-4 py-2 fw-bold">
                    <Plus size={18} /> Nuevo Rol
                </button>
            </div>

            {/* Tabla en Tarjeta Sombreada */}
            <div className="bg-white rounded-3 shadow-sm border p-4">
                <div className="table-responsive">
                    <table className="table table-hover align-middle mb-0">
                        <thead className="table-light">
                        <tr className="text-muted small text-uppercase">
                            <th className="py-3">ID</th>
                            <th className="py-3">Nombre del rol</th>
                            <th className="py-3">Estado</th>
                            <th className="py-3 text-center">Acciones</th>
                        </tr>
                        </thead>
                        <tbody>
                        {roles.map(rol => (
                            <tr key={rol.id}>
                                <td className="py-3 fw-bold text-dark">{rol.id}</td>
                                <td className="py-3">{rol.nombre}</td>
                                <td className="py-3">
                                        <span className={`badge px-3 py-2 ${rol.activo ? 'bg-success-subtle text-success' : 'bg-warning-subtle text-warning'}`}>
                                            {rol.activo ? "Activo" : "Inactivo"}
                                        </span>
                                </td>
                                <td className="py-3 text-center">
                                    <button
                                        onClick={() => toggleEstado(rol.id)}
                                        className={`btn btn-sm btn-light border-0 ${rol.activo ? 'text-danger' : 'text-success'}`}
                                        title={rol.activo ? "Deshabilitar" : "Habilitar"}
                                    >
                                        {rol.activo ? <Ban size={22} /> : <CheckCircle size={22} />}
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}