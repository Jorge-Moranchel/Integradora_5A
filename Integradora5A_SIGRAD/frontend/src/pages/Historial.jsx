import React from 'react';
import { Download, Search, FileText } from 'lucide-react';

export default function Historial() {
    const handleExport = () => {
        // Aquí llamarás al endpoint del backend que genera el PDF
        window.open('http://localhost:8080/api/historial/exportar', '_blank');
    };

    return (
        <div className="animate__animated animate__fadeIn">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold">Historial de Reservas</h2>
                    <p className="text-muted">Visualiza y gestiona todas las reservas del sistema</p>
                </div>
                <button className="btn btn-success d-flex align-items-center gap-2 px-4 py-2 fw-bold" onClick={handleExport}>
                    <Download size={20} /> Exportar
                </button>
            </div>

            <div className="card border-0 shadow-sm p-4">
                <div className="d-flex gap-3 mb-4">
                    <div className="input-group" style={{ maxWidth: '400px' }}>
                        <span className="input-group-text bg-white border-end-0"><Search size={18} className="text-muted" /></span>
                        <input type="text" className="form-control border-start-0 shadow-none" placeholder="Buscar por usuario o cancha..." />
                    </div>
                </div>

                <div className="text-center py-5">
                    <FileText size={60} className="text-muted mb-3 opacity-25" strokeWidth={1} />
                    <h4 className="fw-bold text-muted">No hay registros de historial</h4>
                    <p className="text-muted">Las reservas completadas aparecerán listadas aquí.</p>
                </div>
            </div>
        </div>
    );
}