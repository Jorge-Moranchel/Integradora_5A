import React from 'react';
import { Edit, Mail, Phone, GraduationCap, Shield } from 'lucide-react';
import Swal from 'sweetalert2';

export default function UserTable({ users, isLoading, onRefresh, onEdit }) {

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

    if (isLoading) {
        return (
            <div className="d-flex justify-content-center py-5 my-5">
                <div className="spinner-border text-primary" role="status" style={{ width: '3rem', height: '3rem' }}>
                    <span className="visually-hidden">Cargando...</span>
                </div>
            </div>
        );
    }

    return (
        <div className="table-responsive">
            {/* ESTILOS DEL SWITCH PARA QUE SEA IDÉNTICO AL DE CARRERAS */}
            <style>
                {`
                .custom-switch {
                    width: 48px !important;
                    height: 24px !important;
                    cursor: pointer;
                    background-color: #dee2e6;
                    border: none !important;
                }
                .custom-switch:checked {
                    background-color: #10b981 !important;
                }
                .custom-switch:focus {
                    box-shadow: none !important;
                }
                `}
            </style>

            <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                <tr className="small text-muted text-uppercase fw-bold">
                    <th className="ps-4 py-3">Identidad</th>
                    <th className="py-3">Contacto</th>
                    <th className="py-3">Académico & Rol</th>
                    <th className="py-3 text-center">Cuenta</th>
                    <th className="py-3 text-center">Estado</th>
                    <th className="pe-4 py-3 text-end">Acciones</th>
                </tr>
                </thead>
                <tbody>
                {users?.map((user) => {

                    const estaValidado = user.validado === true;

                    return (
                        <tr key={user.id} className="border-bottom border-light">
                            <td className="ps-4 py-3">
                                <div className="d-flex align-items-center">
                                    <div
                                        className="text-white rounded-circle d-flex align-items-center justify-content-center fw-bold me-3 shadow-sm"
                                        style={{
                                            width: '42px', height: '42px', fontSize: '14px',
                                            backgroundColor: estaValidado ? '#00a854' : '#f59e0b'
                                        }}
                                    >
                                        {user.nombre?.charAt(0).toUpperCase()}
                                    </div>
                                    <div>
                                        <div className="fw-bold text-dark mb-0" style={{ fontSize: '0.95rem' }}>{user.nombre}</div>
                                        <div className="text-muted extra-small">
                                            <span className="badge bg-secondary bg-opacity-10 text-secondary border-0 px-1" style={{fontSize: '10px'}}>
                                                {user.matricula}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </td>

                            {/* CONTACTO*/}
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
                                        <Shield size={14} />
                                        <span className="text-uppercase" style={{fontSize: '10px', fontWeight: '700'}}>{user.rol}</span>
                                    </div>
                                </div>
                            </td>

                            <td className="py-3 text-center">
                                {estaValidado ? (
                                    <span
                                        className="badge d-inline-flex align-items-center gap-1"
                                        style={{
                                            backgroundColor: '#d1fae5',
                                            color: '#065f46',
                                            fontSize: '11px',
                                            padding: '5px 10px',
                                            borderRadius: '20px'
                                        }}
                                    >
                                        <CheckCircle size={12} /> Verificado
                                    </span>
                                ) : (
                                    <span
                                        className="badge d-inline-flex align-items-center gap-1"
                                        style={{
                                            backgroundColor: '#fef3c7',
                                            color: '#92400e',
                                            fontSize: '11px',
                                            padding: '5px 10px',
                                            borderRadius: '20px'
                                        }}
                                    >
                                        <Clock size={12} /> Pendiente
                                    </span>
                                )}
                            </td>

                            {/* ESTADO: Switch bloqueo/activación*/}
                            <td className="py-3 text-center">
                                <div className="d-flex flex-column align-items-center gap-1">
                                    <div className="form-check form-switch m-0 d-flex justify-content-center p-0">
                                        <input
                                            className="form-check-input custom-switch m-0"
                                            type="checkbox"
                                            role="switch"
                                            checked={user.estado !== false}
                                            onChange={() => handleToggleStatus(user.id, user.nombre, user.estado)}
                                        />
                                    </div>
                                    <span
                                        className={`badge ${user.estado !== false
                                            ? 'bg-success bg-opacity-10 text-success'
                                            : 'bg-danger bg-opacity-10 text-danger'}`}
                                        style={{fontSize: '10px'}}
                                    >
                                        {user.estado !== false ? 'ACTIVO' : 'BLOQUEADO'}
                                    </span>
                                </div>
                            </td>

                            {/* ACCIONES*/}
                            <td className="pe-4 py-3 text-end">
                                <button
                                    className="btn btn-outline-primary btn-sm border shadow-sm"
                                    onClick={() => onEdit(user)}
                                    title="Editar"
                                >
                                    <Edit size={18} />
                                </button>
                            </td>
                        </tr>
                    );
                })}
                </tbody>
            </table>
        </div>
    );
}