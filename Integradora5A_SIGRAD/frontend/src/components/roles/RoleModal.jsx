import React, { useState, useEffect } from 'react';
import { ShieldCheck, Save } from 'lucide-react';
import Swal from 'sweetalert2';

export default function RoleModal({ show, onClose, onRefresh, roleToEdit }) {
    const [nombre, setNombre] = useState('');
    // 👇 1. NUEVO ESTADO PARA LA DESCRIPCIÓN 👇
    const [descripcion, setDescripcion] = useState('');
    const [enviando, setEnviando] = useState(false);

    useEffect(() => {
        if (roleToEdit) {
            setNombre(roleToEdit.nombre || '');
            setDescripcion(roleToEdit.descripcion || ''); // Carga la descripción si existe
        } else {
            setNombre('');
            setDescripcion('');
        }
    }, [roleToEdit, show]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!nombre.trim()) return;

        setEnviando(true);

        // 👇 2. AJUSTAMOS URL Y MÉTODO DEPENDIENDO SI ES NUEVO O EDICIÓN 👇
        const isEditing = Boolean(roleToEdit);
        const url = isEditing
            ? `http://localhost:8080/api/roles/${roleToEdit.id}`
            : "http://localhost:8080/api/roles";

        const method = isEditing ? 'PUT' : 'POST';

        const datos = {
            id: roleToEdit ? roleToEdit.id : null,
            nombre: nombre.toUpperCase(),
            descripcion: descripcion, // Agregamos la descripción al JSON
            activo: roleToEdit ? roleToEdit.activo : true
        };

        try {
            const response = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(datos)
            });

            if (response.ok) {
                Swal.fire({
                    icon: 'success',
                    title: '¡Éxito!',
                    text: `El rol se ha ${isEditing ? 'actualizado' : 'guardado'} correctamente.`,
                    timer: 2000,
                    showConfirmButton: false
                });
                onRefresh();
                onClose();
            } else {
                throw new Error('Error en el servidor');
            }
        } catch (error) {
            Swal.fire('Error', 'No se pudo conectar con el servidor', 'error');
        } finally {
            setEnviando(false);
        }
    };

    if (!show) return null;

    return (
        // 👇 3. QUITAMOS EL BLUR, DEJAMOS SOLO EL FONDO OSCURO ESTÁNDAR 👇
        <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
            <div className="modal-dialog modal-dialog-centered">
                <div className="modal-content border-0 shadow-lg" style={{ borderRadius: '20px' }}>
                    <div className="modal-header border-0 p-4 pb-0">
                        <div className="d-flex align-items-center gap-2">
                            <div className="p-2 rounded-3 bg-success bg-opacity-10 text-success">
                                <ShieldCheck size={24} />
                            </div>
                            <h5 className="modal-title fw-bold m-0">
                                {roleToEdit ? 'Editar Rol' : 'Nuevo Rol'}
                            </h5>
                        </div>
                        <button type="button" className="btn-close shadow-none" onClick={onClose}></button>
                    </div>

                    <form onSubmit={handleSubmit}>
                        <div className="modal-body p-4">
                            {/* CAMPO NOMBRE */}
                            <div className="mb-3">
                                <label className="form-label small fw-bold text-muted text-uppercase">Nombre del Rol</label>
                                <input
                                    type="text"
                                    className="form-control border-0 py-3 shadow-none"
                                    style={{ backgroundColor: '#f3f4f6', borderRadius: '12px', fontWeight: '600' }}
                                    placeholder="Ej: ADMINISTRADOR"
                                    value={nombre}
                                    onChange={(e) => setNombre(e.target.value)}
                                    required
                                />
                            </div>

                            {/* 👇 CAMPO DESCRIPCIÓN NUEVO 👇 */}
                            <div className="mb-3">
                                <label className="form-label small fw-bold text-muted text-uppercase">Descripción</label>
                                <textarea
                                    className="form-control border-0 py-3 shadow-none"
                                    style={{ backgroundColor: '#f3f4f6', borderRadius: '12px', fontWeight: '500', resize: 'none' }}
                                    placeholder="Breve explicación de los permisos de este rol..."
                                    rows="3"
                                    value={descripcion}
                                    onChange={(e) => setDescripcion(e.target.value)}
                                ></textarea>
                            </div>
                        </div>

                        <div className="modal-footer border-0 p-4 pt-0 d-flex gap-3">
                            <button type="button" className="btn btn-light flex-grow-1 py-3 fw-bold text-muted" style={{ borderRadius: '12px' }} onClick={onClose}>
                                Cancelar
                            </button>
                            <button type="submit" className="btn btn-success flex-grow-1 py-3 fw-bold d-flex align-items-center justify-content-center gap-2" style={{ borderRadius: '12px', backgroundColor: '#10b981', border: 'none' }} disabled={enviando}>
                                {enviando ? <span className="spinner-border spinner-border-sm"></span> : <><Save size={18} /> Guardar</>}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}