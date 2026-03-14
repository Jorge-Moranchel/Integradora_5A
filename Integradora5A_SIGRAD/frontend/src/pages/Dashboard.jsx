import React from 'react';
import StatCard from '../components/common/StatCard';
import { Calendar, Users, TrendingUp } from 'lucide-react';

export default function Dashboard() {
    const fechaActual = new Intl.DateTimeFormat('es-ES', {
        weekday: 'long', day: 'numeric', month: 'long', year: 'numeric'
    }).format(new Date());

    return (
        <div className="animate__animated animate__fadeIn">
            <header className="mb-4">
                <h2 className="fw-bold">Dashboard</h2>
                <p className="text-muted">Bienvenido al panel de administración - {fechaActual}</p>
            </header>

            <div className="row g-4 mb-5">
                <StatCard title="Reservas Activas" value="0" subText="+0 reservas hoy" icon={<Calendar className="text-primary"/>} />
                <StatCard title="Usuarios Registrados" value="0" subText="Sin datos" icon={<Users className="text-purple"/>} />
                <StatCard title="Tasa de Ocupación" value="0%" subText="Sin datos" icon={<TrendingUp className="text-orange"/>} />
            </div>

            <div className="row g-4">
                <div className="col-md-7">
                    <div className="card shadow-sm border-0 p-4" style={{ minHeight: '300px' }}>
                        <h5 className="fw-bold mb-4">Reservas Mensuales</h5>
                        <div className="d-flex flex-column align-items-center justify-content-center h-100 border border-dashed rounded py-5 text-muted">
                            <p>No hay datos suficientes para mostrar gráficas aún.</p>
                        </div>
                    </div>
                </div>
                <div className="col-md-5">
                    <div className="card shadow-sm border-0 p-4" style={{ minHeight: '300px' }}>
                        <h5 className="fw-bold mb-4">Reservas por Tipo de Cancha</h5>
                    </div>
                </div>
            </div>
        </div>
    );
}