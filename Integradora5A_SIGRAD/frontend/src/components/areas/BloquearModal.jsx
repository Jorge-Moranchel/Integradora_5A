import React, { useState } from 'react';
import { AlertTriangle } from 'lucide-react';

export default function BloquearModal({ show, onClose, fetchAreas, areaId }) {
    const [formData, setFormData] = useState({
        motivoBloqueo: '',
        fechaInicioBloqueo: '',
        fechaFinBloqueo: ''
    });

    if (!show) return null;

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (formData.fechaInicioBloqueo > formData.fechaFinBloqueo) {
            alert("La fecha de fin no puede ser menor a la de inicio.");
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/api/areas/bloquear/${areaId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData)
            });

            if (response.ok) {
                alert('Área bloqueada por mantenimiento');
                fetchAreas();
                onClose();
            } else {
                const errorDelServidor = await response.text();
                alert(`Fallo en el servidor (Código ${response.status}):\n${errorDelServidor}\n\nID enviado: ${areaId}`);
            }
        } catch (error) {
            console.error(error);
            alert('Error de conexión');
        }
    };

    return (
        <div className="modal d-block" style={{ backgroundColor: 'rgba(0,0,0,0.6)', zIndex: 1050 }}>
            <div className="modal-dialog modal-dialog-centered">
                <form onSubmit={handleSubmit} className="modal-content border-0 shadow-lg rounded-4">
                    <div className="modal-header border-0 p-4 pb-0">
                        <h4 className="modal-title fw-bold text-danger d-flex align-items-center gap-2">
                            <AlertTriangle /> Bloquear Área
                        </h4>
                        <button type="button" className="btn-close shadow-none" onClick={onClose}></button>
                    </div>
                    <div className="modal-body p-4">
                        <p className="text-muted small mb-4">Inhabilita esta zona para evitar reservaciones durante el mantenimiento.</p>

                        <div className="mb-3">
                            <label className="form-label small fw-bold">Motivo del Mantenimiento *</label>
                            <textarea required className="form-control bg-light border-0" rows="2"
                                      onChange={(e) => setFormData({...formData, motivoBloqueo: e.target.value})}></textarea>
                        </div>
                        <div className="row">
                            <div className="col-6 mb-3">
                                <label className="form-label small fw-bold">Fecha Inicio *</label>
                                <input type="date" required className="form-control bg-light border-0"
                                       onChange={(e) => setFormData({...formData, fechaInicioBloqueo: e.target.value})} />
                            </div>
                            <div className="col-6 mb-3">
                                <label className="form-label small fw-bold">Fecha Fin *</label>
                                <input type="date" required className="form-control bg-light border-0"
                                       onChange={(e) => setFormData({...formData, fechaFinBloqueo: e.target.value})} />
                            </div>
                        </div>
                    </div>
                    <div className="modal-footer border-0 p-4 pt-0 d-flex gap-3">
                        <button type="button" className="btn btn-outline-secondary flex-grow-1 py-2 fw-bold" onClick={onClose}>Cancelar</button>
                        <button type="submit" className="btn btn-danger flex-grow-1 py-2 fw-bold">Confirmar Bloqueo</button>
                    </div>
                </form>
            </div>
        </div>
    );
}