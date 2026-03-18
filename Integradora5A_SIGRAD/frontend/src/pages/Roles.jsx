import React, { useState } from 'react';
import { ShieldCheck, Pencil, Power, Plus, Search } from 'lucide-react';
import Swal from 'sweetalert2';

export default function Roles() {
    const [roles, setRoles] = useState([
        { id: 1, name: 'ESTUDIANTE', status: 'Habilitado' },
        { id: 2, name: 'PROFESOR', status: 'Habilitado' },
        { id: 3, name: 'ADMINISTRATIVO', status: 'Inhabilitado' },
    ]);

    const [searchTerm, setSearchTerm] = useState('');
    const [filter, setFilter] = useState('Todas');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [currentRole, setCurrentRole] = useState({ id: null, name: '' });

    const openModal = (role = { id: null, name: '' }) => {
        setIsEditMode(!!role.id);
        setCurrentRole(role);
        setIsModalOpen(true);
    };

    const handleStatusChange = (role) => {
        const nuevoEstado = role.status === 'Habilitado' ? 'Inhabilitado' : 'Habilitado';
        Swal.fire({
            title: `¿${nuevoEstado === 'Habilitado' ? 'Habilitar' : 'Inhabilitar'} rol?`,
            text: `El rol cambiará a estado ${nuevoEstado}`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: nuevoEstado === 'Habilitado' ? '#10b981' : '#ef4444',
            confirmButtonText: 'Confirmar'
        }).then((result) => {
            if (result.isConfirmed) {
                setRoles(roles.map(r => r.id === role.id ? { ...r, status: nuevoEstado } : r));
            }
        });
    };

    const filteredRoles = roles.filter(role => {
        const matchesSearch = role.name.toLowerCase().includes(searchTerm.toLowerCase());
        const matchesFilter = filter === 'Todas' || role.status === filter;
        return matchesSearch && matchesFilter;
    });

    return (
        <div className="container-fluid p-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h1 className="fw-bold m-0" style={{ fontSize: '2.5rem', letterSpacing: '-1px' }}>Administración de Roles</h1>
                    <p className="text-muted">Gestiona los roles disponibles para el registro de usuarios</p>
                </div>
                <button
                    className="btn btn-success d-flex align-items-center gap-2 px-4 py-2"
                    style={{ backgroundColor: '#10b981', border: 'none', borderRadius: '10px', fontWeight: '600' }}
                    onClick={() => openModal()}
                >
                    <Plus size={20} /> Nueva Rol
                </button>
            </div>

            <div className="bg-white rounded-4 shadow-sm p-4 border">
                <div className="d-flex justify-content-between align-items-center mb-4">
                    <div className="position-relative w-50">
                        <Search className="position-absolute top-50 translate-middle-y ms-3 text-muted" size={18} />
                        <input
                            type="text"
                            className="form-control ps-5 py-2 border-0 shadow-none"
                            style={{ backgroundColor: '#f9fafb', borderRadius: '10px' }}
                            placeholder="Buscar rol..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>
                    <div className="btn-group p-1" style={{ backgroundColor: '#f3f4f6', borderRadius: '12px' }}>
                        {['Todas', 'Habilitado', 'Inhabilitado'].map((f) => (
                            <button
                                key={f}
                                className={`btn btn-sm px-4 py-2 border-0 ${filter === f ? 'bg-dark text-white shadow' : 'text-muted'}`}
                                style={{ borderRadius: '10px', fontWeight: '600', transition: '0.3s' }}
                                onClick={() => setFilter(f)}
                            >
                                {f === 'Inhabilitado' ? 'Inhabilitadas' : f === 'Habilitado' ? 'Habilitadas' : f}
                            </button>
                        ))}
                    </div>
                </div>

                <div className="table-responsive">
                    <table className="table align-middle">
                        <thead>
                        <tr className="text-muted" style={{ fontSize: '0.8rem', borderBottom: '2px solid #f3f4f6' }}>
                            <th className="py-3 px-4">ID</th>
                            <th className="py-3">NOMBRE DEL ROL</th>
                            <th className="py-3 text-center">ESTADO</th>
                            <th className="py-3 text-center">ACCIONES</th>
                        </tr>
                        </thead>
                        <tbody>
                        {filteredRoles.map((role) => (
                            <tr key={role.id} style={{ borderBottom: '1px solid #f3f4f6' }}>
                                <td className="px-4 text-muted fw-bold">#{role.id}</td>
                                <td>
                                    <div className="d-flex align-items-center gap-3">
                                        <div className="p-2 rounded-3" style={{ backgroundColor: '#d1fae5' }}>
                                            <ShieldCheck size={20} className="text-success" />
                                        </div>
                                        <span className="fw-bold" style={{ color: '#111827' }}>{role.name}</span>
                                    </div>
                                </td>
                                <td className="text-center">
                                        <span className="badge px-3 py-2" style={{
                                            backgroundColor: role.status === 'Habilitado' ? '#d1fae5' : '#fee2e2',
                                            color: role.status === 'Habilitado' ? '#059669' : '#dc2626',
                                            borderRadius: '20px', fontSize: '0.75rem'
                                        }}>
                                            {role.status}
                                        </span>
                                </td>
                                <td className="text-center">
                                    <div className="d-flex justify-content-center gap-2">
                                        <button className="btn-action edit" onClick={() => openModal(role)}>
                                            <Pencil size={18} />
                                        </button>
                                        <button
                                            className="btn-action delete"
                                            style={{ color: role.status === 'Habilitado' ? '#ef4444' : '#10b981' }}
                                            onClick={() => handleStatusChange(role)}
                                        >
                                            <Power size={18} />
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>

            {isModalOpen && (
                <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content border-0 rounded-4 shadow-lg p-3">
                            <div className="modal-header border-0">
                                <h5 className="modal-title fw-bold">{isEditMode ? 'Editar Rol' : 'Nuevo Rol'}</h5>
                                <button type="button" className="btn-close shadow-none" onClick={() => setIsModalOpen(false)}></button>
                            </div>
                            <div className="modal-body">
                                <label className="form-label small fw-bold text-muted">Nombre del Rol</label>
                                <input
                                    type="text"
                                    className="form-control py-2"
                                    style={{ backgroundColor: '#f9fafb', borderRadius: '10px' }}
                                    placeholder="Ej: ADMINISTRADOR"
                                    value={currentRole.name}
                                    onChange={(e) => setCurrentRole({ ...currentRole, name: e.target.value })}
                                />
                                <div className="d-flex justify-content-end gap-2 mt-4">
                                    <button className="btn fw-bold text-muted" onClick={() => setIsModalOpen(false)}>Cancelar</button>
                                    <button className="btn btn-success px-4 fw-bold" style={{ backgroundColor: '#10b981', borderRadius: '10px' }}>
                                        {isEditMode ? 'Actualizar' : 'Registrar'}
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}