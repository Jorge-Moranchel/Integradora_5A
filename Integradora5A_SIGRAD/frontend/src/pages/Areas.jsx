import React, { useState, useEffect } from 'react';
import { Search, Plus, MapPin, ChevronLeft, ChevronRight } from 'lucide-react';
import Swal from 'sweetalert2';
import AreaModal from '../components/areas/AreaModal';
import AreaCard from '../components/areas/AreaCard';
import BloquearModal from '../components/areas/BloquearModal';

export default function Areas() {
    // ESTADOS
    const [showModal, setShowModal] = useState(false);
    const [areas, setAreas] = useState([]);
    const [showBloquearModal, setShowBloquearModal] = useState(false);
    const [areaSeleccionada, setAreaSeleccionada] = useState(null);
    const [areaParaEditar, setAreaParaEditar] = useState(null);

    // 👇 NUEVO ESTADO PARA EL LOADING 👇
    const [isLoading, setIsLoading] = useState(true);

    // FILTROS Y BÚSQUEDA
    const [searchTerm, setSearchTerm] = useState('');
    const [activeFilter, setActiveFilter] = useState('Todos');

    // PAGINACIÓN
    const [paginaActual, setPaginaActual] = useState(1);
    const elementosPorPagina = 6;

    const fetchAreas = async () => {
        setIsLoading(true); // Encendemos el loading antes de buscar
        try {
            const response = await fetch('http://localhost:8080/api/areas/listar');
            if (response.ok) {
                const data = await response.json();
                setAreas(Array.isArray(data) ? data : []);
            }
        } catch (error) {
            console.error("Error al traer las áreas:", error);
        } finally {
            setIsLoading(false); // Apagamos el loading sin importar si falló o fue exitoso
        }
    };

    useEffect(() => {
        fetchAreas();
    }, []);

    // Resetear página al buscar o filtrar
    useEffect(() => {
        setPaginaActual(1);
    }, [searchTerm, activeFilter]);

    // === MENSAJE INTERACTIVO PARA DESBLOQUEAR AREA ===
    const handleDesbloquear = async (id) => {
        Swal.fire({
            title: '¿Habilitar área?',
            text: "El área volverá a estar disponible para reservaciones.",
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#10b981',
            cancelButtonColor: '#6c757d',
            confirmButtonText: 'Sí, habilitar',
            cancelButtonText: 'Cancelar',
            reverseButtons: true,
            borderRadius: '15px'
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    const response = await fetch(`http://localhost:8080/api/areas/desbloquear/${id}`, {
                        method: 'PUT'
                    });

                    if (response.ok) {
                        Swal.fire({
                            title: '¡Área Habilitada!',
                            text: 'La cancha ya puede recibir reservas.',
                            icon: 'success',
                            timer: 2000,
                            showConfirmButton: false,
                            borderRadius: '15px'
                        });
                        fetchAreas(); // Refrescar lista automáticamente
                    } else {
                        Swal.fire('Error', 'No se pudo habilitar el área.', 'error');
                    }
                } catch (error) {
                    Swal.fire('Error', 'Problema de conexión con el servidor.', 'error');
                }
            }
        });
    };

    // LÓGICA DE FILTRADO
    const filteredAreas = areas.filter(area => {
        const matchesSearch = area.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
            area.ubicacion.toLowerCase().includes(searchTerm.toLowerCase());

        let matchesFilter = true;
        if (activeFilter === 'Disponible') {
            matchesFilter = area.estado?.toLowerCase() === 'disponible';
        } else if (activeFilter === 'Mantenimiento') {
            matchesFilter = area.estado?.toLowerCase() === 'bloqueada';
        }

        return matchesSearch && matchesFilter;
    });

    // LÓGICA DE PAGINACIÓN NUMÉRICA
    const totalPaginas = Math.max(1, Math.ceil(filteredAreas.length / elementosPorPagina));
    const indiceUltimo = paginaActual * elementosPorPagina;
    const indicePrimer = indiceUltimo - elementosPorPagina;
    const areasPaginadas = filteredAreas.slice(indicePrimer, indiceUltimo);

    const numerosPagina = [];
    for (let i = 1; i <= totalPaginas; i++) {
        numerosPagina.push(i);
    }

    return (
        <div className="p-5 animate__animated animate__fadeIn bg-light" style={{ minHeight: '100vh' }}>
            {/* ENCABEZADO */}
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold mb-1 text-dark" style={{ fontSize: '2.5rem', letterSpacing: '-1.5px' }}>Áreas Deportivas</h2>
                    <p className="text-muted small">Gestiona todas las canchas y espacios deportivos</p>
                </div>
                <button
                    className="btn btn-success d-flex align-items-center gap-2 px-4 py-2 fw-bold shadow-sm"
                    style={{ borderRadius: '12px', backgroundColor: '#10b981', border: 'none' }}
                    onClick={() => { setAreaParaEditar(null); setShowModal(true); }}
                >
                    <Plus size={18} /> Nueva área
                </button>
            </div>

            {/* BARRA DE BÚSQUEDA Y FILTROS */}
            <div className="card border-0 shadow-sm p-3 mb-5 bg-white rounded-4">
                <div className="d-flex align-items-center gap-3">
                    <div className="position-relative flex-grow-1" style={{ maxWidth: '450px' }}>
                        <Search className="position-absolute top-50 translate-middle-y ms-3 text-muted" size={18} />
                        <input
                            type="text"
                            className="form-control bg-light border-0 ps-5 py-2 shadow-none"
                            placeholder="Buscar por nombre o ubicación..."
                            style={{ borderRadius: '12px' }}
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>

                    <div className="d-flex gap-2 p-1 bg-light rounded-3">
                        {['Todos', 'Disponible', 'Mantenimiento'].map(f => (
                            <button
                                key={f}
                                className={`btn btn-sm px-4 py-2 border-0 ${activeFilter === f ? 'bg-dark text-white shadow' : 'text-muted'}`}
                                style={{ borderRadius: '10px', fontWeight: '600', transition: '0.3s' }}
                                onClick={() => setActiveFilter(f)}
                            >
                                {f}
                            </button>
                        ))}
                    </div>
                </div>
            </div>

            {/* 👇 RENDERIZADO CONDICIONAL: LOADING VS CARDS 👇 */}
            {isLoading ? (
                <div className="d-flex justify-content-center py-5">
                    <div className="spinner-border text-primary" role="status">
                        <span className="visually-hidden">Cargando...</span>
                    </div>
                </div>
            )  : areasPaginadas.length === 0 ? (
                <div className="text-center py-5 mt-4">
                    <MapPin size={80} className="text-muted mb-3 opacity-25" strokeWidth={1} />
                    <h4 className="fw-bold text-dark">No se encontraron áreas</h4>
                    <p className="text-muted small">Intenta con otros filtros o crea una nueva área.</p>
                </div>
            ) : (
                <>
                    <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4 mb-5">
                        {areasPaginadas.map((area) => (
                            <div className="col" key={area.id}>
                                <AreaCard
                                    area={area}
                                    onBloquear={(id) => {
                                        setAreaSeleccionada(id);
                                        setShowBloquearModal(true);
                                    }}
                                    onDesbloquear={handleDesbloquear}
                                    onEditar={(areaObj) => {
                                        setAreaParaEditar(areaObj);
                                        setShowModal(true);
                                    }}
                                />
                            </div>
                        ))}
                    </div>

                    {/* PAGINADOR NUMÉRICO AZUL (Siempre visible si hay áreas) */}
                    <div className="d-flex justify-content-between align-items-center bg-white p-3 rounded-4 shadow-sm border">
                        <span className="text-muted small">
                            Mostrando <b>{filteredAreas.length > 0 ? indicePrimer + 1 : 0}</b> a <b>{Math.min(indiceUltimo, filteredAreas.length)}</b> de <b>{filteredAreas.length}</b> resultados
                        </span>

                        <div className="d-flex gap-1">
                            <button
                                className="btn btn-light border btn-sm px-2"
                                onClick={() => setPaginaActual(p => Math.max(1, p - 1))}
                                disabled={paginaActual === 1}
                            >
                                <ChevronLeft size={16} />
                            </button>

                            {numerosPagina.map(num => (
                                <button
                                    key={num}
                                    className={`btn btn-sm px-3 ${paginaActual === num ? 'btn-primary shadow-sm' : 'btn-light border text-primary'}`}
                                    onClick={() => setPaginaActual(num)}
                                    style={{ borderRadius: '4px', fontWeight: 'bold', minWidth: '35px' }}
                                >
                                    {num}
                                </button>
                            ))}

                            <button
                                className="btn btn-light border btn-sm px-2"
                                onClick={() => setPaginaActual(p => Math.min(totalPaginas, p + 1))}
                                disabled={paginaActual === totalPaginas}
                            >
                                <ChevronRight size={16} />
                            </button>
                        </div>
                    </div>
                </>
            )}

            {/* MODALES */}
            <AreaModal
                show={showModal}
                onClose={() => setShowModal(false)}
                fetchAreas={fetchAreas}
                areaToEdit={areaParaEditar}
            />
            <BloquearModal
                show={showBloquearModal}
                onClose={() => setShowBloquearModal(false)}
                fetchAreas={fetchAreas}
                areaId={areaSeleccionada}
            />
        </div>
    );
}