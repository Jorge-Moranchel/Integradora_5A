import React, { useState, useEffect } from 'react';
import StatCard from '../components/common/StatCard';
import { Calendar, Users, TrendingUp } from 'lucide-react';
// Importamos los componentes mágicos de Recharts
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, ResponsiveContainer, PieChart, Pie, Cell, Legend } from 'recharts';

export default function Dashboard() {
    const [stats, setStats] = useState({
        reservasActivas: 0, reservasHoy: 0, usuariosRegistrados: 0, tasaOcupacion: 0, reservasPorArea: {}, reservasPorMes: {}
    });
    const [loading, setLoading] = useState(true);

    const fechaActual = new Intl.DateTimeFormat('es-ES', {
        weekday: 'long', day: 'numeric', month: 'long', year: 'numeric'
    }).format(new Date());

    useEffect(() => {
        fetch('http://localhost:8080/api/dashboard/stats')
            .then(response => {
                if (!response.ok) throw new Error('Error al conectar con el servidor');
                return response.json();
            })
            .then(data => {
                setStats(data);
                setLoading(false);
            })
            .catch(error => {
                console.error('Error fetching dashboard stats:', error);
                setLoading(false);
            });
    }, []);

    // --- TRANSFORMACIÓN DE DATOS PARA LAS GRÁFICAS ---
    // Convertimos el mapa de Java en un arreglo de objetos para la gráfica de Barras
    const dataMeses = Object.entries(stats.reservasPorMes).map(([mes, cantidad]) => ({
        mes: mes,
        reservas: cantidad
    }));

    // Convertimos el mapa de Java en un arreglo de objetos para la gráfica de Dona
    const dataAreas = Object.entries(stats.reservasPorArea).map(([area, cantidad]) => ({
        name: area,
        value: cantidad
    }));

    // Paleta de colores estilo Bootstrap para las rebanadas de la dona
    const COLORS = ['#0d6efd', '#198754', '#ffc107', '#dc3545', '#6f42c1', '#0dcaf0'];

    return (
        <div className="animate__animated animate__fadeIn">
            <header className="mb-4">
                <h2 className="fw-bold">Dashboard</h2>
                <p className="text-muted">Bienvenido al panel de administración - {fechaActual}</p>
            </header>

            {loading ? (
                <div className="d-flex justify-content-center py-5">
                    <div className="spinner-border text-primary" role="status">
                        <span className="visually-hidden">Cargando...</span>
                    </div>
                </div>
            ) : (
                <>
                    {/* Tarjetas Superiores */}
                    <div className="row g-4 mb-5">
                        <StatCard title="Reservas Activas" value={stats.reservasActivas} subText={`Resevas del dia de hoy: ${stats.reservasHoy}`} icon={<Calendar className="text-primary"/>} />
                        <StatCard title="Usuarios Registrados" value={stats.usuariosRegistrados} subText="En todo el sistema" icon={<Users className="text-purple"/>} />
                        <StatCard title="Tasa de Ocupación" value={`${stats.tasaOcupacion}%`} subText="Capacidad diaria actual" icon={<TrendingUp className="text-orange"/>} />
                    </div>

                    {/* Sección de Gráficas */}
                    <div className="row g-4 mb-4">

                        {/* Gráfica de Barras (Meses) */}
                        <div className="col-lg-7 col-md-12">
                            <div className="card shadow-sm border-0 p-4 h-100" style={{ minHeight: '350px' }}>
                                <h5 className="fw-bold mb-4 text-secondary">Evolución de Reservas Mensuales</h5>
                                {dataMeses.length > 0 ? (
                                    <ResponsiveContainer width="100%" height={300}>
                                        <BarChart data={dataMeses} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                                            <CartesianGrid strokeDasharray="3 3" vertical={false} opacity={0.3} />
                                            <XAxis dataKey="mes" tick={{fontSize: 12}} />
                                            <YAxis allowDecimals={false} tick={{fontSize: 12}} />
                                            <RechartsTooltip cursor={{fill: '#f8f9fa'}} borderRadius={8} />
                                            <Bar dataKey="reservas" fill="#0d6efd" radius={[4, 4, 0, 0]} barSize={40} name="Reservas" />
                                        </BarChart>
                                    </ResponsiveContainer>
                                ) : (
                                    <div className="d-flex flex-column align-items-center justify-content-center h-100 text-muted">
                                        <p>No hay datos suficientes</p>
                                    </div>
                                )}
                            </div>
                        </div>

                        {/* Gráfica de Dona (Áreas) */}
                        <div className="col-lg-5 col-md-12">
                            <div className="card shadow-sm border-0 p-4 h-100" style={{ minHeight: '350px' }}>
                                <h5 className="fw-bold mb-4 text-secondary">Total de Reservas por Área Deportiva</h5>
                                {dataAreas.length > 0 ? (
                                    <ResponsiveContainer width="100%" height={300}>
                                        <PieChart>
                                            <Pie
                                                data={dataAreas}
                                                cx="50%"
                                                cy="45%"
                                                innerRadius={60}
                                                outerRadius={90}
                                                paddingAngle={5}
                                                dataKey="value"
                                            >
                                                {dataAreas.map((entry, index) => (
                                                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                                                ))}
                                            </Pie>
                                            <RechartsTooltip />
                                            <Legend verticalAlign="bottom" height={36} iconType="circle" wrapperStyle={{ fontSize: '12px' }}/>
                                        </PieChart>
                                    </ResponsiveContainer>
                                ) : (
                                    <div className="d-flex flex-column align-items-center justify-content-center h-100 text-muted">
                                        <p>Sin reservas registradas</p>
                                    </div>
                                )}
                            </div>
                        </div>

                    </div>
                </>
            )}
        </div>
    );
}