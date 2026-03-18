import React, { useState, useEffect } from 'react';
import { Plus, Search, Edit, Power, SearchIcon, GraduationCap, ChevronLeft, ChevronRight } from "lucide-react";
import Swal from 'sweetalert2';
import CarreraModal from '../components/carreras/CarreraModal';

export default function Carreras() {
    const [showModal, setShowModal] = useState(false);
    const [listaCarreras, setListaCarreras] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [filtro, setFiltro] = useState('Todas');
    const [carreraParaEditar, setCarreraParaEditar] = useState(null);

    // --- NUEVOS ESTADOS PARA PAGINACIÓN ---
    const [paginaActual, setPaginaActual] = useState(1);
    const elementosPorPagina = 10;

    const obtenerCarreras = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/carreras/listar');
            if (response.ok) {
                const data = await response.json();
                setListaCarreras(Array.isArray(data) ? data : []);
            } else {
                setListaCarreras([]);
            }
        } catch (error) {
            console.error("Error al obtener carreras:", error);
            setListaCarreras([]);
        }
    };

    useEffect(() => {
        obtenerCarreras();
    }, []);

    // Si el usuario busca algo o cambia de filtro, lo regresamos a la página 1
    useEffect(() => {
        setPaginaActual(1);
    }, [searchTerm, filtro]);

    const handleCambiarEstado = async (id, estadoActual) => {
        const accion = estadoActual ? 'inhabilitar' : 'habilitar';
        const result = await Swal.fire({
            title: `¿${accion.charAt(0).toUpperCase() + accion.slice(1)} esta carrera?`,
            text: estadoActual ? "Los alumnos no podrán seleccionarla." : "Volverá a estar disponible.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: estadoActual ? '#dc3545' : '#198754',
            cancelButtonColor: '#6c757d',
            confirmButtonText: `Sí, ${accion}`
        });

        if (result.isConfirmed) {
            try {
                const response = await fetch(`http://localhost:8080/api/carreras/estado/${id}`, { method: 'PUT' });
                if (response.ok) {
                    Swal.fire('¡Listo!', 'El estado ha sido actualizado.', 'success');
                    obtenerCarreras();
                } else {
                    Swal.fire('Error', 'No se pudo actualizar el estado.', 'error');
                }
            } catch (error) {
                Swal.fire('Error', 'Problema de conexión con el servidor.', 'error');
            }
        }
    };

    // 1. Primero filtramos la lista original
    const carrerasFiltradas = listaCarreras.filter(carrera => {
        const coincideTexto = carrera.nombre.toLowerCase().includes(searchTerm.toLowerCase());
        let coincideFiltro = true;
        if (filtro === 'Habilitadas') coincideFiltro = carrera.habilitada === true;
        if (filtro === 'Inhabilitadas') coincideFiltro = carrera.habilitada === false;
        return coincideTexto && coincideFiltro;
    });

    // --- 2. LÓGICA MATEMÁTICA DE PAGINACIÓN ---
    const indiceUltimoElemento = paginaActual * elementosPorPagina;
    const indicePrimerElemento = indiceUltimoElemento - elementosPorPagina;
    // Cortamos la lista para obtener solo los 10 de esta página
    const carrerasPaginadas = carrerasFiltradas.slice(indicePrimerElemento, indiceUltimoElemento);
    // Calculamos el total de páginas necesarias
    const totalPaginas = Math.ceil(carrerasFiltradas.length / elementosPorPagina);

    // Funciones para los botones de Siguiente y Anterior
    const paginaSiguiente = () => {
        if (paginaActual < totalPaginas) setPaginaActual(paginaActual + 1);
    };
    const paginaAnterior = () => {
        if (paginaActual > 1) setPaginaActual(paginaActual - 1);
    };

    const cardStyle = { borderRadius: '15px', border: '1px solid #e9ecef', padding: '2rem' };

    return (
        <div className="p-5 animate__animated animate__fadeIn bg-light" style={{ minHeight: '100vh' }}>
            <div className="d-flex justify-content-between align-items-center mb-5">
                <div>
                    <h2 className="fw-bold m-0" style={{ fontSize: '2.5rem' }}>Administración de Carreras</h2>
                    <p className="text-muted m-0 mt-1">Gestiona las carreras disponibles para el registro de usuarios</p>
                </div>
                <button
                    className="btn btn-success d-flex align-items-center gap-2 fw-bold"
                    style={{ borderRadius: '10px', padding: '12px 24px', backgroundColor: '#00a854', border: 'none' }}
                    onClick={() => { setCarreraParaEditar(null); setShowModal(true); }}
                >
                    <Plus size={20} /> Nueva Carrera
                </button>
            </div>

            <div className="card shadow-sm bg-white" style={{ ...cardStyle, padding: '1.5rem' }}>
                <div className="d-flex justify-content-between align-items-center mb-4 gap-3">
                    <div className="input-group flex-grow-1" style={{ maxWidth: '500px' }}>
                        <span className="input-group-text bg-light border-0"><Search size={18} className="text-muted" /></span>
                        <input
                            type="text"
                            className="form-control bg-light border-0 shadow-none py-2"
                            placeholder="Buscar carrera..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            style={{ borderRadius: '0 10px 10px 0' }}
                        />
                    </div>
                    <div className="d-flex gap-2">
                        {['Todas', 'Habilitadas', 'Inhabilitadas'].map(estado => (
                            <button
                                key={estado}
                                className={`btn px-4 py-2 ${filtro === estado ? 'bg-dark text-white fw-bold' : 'bg-white text-muted border'}`}
                                style={{ borderRadius: '10px', transition: '0.3s' }}
                                onClick={() => setFiltro(estado)}
                            >
                                {estado}
                            </button>
                        ))}
                    </div>
                </div>

                <div className="table-responsive">
                    <table className="table table-hover align-middle">
                        <thead className="table-light">
                        <tr className="small text-muted">
                            <th style={{ width: '80px' }}>ID</th>
                            <th>NOMBRE DE LA CARRERA</th>
                            <th className="text-center">ESTADO</th>
                            <th className="text-center">ACCIONES</th>
                        </tr>
                        </thead>
                        <tbody>
                        {/* 3. DIBUJAMOS SOLO LA LISTA PAGINADA (carrerasPaginadas) */}
                        {carrerasPaginadas.length > 0 ? (
                            carrerasPaginadas.map((carrera) => (
                                <tr key={carrera.id}>
                                    <td className="text-muted">#{carrera.id}</td>
                                    <td>
                                        <div className="d-flex align-items-center gap-2">
                                            <div className={`p-2 rounded ${carrera.habilitada ? 'bg-success-subtle text-success' : 'bg-secondary-subtle text-secondary'}`}>
                                                <GraduationCap size={18} />
                                            </div>
                                            <span className={`fw-bold ${!carrera.habilitada && 'text-decoration-line-through text-muted'}`}>
                                                {carrera.nombre}
                                            </span>
                                        </div>
                                    </td>
                                    <td className="text-center">
                                        {carrera.habilitada ? (
                                            <span className="badge rounded-pill bg-success-subtle text-success px-3">Habilitada</span>
                                        ) : (
                                            <span className="badge rounded-pill bg-danger-subtle text-danger px-3">Inhabilitada</span>
                                        )}
                                    </td>
                                    <td className="text-center">
                                        <div className="d-flex justify-content-center gap-2">
                                            <button
                                                className="btn btn-light btn-sm border"
                                                title="Editar"
                                                onClick={() => { setCarreraParaEditar(carrera); setShowModal(true); }}
                                            >
                                                <Edit size={16} className="text-primary" />
                                            </button>
                                            <button
                                                className="btn btn-light btn-sm border"
                                                title={carrera.habilitada ? "Inhabilitar" : "Habilitar"}
                                                onClick={() => handleCambiarEstado(carrera.id, carrera.habilitada)}
                                            >
                                                <Power size={16} className={carrera.habilitada ? "text-danger" : "text-success"} />
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="4" className="text-center py-5 opacity-50">
                                    <SearchIcon size={50} className="mb-3" />
                                    <p className="m-0">No se encontraron carreras.</p>
                                </td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>

                {/* --- 4. CONTROLES DEL PAGINADOR --- */}
                {totalPaginas > 1 && (
                    <div className="d-flex justify-content-between align-items-center mt-4 pt-3 border-top">
                        <span className="text-muted small">
                            Mostrando {indicePrimerElemento + 1} a {Math.min(indiceUltimoElemento, carrerasFiltradas.length)} de {carrerasFiltradas.length} carreras
                        </span>

                        <div className="d-flex gap-2 align-items-center">
                            <button
                                className="btn btn-outline-secondary btn-sm d-flex align-items-center"
                                onClick={paginaAnterior}
                                disabled={paginaActual === 1}
                            >
                                <ChevronLeft size={16} /> Anterior
                            </button>

                            <span className="badge bg-light text-dark border px-3 py-2">
                                Página {paginaActual} de {totalPaginas}
                            </span>

                            <button
                                className="btn btn-outline-secondary btn-sm d-flex align-items-center"
                                onClick={paginaSiguiente}
                                disabled={paginaActual === totalPaginas}
                            >
                                Siguiente <ChevronRight size={16} />
                            </button>
                        </div>
                    </div>
                )}

            </div>

            <CarreraModal
                show={showModal}
                onClose={() => setShowModal(false)}
                onRefresh={obtenerCarreras}
                carreraToEdit={carreraParaEditar}
            />
        </div>
    );
}