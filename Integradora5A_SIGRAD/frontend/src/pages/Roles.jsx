import React, { useEffect, useState } from 'react';
import { Plus, Ban, CheckCircle, Save, X } from "lucide-react";

export default function Roles() {
    const [roles, setRoles] = useState([]);
    const [nombreNuevoRol, setNombreNuevoRol] = useState("");
    const [mostrarForm, setMostrarForm] = useState(false);

    const API_URL = 'http://localhost:8080/api/roles';

    const cargarRoles = () => {
        fetch(API_URL)
            .then(res => res.json())
            .then(data => setRoles(data))
            .catch(err => console.error("Error al cargar roles:", err));
    };

    useEffect(() => { cargarRoles(); }, []);

    // FUNCIÓN PARA CREAR EL ROL
    const manejarEnvio = async (e) => {
        e.preventDefault();
        if (!nombreNuevoRol.trim()) return;

        try {
            const res = await fetch(API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    nombre: nombreNuevoRol.toUpperCase(), // Recomendado para roles
                    activo: true
                })
            });

            if (res.ok) {
                setNombreNuevoRol("");
                setMostrarForm(false);
                cargarRoles(); // Recarga la lista
            }
        } catch (err) {
            console.error("Error al crear rol:", err);
        }
    };

    const toggleEstado = async (id) => {
        try {
            const res = await fetch(`${API_URL}/${id}/estado`, {
                method: 'PATCH'
            });
            if (res.ok) cargarRoles();
        } catch (err) {
            console.error("Error al actualizar:", err);
        }
    };

    return (
        <div className="p-5 animate__animated animate__fadeIn">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold mb-1 text-dark">Gestión de Roles</h2>
                    <p className="text-muted small">Catálogo para registro en App Móvil</p>
                </div>
                <button
                    onClick={() => setMostrarForm(!mostrarForm)}
                    className={`btn ${mostrarForm ? 'btn-secondary' : 'btn-success'} d-flex align-items-center gap-2 px-4 py-2 fw-bold`}
                >
                    {mostrarForm ? <X size={18} /> : <Plus size={18} />}
                    {mostrarForm ? 'Cancelar' : 'Nuevo Rol'}
                </button>
            </div>

            {/* FORMULARIO DE REGISTRO */}
            {mostrarForm && (
                <div className="card border-0 shadow-sm mb-4 p-3 animate__animated animate__fadeInDown">
                    <form onSubmit={manejarEnvio} className="row g-3">
                        <div className="col-md-10">
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Escribe el nombre del rol (ej: PROFESOR)"
                                value={nombreNuevoRol}
                                onChange={(e) => setNombreNuevoRol(e.target.value)}
                                autoFocus
                            />
                        </div>
                        <div className="col-md-2">
                            <button type="submit" className="btn btn-success w-100 d-flex align-items-center justify-content-center gap-2">
                                <Save size={18} /> Guardar
                            </button>
                        </div>
                    </form>
                </div>
            )}

            <div className="bg-white rounded-3 shadow-sm border p-4">
                <div className="table-responsive">
                    <table className="table table-hover align-middle mb-0">
                        <thead className="table-light">
                        <tr className="text-muted small text-uppercase">
                            <th className="py-3 text-center">ID</th>
                            <th className="py-3">Nombre del rol</th>
                            <th className="py-3">Estado</th>
                            <th className="py-3 text-center">Acciones</th>
                        </tr>
                        </thead>
                        <tbody>
                        {roles.map(rol => (
                            <tr key={rol.id}>
                                <td className="py-3 text-center fw-bold text-dark">{rol.id}</td>
                                <td className="py-3">{rol.nombre}</td>
                                <td className="py-3">
                                        <span className={`badge px-3 py-2 ${rol.activo ? 'bg-success-subtle text-success' : 'bg-warning-subtle text-warning'}`}>
                                            {rol.activo ? "Activo" : "Inactivo"}
                                        </span>
                                </td>
                                <td className="py-3 text-center">
                                    <button
                                        onClick={() => toggleEstado(rol.id)}
                                        className={`btn btn-sm btn-light border shadow-sm ${rol.activo ? 'text-danger' : 'text-success'}`}
                                        title={rol.activo ? "Desactivar" : "Activar"}
                                    >
                                        {rol.activo ? <Ban size={20} /> : <CheckCircle size={20} />}
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