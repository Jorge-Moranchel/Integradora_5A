import React from 'react';
import { MapPin, Clock, Edit, Trash2 } from 'lucide-react';

export default function AreaCard({ area }) {
    const badgeClass = area.estado === 'Disponible' ? 'badge-disponible' : 'badge-ocupada';

    return (
        <div className="card shadow-sm border-0 rounded-4 overflow-hidden h-100">
            <div className="bg-light d-flex align-items-center justify-content-center position-relative" style={{height: 180}}>
                <span className={`position-absolute top-0 end-0 m-3 ${badgeClass}`}>{area.estado}</span>
                <MapPin size={48} className="text-muted opacity-25" />
            </div>
            <div className="p-3">
                <h5 className="fw-bold mb-1">{area.nombre}</h5>
                <p className="text-muted small mb-3">{area.tipoDeporte}</p>
                <div className="d-flex align-items-center gap-2 text-muted small mb-4">
                    <Clock size={16} /> {area.horario}
                </div>
                <div className="d-flex gap-2">
                    <button className="btn btn-outline-dark flex-grow-1 d-flex align-items-center justify-content-center gap-2 py-2">
                        <Edit size={16}/> <small>Editar</small>
                    </button>
                    <button className="btn btn-outline-danger flex-grow-1 d-flex align-items-center justify-content-center gap-2 py-2">
                        <Trash2 size={16}/> <small>Eliminar</small>
                    </button>
                </div>
            </div>
        </div>
    );
}