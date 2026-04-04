import React from 'react';
import { Eye, ShieldCheck, AlignLeft } from 'lucide-react';

export default function RoleViewModal({ show, onClose, role }) {
    if (!show || !role) return null;

    return (
        <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
            <div className="modal-dialog modal-dialog-centered">
                <div className="modal-content border-0 shadow-lg" style={{ borderRadius: '20px' }}>
                    <div className="modal-header border-0 p-4 pb-0">
                        <div className="d-flex align-items-center gap-2">
                            <div className="p-2 rounded-3 bg-info bg-opacity-10 text-info">
                                <Eye size={24} />
                            </div>
                            <h5 className="modal-title fw-bold m-0">Detalles del Rol</h5>
                        </div>
                        <button type="button" className="btn-close shadow-none" onClick={onClose}></button>
                    </div>

                    <div className="modal-body p-4">
                        {/* HEADER DEL ROL */}
                        <div className="d-flex align-items-center gap-3 mb-4 p-3 bg-light rounded-4">
                            <div className="p-3 rounded-circle bg-primary bg-opacity-10 text-primary">
                                <ShieldCheck size={30} />
                            </div>
                            <div>
                                <h4 className="fw-bolder m-0 text-dark">{role.nombre}</h4>
                                <span className={`badge mt-1 ${role.activo ? 'bg-success' : 'bg-secondary'}`}>
                                    {role.activo ? 'ESTADO ACTIVO' : 'ESTADO INACTIVO'}
                                </span>
                            </div>
                        </div>

                        {/* SECCIÓN DE DESCRIPCIÓN */}
                        <div>
                            <h6 className="fw-bold text-muted text-uppercase d-flex align-items-center gap-2 mb-2" style={{ fontSize: '0.85rem' }}>
                                <AlignLeft size={16} /> Descripción y Permisos
                            </h6>
                            <div className="p-3 bg-white border rounded-4 shadow-sm" style={{ minHeight: '100px' }}>
                                {role.descripcion ? (
                                    <p className="m-0 text-dark" style={{ lineHeight: '1.6', fontSize: '0.95rem' }}>
                                        {role.descripcion}
                                    </p>
                                ) : (
                                    <p className="m-0 text-muted fst-italic text-center mt-3">
                                        Este rol no tiene una descripción asignada.
                                    </p>
                                )}
                            </div>
                        </div>
                    </div>

                    <div className="modal-footer border-0 p-4 pt-0">
                        <button
                            type="button"
                            className="btn btn-primary w-100 py-3 fw-bold"
                            style={{ borderRadius: '12px' }}
                            onClick={onClose}
                        >
                            Cerrar
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}