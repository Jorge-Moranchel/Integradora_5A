import React, { useState, useEffect } from 'react';
import { Download, Search, FileText, Plus, Edit, ChevronLeft, ChevronRight } from 'lucide-react';
import Swal from 'sweetalert2';
import ReservaModal from '../components/reservas/ReservaModal';

export default function Historial() {
    const [reservas, setReservas] = useState([]);
    const [loading, setLoading] = useState(true);
    const [busqueda, setBusqueda] = useState("");
    const [showModal, setShowModal] = useState(false);
    const [reservaAEditar, setReservaAEditar] = useState(null);

    // 👇 ESTADOS DE PAGINACIÓN BACKEND (Spring Boot empieza en página 0) 👇
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    // Cada vez que cambie la página o la búsqueda, llamamos al backend
    useEffect(() => {
        // Un pequeño retraso (debounce) para no saturar el backend si escriben muy rápido en el buscador
        const delayDebounceFn = setTimeout(() => {
            fetchReservas();
        }, 300);
        return () => clearTimeout(delayDebounceFn);
    }, [currentPage, busqueda]);

    const fetchReservas = async () => {
        setLoading(true);
        try {
            // Mandamos los parámetros exactos a nuestro nuevo endpoint de Spring Boot
            const response = await fetch(`http://localhost:8080/api/reservas/paginadas?page=${currentPage}&size=10&termino=${busqueda}`);
            if (response.ok) {
                const data = await response.json();
                setReservas(data.content); // Spring Boot guarda la lista dentro de "content"
                setTotalPages(data.totalPages);
                setTotalElements(data.totalElements);
            }
        } catch (error) {
            console.error("Error al obtener las reservas:", error);
        } finally {
            setLoading(false);
        }
    };

    // Si el usuario escribe en el buscador, lo regresamos a la página 1 (índice 0)
    const handleBusquedaChange = (e) => {
        setBusqueda(e.target.value);
        setCurrentPage(0);
    };

    const handleExport = () => {
        window.open('http://localhost:8080/api/reservas/exportar-pdf', '_blank');
    };

    const abrirModalNuevaReserva = () => {
        setReservaAEditar(null);
        setShowModal(true);
    };

    const abrirModalEditarReserva = (reserva) => {
        setReservaAEditar(reserva);
        setShowModal(true);
    };

    const handleCancelar = async (id) => {
        try {
            const response = await fetch(`http://localhost:8080/api/reservas/cancelar/${id}`, {
                method: 'PUT'
            });

            if (response.ok) {
                Swal.fire('Listo', 'Reserva cancelada correctamente', 'success');
                fetchReservas();
                return;
            }

            let msg = 'No se pudo cancelar la reserva';
            try {
                const data = await response.json();
                msg = data?.mensaje || data?.message || msg;
            } catch (_) {
                try { msg = await response.text(); } catch (_) {}
            }
            Swal.fire('Error', msg, 'error');
        } catch (e) {
            Swal.fire('Error', 'Backend no responde', 'error');
        }
    };

    return (
        <div className="animate__animated animate__fadeIn p-4 bg-light" style={{ minHeight: '100vh' }}>
            <div className="d-flex justify-content-between align-items-center mb-5">
                <div>
                    <h2 className="fw-bold m-0" style={{ fontSize: '2.5rem', letterSpacing: '-1.5px' }}>Historial de Reservas</h2>
                    <p className="text-muted m-0 mt-1">Visualiza y gestiona todas las reservas del sistema</p>
                </div>
                <div className="d-flex align-items-center gap-2">
                    <button
                        className="btn btn-success d-flex align-items-center gap-2 px-4 py-2 fw-bold shadow-sm"
                        style={{ borderRadius: '12px', backgroundColor: '#10b981', border: 'none' }}
                        onClick={abrirModalNuevaReserva}
                    >
                        <Plus size={20} /> Nueva Reserva
                    </button>
                    <button
                        className="btn btn-primary d-flex align-items-center gap-2 px-4 py-2 fw-bold shadow-sm"
                        style={{ borderRadius: '12px', border: 'none' }}
                        onClick={handleExport}
                        disabled={totalElements === 0}
                    >
                        <Download size={20} /> Exportar
                    </button>
                </div>
            </div>

            <div className="card border-0 shadow-sm p-4 rounded-4 bg-white">
                <div className="d-flex gap-3 mb-4">
                    <div className="input-group" style={{ maxWidth: '450px' }}>
                        <span className="input-group-text bg-light border-0">
                            <Search size={18} className="text-muted" />
                        </span>
                        <input
                            type="text"
                            className="form-control bg-light border-0 shadow-none py-2"
                            placeholder="Buscar por usuario, rol o cancha..."
                            value={busqueda}
                            onChange={handleBusquedaChange}
                            style={{ borderRadius: '0 12px 12px 0' }}
                        />
                    </div>
                </div>

                {loading ? (
                    <div className="d-flex justify-content-center py-5 my-5">
                        <div className="spinner-border text-primary" role="status" style={{ width: '3rem', height: '3rem' }}>
                            <span className="visually-hidden">Cargando...</span>
                        </div>
                    </div>
                ) : reservas.length > 0 ? (
                    <>
                        <div className="table-responsive">
                            <table className="table table-hover align-middle">
                                <thead className="table-light">
                                <tr className="small text-muted text-uppercase fw-bold">
                                    <th className="ps-4 py-3">ID</th>
                                    <th>Usuario</th>
                                    <th>Rol</th>
                                    <th>Área Deportiva</th>
                                    <th>Fecha</th>
                                    <th>Horario</th>
                                    <th className="text-center">Estado</th>
                                    <th className="text-center pe-4">Acciones</th>
                                </tr>
                                </thead>
                                <tbody>
                                {reservas.map((reserva) => (
                                    <tr key={reserva.id} className="border-bottom border-light">
                                        <td className="ps-4 fw-bold text-muted">#{reserva.id}</td>
                                        <td className="fw-semibold text-dark">{reserva.usuario ? reserva.usuario.nombre : 'N/A'}</td>
                                        <td>
                                            <span className="badge bg-info bg-opacity-10 text-info fw-bold">
                                                {reserva.usuario && reserva.usuario.rol ? reserva.usuario.rol : 'N/A'}
                                            </span>
                                        </td>
                                        <td className="text-dark">{reserva.area ? reserva.area.nombre : 'N/A'}</td>
                                        <td className="text-muted small fw-semibold">{reserva.fecha}</td>
                                        <td className="text-muted small fw-semibold">{reserva.horaInicio} - {reserva.horaFin}</td>
                                        <td className="text-center">
                                            <span className={`badge ${
                                                reserva.estado === 'CONFIRMADA' ? 'bg-success bg-opacity-10 text-success' :
                                                    reserva.estado === 'CANCELADA' ? 'bg-danger bg-opacity-10 text-danger' : 'bg-secondary bg-opacity-10 text-secondary'
                                            }`} style={{ fontSize: '11px', letterSpacing: '0.5px' }}>
                                                {reserva.estado}
                                            </span>
                                        </td>
                                        <td className="text-center pe-4">
                                            <div className="d-flex justify-content-center gap-2">
                                                <button
                                                    className="btn btn-sm btn-light border-0 p-2 shadow-sm rounded-3 text-primary"
                                                    onClick={() => abrirModalEditarReserva(reserva)}
                                                    disabled={reserva.estado === 'COMPLETADA' || reserva.estado === 'CANCELADA'}
                                                    title={reserva.estado === 'COMPLETADA' || reserva.estado === 'CANCELADA' ? 'Solo se pueden editar reservas CONFIRMADAS' : 'Editar reserva'}
                                                >
                                                    <Edit size={16} />
                                                </button>

                                                {reserva.estado === 'CONFIRMADA' ? (
                                                    <button
                                                        className="btn btn-sm btn-outline-danger fw-bold rounded-3 px-3"
                                                        onClick={() => handleCancelar(reserva.id)}
                                                    >
                                                        Cancelar
                                                    </button>
                                                ) : (
                                                    <button className="btn btn-sm btn-light text-muted fw-bold rounded-3 px-3" disabled>
                                                        Cancelar
                                                    </button>
                                                )}
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>

                        {/* 👇 PAGINADOR CONECTADO AL BACKEND 👇 */}
                        {totalPages > 1 && (
                            <div className="d-flex justify-content-between align-items-center mt-4 pt-3 border-top border-light">
                                <span className="text-muted small">
                                    Mostrando página <b>{currentPage + 1}</b> de <b>{totalPages}</b> (Total: {totalElements} reservas)
                                </span>
                                <div className="d-flex gap-1">
                                    <button
                                        className="btn btn-light border btn-sm px-2 rounded-3"
                                        onClick={() => setCurrentPage(p => Math.max(0, p - 1))}
                                        disabled={currentPage === 0}
                                    >
                                        <ChevronLeft size={16} />
                                    </button>

                                    {[...Array(totalPages)].map((_, i) => (
                                        <button
                                            key={i}
                                            className={`btn btn-sm px-3 rounded-3 ${currentPage === i ? 'btn-primary shadow-sm' : 'btn-light border text-primary'}`}
                                            onClick={() => setCurrentPage(i)}
                                            style={{ fontWeight: 'bold' }}
                                        >
                                            {i + 1}
                                        </button>
                                    ))}

                                    <button
                                        className="btn btn-light border btn-sm px-2 rounded-3"
                                        onClick={() => setCurrentPage(p => Math.min(totalPages - 1, p + 1))}
                                        disabled={currentPage === totalPages - 1}
                                    >
                                        <ChevronRight size={16} />
                                    </button>
                                </div>
                            </div>
                        )}
                    </>
                ) : (
                    <div className="text-center py-5 opacity-50">
                        <FileText size={70} className="text-muted mb-4" strokeWidth={1.5} />
                        <h4 className="fw-bold text-dark">No se encontraron registros</h4>
                        <p className="text-muted m-0">
                            {busqueda ? "No hay coincidencias para tu búsqueda." : "Las reservas completadas aparecerán listadas aquí."}
                        </p>
                    </div>
                )}
            </div>

            <ReservaModal
                show={showModal}
                onClose={() => setShowModal(false)}
                onSuccess={fetchReservas}
                reservaAEditar={reservaAEditar}
            />
        </div>
    );
}