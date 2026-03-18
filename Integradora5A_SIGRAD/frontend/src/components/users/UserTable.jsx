import React from 'react';
import { Edit, Ban, Mail, Phone, GraduationCap, Shield, CircleCheck } from 'lucide-react';
import Swal from 'sweetalert2';

export default function UserTable({ users, onRefresh, onEdit }) {

    const handleToggleStatus = async (id, nombre, estadoActual) => {
        const accion = estadoActual !== false ? 'bloquear' : 'activar';
        const result = await Swal.fire({
            title: `¿${accion.charAt(0).toUpperCase() + accion.slice(1)} usuario?`,
            text: `Vas a cambiar el estado de ${nombre}`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: estadoActual !== false ? '#dc2626' : '#16a34a',
            confirmButtonText: `Sí, ${accion}`,
            cancelButtonText: 'Cancelar',
            borderRadius: '15px'
        });

        if (result.isConfirmed) {
            try {
                const res = await fetch(`http://localhost:8080/api/usuarios/${id}/estado`, { method: 'PATCH' });
                if (res.ok) {
                    Swal.fire({ icon: 'success', title: '¡Hecho!', showConfirmButton: false, timer: 1000 });
                    onRefresh();
                }
            } catch (error) { Swal.fire('Error', 'Error de red', 'error'); }
        }
    };

    return (
        <div className="table-responsive">
            <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                <tr className="small text-muted text-uppercase fw-bold">
                    <th className="ps-4 py-3">Identidad</th>
                    <th className="py-3">Contacto</th>
                    <th className="py-3">Académico & Rol</th>
                    <th className="py-3 text-center">Estado</th>
                    <th className="pe-4 py-3 text-end">Acciones</th>
                </tr>
                </thead>
                <tbody>
                {users?.map((user) => (
                    <tr key={user.id} className="border-bottom border-light">
                        {/* IDENTIDAD: Nombre y Matrícula */}
                        <td className="ps-4 py-3">
                            <div className="d-flex align-items-center">
                                <div className="bg-success text-white rounded-circle d-flex align-items-center justify-content-center fw-bold me-3 shadow-sm"
                                     style={{ width: '42px', height: '42px', fontSize: '14px' }}>
                                    {user.nombre?.charAt(0).toUpperCase()}
                                </div>
                                <div>
                                    <div className="fw-bold text-dark mb-0" style={{ fontSize: '0.95rem' }}>{user.nombre}</div>
                                    <div className="text-muted extra-small">
                                        <span className="badge bg-secondary bg-opacity-10 text-secondary border-0 px-1" style={{fontSize: '10px'}}>{user.matricula}</span>
                                    </div>
                                </div>
                            </div>
                        </td>

                        {/* CONTACTO: Email e Icono de Teléfono */}
                        <td className="py-3">
                            <div className="d-flex flex-column gap-1">
                                <div className="small text-dark d-flex align-items-center gap-2">
                                    <Mail size={14} className="text-muted" /> {user.emailInstitucional}
                                </div>
                                <div className="extra-small text-muted d-flex align-items-center gap-2">
                                    <Phone size={14} /> {user.telefono || 'Sin teléfono'}
                                </div>
                            </div>
                        </td>

                        {/* ACADÉMICO: Carrera y Rol */}
                        <td className="py-3">
                            <div className="d-flex flex-column gap-1">
                                <div className="small fw-semibold text-dark d-flex align-items-center gap-2">
                                    <GraduationCap size={16} className="text-success" /> {user.carrera || 'N/A'}
                                </div>
                                <div className="d-flex align-items-center gap-1 text-muted small">
                                    <Shield size={14} /> <span className="text-uppercase" style={{fontSize: '10px', fontWeight: '700'}}>{user.rol}</span>
                                </div>
                            </div>
                        </td>

                        {/* ESTADO: Con efecto Glow */}
                        <td className="py-3 text-center">
                            <span className={`status-badge ${user.estado !== false ? 'active' : 'blocked'}`}>
                                <span className="status-dot"></span>
                                {user.estado !== false ? 'Activo' : 'Bloqueado'}
                            </span>
                        </td>

                        {/* ACCIONES: Botones Azules y Rojos */}
                        <td className="pe-4 py-3 text-end">
                            <div className="d-inline-flex gap-2">
                                <button className="btn btn-outline-primary btn-sm border shadow-sm" onClick={() => onEdit(user)} title="Editar">
                                    <Edit size={18} />
                                </button>
                                <button
                                    className={`btn btn-sm border shadow-sm ${user.estado !== false ? 'btn-outline-danger' : 'btn-outline-success'}`}
                                    onClick={() => handleToggleStatus(user.id, user.nombre, user.estado)}
                                    title={user.estado !== false ? "Bloquear" : "Activar"}
                                >
                                    {user.estado !== false ? <Ban size={18}/> : <CircleCheck size={18}/>}
                                </button>
                            </div>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}