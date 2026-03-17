import React, { useState } from 'react';
import Swal from 'sweetalert2';
import { X, GraduationCap } from 'lucide-react';

export default function CarreraModal({ show, onClose, onRefresh }) {
    const [nombre, setNombre] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await fetch('http://localhost:8080/api/carreras/guardar', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nombre })
            });

            if (response.ok) {
                Swal.fire('¡Éxito!', 'Carrera registrada correctamente', 'success');
                setNombre(''); // Limpiar campo
                onRefresh();   // Recargar la tabla
                onClose();     // Cerrar modal
            } else {
                const error = await response.text();
                Swal.fire('Error', error, 'error');
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
                            <GraduationCap className="text-success" size={24} />
                            <h4 className="fw-bold m-0">Nueva Carrera</h4>
                        </div>
                        <button className="btn-close" onClick={onClose}></button>
                    </div>
                    <form onSubmit={handleSubmit}>
                        <div className="modal-body p-4">
                            <label className="form-label fw-bold small">Nombre de la Carrera</label>
                            <input
                                type="text"
                                className="form-control bg-light border-0 py-2"
                                placeholder="Ej: Desarrollo de Software Multiplataforma"
                                value={nombre}
                                onChange={(e) => setNombre(e.target.value)}
                                required
                            />
                        </div>
                        <div className="modal-footer border-0 p-4 pt-0 d-flex gap-2">
                            <button type="button" className="btn btn-light flex-grow-1 fw-bold" onClick={onClose}>Cancelar</button>
                            <button type="submit" className="btn btn-success flex-grow-1 fw-bold" style={{backgroundColor: '#00a854'}}>Registrar</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}