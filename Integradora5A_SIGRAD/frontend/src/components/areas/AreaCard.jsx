import React from 'react';
import { MapPin, Clock, Edit, Trash2, Unlock } from 'lucide-react'; // Agregamos el ícono Unlock

export default function AreaCard({ area, onBloquear, onDesbloquear, onEditar }) {
    const getBadgeStyle = (estado) => {
        switch(estado?.toLowerCase()) {
            case 'disponible': return 'bg-success';
            case 'ocupada': return 'bg-danger';
            case 'bloqueada': return 'bg-warning text-dark';
            default: return 'bg-secondary';
        }
    };

    return (
        <div className="card shadow-sm border-0 rounded-4 overflow-hidden h-100">
            <div className="bg-light d-flex align-items-center justify-content-center position-relative" style={{height: 180}}>
                <span className={`position-absolute top-0 end-0 m-3 badge ${getBadgeStyle(area.estado)}`}>
                    {area.estado ? area.estado.toUpperCase() : 'DISPONIBLE'}
                </span>

                {area.imagen ? (
                    <img src={area.imagen} alt={area.nombre} style={{width: '100%', height: '100%', objectFit: 'cover'}} />
                ) : (
                    <MapPin size={48} className="text-muted opacity-25" />
                )}
            </div>

            <div className="p-3">
                <h5 className="fw-bold mb-1 text-truncate">{area.nombre}</h5>
                <p className="text-muted small mb-3 text-truncate">
                    <MapPin size={14} className="me-1"/> {area.ubicacion}
                </p>
                <div className="d-flex align-items-center gap-2 text-muted small mb-4">
                    <Clock size={16} /> {area.horaApertura} - {area.horaCierre}
                </div>
                <div className="d-flex gap-2">
                    <button
                        className="btn btn-outline-dark flex-grow-1 d-flex align-items-center justify-content-center gap-2 py-2"
                        onClick={() => onEditar(area)}
                    >
                        <Edit size={16}/> <small>Editar</small>
                    </button>

                    {/* LA MAGIA: Condición para mostrar un botón u otro */}
                    {area.estado?.toLowerCase() === 'bloqueada' ? (
                        <button
                            className="btn btn-outline-success flex-grow-1 d-flex align-items-center justify-content-center gap-2 py-2"
                            onClick={() => onDesbloquear(area.id)}
                        >
                            <Unlock size={16}/> <small>Desbloquear</small>
                        </button>
                    ) : (
                        <button
                            className="btn btn-outline-danger flex-grow-1 d-flex align-items-center justify-content-center gap-2 py-2"
                            onClick={() => onBloquear(area.id)}
                        >
                            <Trash2 size={16}/> <small>Bloquear</small>
                        </button>
                    )}

                </div>
            </div>
        </div>
    );
}