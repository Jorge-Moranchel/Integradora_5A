import React from 'react';
import { Edit, Ban } from 'lucide-react';

export default function UserTable({ users }) {
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
                    <th>ACCIONES</th>
                </tr>
                </thead>
                <tbody>
                {users?.map(user => (
                    <tr key={user.id}>
                        <td><span className="fw-bold">{user.nombre}</span></td>
                        <td>{user.email}</td>
                        <td>{user.telefono}</td>
                        <td><span className="badge bg-light text-dark border">{user.rol}</span></td>
                        <td><span className="badge-disponible">Activo</span></td>
                        <td>
                            <button className="btn btn-light btn-sm me-1"><Edit size={16}/></button>
                            <button className="btn btn-light btn-sm text-danger"><Ban size={16}/></button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}