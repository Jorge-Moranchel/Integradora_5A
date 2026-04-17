import React, { useState, useEffect } from 'react';
import Swal from 'sweetalert2';
import { Building2 } from 'lucide-react';

export default function DivisionModal({ show, onClose, onRefresh, divisionToEdit }) {
    const [nombre, setNombre] = useState('');

    useEffect(() => {
        if (divisionToEdit) {
            setNombre(divisionToEdit.nombre);
        } else {
            setNombre('');
        }
    }, [divisionToEdit, show]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        const url = divisionToEdit
            ? `http://localhost:8080/api/divisiones/actualizar/${divisionToEdit.id}`
            : 'http://localhost:8080/api/divisiones/guardar';
        const metodo = divisionToEdit ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method: metodo,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nombre })
            });

            if (response.ok) {
                Swal.fire('¡Éxito!', divisionToEdit ? 'División actualizada correctamente' : 'División registrada correctamente', 'success');
                setNombre('');
                onRefresh();
                onClose();
            } else {
                let msg = 'Error al guardar';
                try {
                    const data = await response.json();
                    msg = data?.mensaje || data?.message || msg;
                } catch (_) {
                    try { msg = await response.text(); } catch (_) {}
                }
                Swal.fire('Error', msg, 'error');
            }
        } catch (error) {
            Swal.fire('Error', 'No hay conexión con el servidor', 'error');
        }
    };

    if (!show) return null;

    return (
        <div className="modal d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)', zIndex: 1050 }}>
            <div className="modal-dialog modal-dialog-centered">
                <div className="modal-content border-0 shadow-lg rounded-4">
                    <div className="modal-header border-0 p-4 pb-0">
                        <div className="d-flex align-items-center gap-2">
                            <Building2 className="text-primary" size={24} />
                            <h4 className="fw-bold m-0">{divisionToEdit ? 'Editar División' : 'Nueva División'}</h4>
                        </div>
                        <button className="btn-close" onClick={onClose}></button>
                    </div>
                    <form onSubmit={handleSubmit}>
                        <div className="modal-body p-4">
                            <label className="form-label fw-bold small">Nombre de la División *</label>
                            <input
                                type="text"
                                className="form-control bg-light border-0 py-2"
                                placeholder="Ej: División de Tecnologías de la Información"
                                value={nombre}
                                onChange={(e) => setNombre(e.target.value)}
                                required
                            />
                        </div>
                        <div className="modal-footer border-0 p-4 pt-0 d-flex gap-2">
                            <button type="button" className="btn btn-light flex-grow-1 fw-bold" onClick={onClose}>
                                Cancelar
                            </button>
                            <button type="submit" className="btn btn-success flex-grow-1 fw-bold" style={{ backgroundColor: '#00a854' }}>
                                {divisionToEdit ? 'Guardar Cambios' : 'Registrar'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}