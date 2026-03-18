import React, { useState } from 'react';
import { ShieldCheck, Pencil, Trash2, X, Plus } from 'lucide-react';
import Swal from 'sweetalert2';

export default function Roles() {
    const [roles, setRoles] = useState([
        { id: 1, name: 'ESTUDIANTE', status: 'Activo' },
        { id: 2, name: 'PROFESOR', status: 'Activo' },
        { id: 3, name: 'ADMINISTRATIVO', status: 'Activo' },
    ]);

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [newRoleName, setNewRoleName] = useState('');

    const openModal = () => {
        setNewRoleName('');
        setIsModalOpen(true);
    };

    const closeModal = () => setIsModalOpen(false);

    const handleRegister = (e) => {
        e.preventDefault();
        if (!newRoleName.trim()) return;

        const newEntry = {
            id: roles.length + 1,
            name: newRoleName.toUpperCase(),
            status: 'Activo'
        };

        setRoles([...roles, newEntry]);
        closeModal();

        Swal.fire({
            title: '¡Guardado!',
            text: 'El rol se ha registrado correctamente.',
            icon: 'success',
            confirmButtonColor: '#10b981'
        });
    };

    const handleDelete = (id) => {
        Swal.fire({
            title: '¿Estás seguro?',
            text: "Esta acción no se puede deshacer",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#ef4444',
            cancelButtonColor: '#6b7280',
            confirmButtonText: 'Sí, borrar',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                setRoles(roles.filter(r => r.id !== id));
                Swal.fire('Borrado', 'El rol ha sido eliminado.', 'success');
            }
        });
    };

    return (
        <div className="container-fluid p-4" style={{ backgroundColor: '#f3f4f6', minHeight: '100vh' }}>

            <div className="d-flex justify-content-between align-items-center mb-5">
                <div>
                    <h1 className="fw-bold m-0" style={{ letterSpacing: '-1.5px', color: '#111827' }}>Gestión de Roles</h1>
                    <p className="text-muted m-0">Catálogo para registro en App Móvil</p>
                </div>
                <button
                    className="btn btn-success d-flex align-items-center gap-2 px-4 py-2 shadow-sm"
                    style={{ backgroundColor: '#10b981', borderColor: '#10b981', borderRadius: '12px' }}
                    onClick={openModal}
                >
                    <Plus size={18} />
                    <span className="fw-bold">Nuevo Rol</span>
                </button>
            </div>

            {/* TABLA LIMPIA */}
            <div className="bg-white rounded-4 shadow-sm overflow-hidden border">
                <table className="table table-hover m-0">
                    <thead style={{ backgroundColor: '#f9fafb' }}>
                    <tr>
                        <th className="ps-4 py-3 text-muted fw-bold" style={{ fontSize: '0.75rem' }}>ID</th>
                        <th className="py-3 text-muted fw-bold" style={{ fontSize: '0.75rem' }}>NOMBRE DEL ROL</th>
                        <th className="py-3 text-muted fw-bold" style={{ fontSize: '0.75rem' }}>ESTADO</th>
                        <th className="pe-4 py-3 text-muted fw-bold text-center" style={{ fontSize: '0.75rem' }}>ACCIONES</th>
                    </tr>
                    </thead>
                    <tbody>
                    {roles.map((role) => (
                        <tr key={role.id} className="align-middle">
                            <td className="ps-4 fw-bold text-muted">#{role.id}</td>
                            <td className="fw-semibold" style={{ color: '#374151' }}>{role.name}</td>
                            <td>
                                    <span className="badge rounded-pill" style={{ backgroundColor: '#d1fae5', color: '#065f46', padding: '6px 12px', fontSize: '0.7rem' }}>
                                        {role.status}
                                    </span>
                            </td>
                            <td className="pe-4">
                                <div className="d-flex justify-content-center gap-2">
                                    <button className="btn-action edit" title="Editar">
                                        <Pencil size={16} />
                                    </button>
                                    <button className="btn-action delete" title="Borrar" onClick={() => handleDelete(role.id)}>
                                        <Trash2 size={16} />
                                    </button>
                                </div>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

            {/* MODAL UNIFICADO (ESTILO CARRERAS) */}
            {isModalOpen && (
                <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content border-0 rounded-4 shadow-lg">
                            <div className="modal-header border-0 p-4 pb-0">
                                <h5 className="modal-title fw-bold d-flex align-items-center gap-2">
                                    <ShieldCheck size={22} className="text-success" />
                                    Nuevo Rol
                                </h5>
                                <button type="button" className="btn-close" onClick={closeModal}></button>
                            </div>
                            <form onSubmit={handleRegister}>
                                <div className="modal-body p-4">
                                    <div className="mb-3">
                                        <label className="form-label fw-bold small text-dark">Nombre del Rol</label>
                                        <input
                                            type="text"
                                            className="form-control p-2 px-3"
                                            placeholder="Ej: ESTUDIANTE"
                                            style={{ backgroundColor: '#f9fafb', borderRadius: '10px' }}
                                            value={newRoleName}
                                            onChange={(e) => setNewRoleName(e.target.value)}
                                            required
                                        />
                                    </div>
                                    <div className="d-flex justify-content-end gap-2 mt-4">
                                        <button type="button" className="btn border-0 fw-bold" onClick={closeModal}>Cancelar</button>
                                        <button type="submit" className="btn btn-success px-4 fw-bold" style={{ backgroundColor: '#10b981', borderRadius: '10px' }}>
                                            Registrar
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}