import React, { useState, useEffect } from 'react';
import { Download, Search, FileText } from 'lucide-react';

export default function Historial() {
    const [reservas, setReservas] = useState([]);
    const [loading, setLoading] = useState(true);
    const [busqueda, setBusqueda] = useState("");

    useEffect(() => {
        fetchReservas();
    }, []);

    const fetchReservas = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/reservas/listar');
            if (response.ok) {
                const data = await response.json();
                setReservas(data);
            }
        } catch (error) {
            console.error("Error al obtener las reservas:", error);
        } finally {
            setLoading(false);
        }
    };

    const handleExport = () => {
        window.open('http://localhost:8080/api/reservas/exportar-pdf', '_blank');
    };

    // Filtro actualizado para incluir la búsqueda por Rol
    const reservasFiltradas = reservas.filter((reserva) => {
        const nombreUsuario = reserva.usuario?.nombre?.toLowerCase() || "";
        const rolUsuario = reserva.usuario?.rol?.toLowerCase() || "";
        const nombreArea = reserva.area?.nombre?.toLowerCase() || "";
        const terminoBusqueda = busqueda.toLowerCase();

        return nombreUsuario.includes(terminoBusqueda) ||
            nombreArea.includes(terminoBusqueda) ||
            rolUsuario.includes(terminoBusqueda);
    });

    return (
        <div className="animate__animated animate__fadeIn">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold">Historial de Reservas</h2>
                    <p className="text-muted">Visualiza y gestiona todas las reservas del sistema</p>
                </div>
                <button
                    className="btn btn-success d-flex align-items-center gap-2 px-4 py-2 fw-bold"
                    onClick={handleExport}
                    disabled={reservas.length === 0}
                >
                    <Download size={20} /> Exportar
                </button>
            </div>

            <div className="card border-0 shadow-sm p-4">
                <div className="d-flex gap-3 mb-4">
                    <div className="input-group" style={{ maxWidth: '400px' }}>
                        <span className="input-group-text bg-white border-end-0">
                            <Search size={18} className="text-muted" />
                        </span>
                        <input
                            type="text"
                            className="form-control border-start-0 shadow-none"
                            placeholder="Buscar por usuario, rol o cancha..."
                            value={busqueda}
                            onChange={(e) => setBusqueda(e.target.value)}
                        />
                    </div>
                </div>

                {loading ? (
                    <div className="text-center py-5">
                        <div className="spinner-border text-primary" role="status"></div>
                        <p className="mt-2 text-muted">Cargando reservas...</p>
                    </div>
                ) : reservasFiltradas.length > 0 ? (
                    <div className="table-responsive">
                        <table className="table table-hover align-middle">
                            <thead className="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Usuario</th>
                                <th>Rol</th> {/* NUEVA COLUMNA */}
                                <th>Área Deportiva</th>
                                <th>Fecha</th>
                                <th>Horario</th>
                                <th>Estado</th>
                            </tr>
                            </thead>
                            <tbody>
                            {reservasFiltradas.map((reserva) => (
                                <tr key={reserva.id}>
                                    <td className="fw-bold text-muted">#{reserva.id}</td>
                                    <td>{reserva.usuario ? reserva.usuario.nombre : 'N/A'}</td>

                                    {/* MOSTRAMOS EL ROL COMO UNA ETIQUETA PARA QUE SE VEA MEJOR */}
                                    <td>
                                            <span className="badge bg-info text-dark">
                                                {reserva.usuario && reserva.usuario.rol ? reserva.usuario.rol : 'N/A'}
                                            </span>
                                    </td>

                                    <td>{reserva.area ? reserva.area.nombre : 'N/A'}</td>
                                    <td>{reserva.fecha}</td>
                                    <td>{reserva.horaInicio} - {reserva.horaFin}</td>
                                    <td>
                                            <span className={`badge ${
                                                reserva.estado === 'CONFIRMADA' ? 'bg-success' :
                                                    reserva.estado === 'CANCELADA' ? 'bg-danger' : 'bg-secondary'
                                            }`}>
                                                {reserva.estado}
                                            </span>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                ) : (
                    <div className="text-center py-5">
                        <FileText size={60} className="text-muted mb-3 opacity-25" strokeWidth={1} />
                        <h4 className="fw-bold text-muted">No se encontraron registros</h4>
                        <p className="text-muted">
                            {busqueda ? "No hay coincidencias para tu búsqueda." : "Las reservas completadas aparecerán listadas aquí."}
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
}