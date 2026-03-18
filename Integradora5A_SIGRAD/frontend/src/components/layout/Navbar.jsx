import React from 'react';
import { Calendar } from 'lucide-react';

export default function Navbar({ title }) {
    const fecha = new Date().toLocaleDateString('es-MX', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });

    return (
        <div className="d-flex justify-content-between align-items-center mb-5 bg-white p-3 shadow-sm rounded-4">
            <div>
                <h2 className="fw-bold m-0 text-dark" style={{ letterSpacing: '-1px' }}>{title}</h2>
                <p className="text-muted m-0 small d-flex align-items-center gap-2">
                    <Calendar size={14} /> {fecha}
                </p>
            </div>

            {/* Aquí puedes meter un buscador o notificaciones después */}
            <div className="d-flex gap-3">
                <div className="badge bg-light text-dark p-2 px-3 border rounded-pill">
                    Sistema Activo
                </div>
            </div>
        </div>
    );
}