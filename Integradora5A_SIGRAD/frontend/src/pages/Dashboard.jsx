import React, { useState, useEffect } from 'react';
import StatCard from '../components/common/StatCard';
// 👇 AQUÍ ESTABA EL ERROR: Faltaba importar CheckCircle y XCircle 👇
import { Calendar, CheckCircle, XCircle } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, ResponsiveContainer, PieChart, Pie, Cell, Legend } from 'recharts';

export default function Dashboard() {
    const [stats, setStats] = useState({
        reservasActivas: 0,
        reservasHoy: 0,
        usuariosRegistrados: 0,
        tasaOcupacion: 0,
        reservasPorArea: {},
        reservasPorMes: {}
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

    const dataMeses = Object.entries(stats.reservasPorMes || {}).map(([mes, cantidad]) => ({
        mes,
        reservas: cantidad
    }));

    const dataAreas = Object.entries(stats.reservasPorArea || {}).map(([area, cantidad]) => ({
        name: area,
        value: cantidad
    }));

    const COLORS = ['#0d6efd', '#198754', '#ffc107', '#dc3545', '#6f42c1', '#0dcaf0'];

    return (
        <div className="animate__animated animate__fadeIn p-4">
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
                    <div className="row g-4 mb-5">
                        {/* Tarjeta Activas (Tu azul actual) */}
                        <StatCard
                            title="Reservas Activas"
                            value={stats.reservasActivas || 0}
                            subText={`Hoy: ${stats.reservasHoy || 0}`}
                            icon={<Calendar className="text-primary"/>}
                        />

                        {/* NUEVA Tarjeta Completadas (Verde) */}
                        <StatCard
                            title="Completadas"
                            value={stats.reservasCompletadas || 0}
                            subText="Histórico exitoso"
                            icon={<CheckCircle className="text-success"/>}
                        />

                        {/* NUEVA Tarjeta Canceladas (Rojo) */}
                        <StatCard
                            title="Canceladas"
                            value={stats.reservasCanceladas || 0}
                            subText="Reservas anuladas"
                            icon={<XCircle className="text-danger"/>}
                        />
                    </div>

                    <div className="row g-4 mb-4">
                        {/* Gráfica de Barras */}
                        <div className="col-lg-7">
                            <div className="card shadow-sm border-0 p-4 h-100">
                                <h5 className="fw-bold mb-4">Evolución Mensual</h5>
                                <div style={{ width: '100%', height: 300 }}>
                                    <ResponsiveContainer>
                                        <BarChart data={dataMeses}>
                                            <CartesianGrid strokeDasharray="3 3" vertical={false} />
                                            <XAxis dataKey="mes" />
                                            <YAxis allowDecimals={false} />
                                            <RechartsTooltip cursor={{fill: '#f8f9fa'}} />
                                            <Bar dataKey="reservas" fill="#0d6efd" radius={[4, 4, 0, 0]} />
                                        </BarChart>
                                    </ResponsiveContainer>
                                </div>
                            </div>
                        </div>

                        {/* Gráfica de Dona */}
                        <div className="col-lg-5">
                            <div className="card shadow-sm border-0 p-4 h-100">
                                <h5 className="fw-bold mb-4">Reservas por Área</h5>
                                <div style={{ width: '100%', height: 300 }}>
                                    <ResponsiveContainer>
                                        <PieChart>
                                            <Pie
                                                data={dataAreas}
                                                innerRadius={60}
                                                outerRadius={80}
                                                paddingAngle={5}
                                                dataKey="value"
                                            >
                                                {dataAreas.map((entry, index) => (
                                                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                                                ))}
                                            </Pie>
                                            <RechartsTooltip />
                                            <Legend />
                                        </PieChart>
                                    </ResponsiveContainer>
                                </div>
                            </div>
                        </div>
                    </div>
                </>
            )}
        </div>
    );
}