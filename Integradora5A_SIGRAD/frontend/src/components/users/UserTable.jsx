import React from 'react';
import { Edit, Ban } from 'lucide-react';
import Swal from 'sweetalert2';

export default function UserTable({ users, onRefresh, onEdit }) {

    const handleToggleStatus = async (id, nombre, estadoActual) => {
        const accion = estadoActual !== false ? 'bloquear' : 'activar';
        const result = await Swal.fire({
            title: `¿Deseas ${accion} a este usuario?`,
            text: `Cambiando el estado de ${nombre}`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: estadoActual !== false ? '#d33' : '#00a854',
            confirmButtonText: `Sí, ${accion}`
        });

        if (result.isConfirmed) {
            try {
                const res = await fetch(`http://localhost:8080/api/usuarios/${id}/estado`, { method: 'PATCH' });
                if (res.ok) {
                    Swal.fire('¡Hecho!', '', 'success');
                    onRefresh();
                }
            } catch (error) { Swal.fire('Error', 'Servidor no responde', 'error'); }
        }
    };

    return (
        <div className="table-responsive">
            <table className="table table-hover align-middle">
                <thead className="table-light">
                <tr className="small text-muted">
                    <th>USUARIO</th>
                    <th>CORREO</th>
                    <th>TELÉFONO</th>
                    <th>ROL</th>
                    <th>ESTADO</th>
                    <th className="text-center">ACCIONES</th>
                </tr>
                </thead>
                <tbody>
                {users?.map((user) => (
                    <tr key={user.id}>
                        <td>
                            <div className="d-flex align-items-center gap-2">
                                <div className="bg-success text-white rounded-circle d-flex align-items-center justify-content-center" style={{ width: '32px', height: '32px', fontSize: '12px' }}>
                                    {user.nombre?.charAt(0).toUpperCase()}
                                </div>
                                <span className="fw-bold">{user.nombre}</span>
                            </div>
                        </td>
                        <td className="text-muted small">{user.emailInstitucional}</td>
                        <td>{user.telefono || 'N/A'}</td>
                        <td><span className="badge bg-light text-dark border">{user.rol}</span></td>
                        <td>
                            <span className={`badge rounded-pill px-3 ${user.estado !== false ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'}`}>
                                {user.estado !== false ? 'Activo' : 'Bloqueado'}
                            </span>
                        </td>
                        <td className="text-center">
                            <button className="btn btn-light btn-sm me-1 border" onClick={() => onEdit(user)}>
                                <Edit size={16} className="text-primary"/>
                            </button>
                            <button
                                className={`btn btn-light btn-sm border ${user.estado !== false ? 'text-danger' : 'text-success'}`}
                                onClick={() => handleToggleStatus(user.id, user.nombre, user.estado)}
                            >
                                <Ban size={16}/>
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}