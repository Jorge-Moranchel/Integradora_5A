import React, { useState, useEffect } from 'react';
import { Search, Plus, MapPin } from 'lucide-react';
import AreaModal from '../components/areas/AreaModal';
import AreaCard from '../components/areas/AreaCard';
import BloquearModal from '../components/areas/BloquearModal';

export default function Areas() {
    const [showModal, setShowModal] = useState(false);
    const [areas, setAreas] = useState([]);
    const [showBloquearModal, setShowBloquearModal] = useState(false);
    const [areaSeleccionada, setAreaSeleccionada] = useState(null);
    const [areaParaEditar, setAreaParaEditar] = useState(null);

    // NUEVOS ESTADOS PARA BÚSQUEDA Y FILTROS
    const [searchTerm, setSearchTerm] = useState('');
    const [activeFilter, setActiveFilter] = useState('Todos'); // Puede ser: 'Todos', 'Disponible', 'Mantenimiento'

    const fetchAreas = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/areas/listar');
            if (response.ok) {
                const data = await response.json();
                setAreas(data);
            }
        } catch (error) {
            console.error("Error al traer las áreas:", error);
        }
    };

    useEffect(() => {
        fetchAreas();
    }, []);

    const handleDesbloquear = async (id) => {
        if(!window.confirm("¿Estás seguro de que deseas habilitar esta área nuevamente?")) return;
        try {
            const response = await fetch(`http://localhost:8080/api/areas/desbloquear/${id}`, { method: 'PUT' });
            if (response.ok) {
                alert('Área desbloqueada y lista para reservaciones.');
                fetchAreas();
            } else {
                const errorDelServidor = await response.text();
                alert(`Fallo en el servidor (Código ${response.status}):\n${errorDelServidor}`);
            }
        } catch (error) {
            console.error(error);
            alert('Error de conexión con el servidor.');
        }
    };

    // --- LÓGICA DE FILTRADO MÁGICO ---
    const filteredAreas = areas.filter(area => {
        // 1. Coincidencia con la barra de búsqueda (por nombre o ubicación)
        const matchesSearch = area.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
            area.ubicacion.toLowerCase().includes(searchTerm.toLowerCase());

        // 2. Coincidencia con los botones de estado
        let matchesFilter = true;
        if (activeFilter === 'Disponible') {
            matchesFilter = area.estado?.toLowerCase() === 'disponible';
        } else if (activeFilter === 'Mantenimiento') {
            matchesFilter = area.estado?.toLowerCase() === 'bloqueada';
        }

        return matchesSearch && matchesFilter;
    });

    return (
        <div className="p-5 animate__animated animate__fadeIn">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 className="fw-bold mb-1 text-dark">Áreas Deportivas</h2>
                    <p className="text-muted small">Gestiona todas las canchas y espacios deportivos</p>
                </div>
                <button
                    className="btn btn-success d-flex align-items-center gap-2 px-4 py-2 fw-bold"
                    onClick={() => { setAreaParaEditar(null); setShowModal(true); }}
                >
                    <Plus size={18} /> Nueva Área
                </button>
            </div>

            {/* BARRA DE BÚSQUEDA Y FILTROS */}
            <div className="search-card p-3 mb-5 shadow-sm bg-white rounded-3 border">
                <div className="d-flex align-items-center gap-3">
                    <div className="input-group" style={{ maxWidth: '450px' }}>
                        <span className="input-group-text bg-white border-end-0 text-muted">
                            <Search size={18} />
                        </span>
                        <input
                            type="text"
                            className="form-control border-start-0 shadow-none"
                            placeholder="Buscar por nombre o ubicación..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>

                    {/* Botones dinámicos */}
                    <div className="btn-group shadow-sm">
                        <button
                            className={`btn ${activeFilter === 'Todos' ? 'btn-dark active fw-bold' : 'btn-outline-secondary'}`}
                            onClick={() => setActiveFilter('Todos')}
                        >
                            Todos
                        </button>
                        <button
                            className={`btn ${activeFilter === 'Disponible' ? 'btn-success active fw-bold' : 'btn-outline-secondary'}`}
                            onClick={() => setActiveFilter('Disponible')}
                        >
                            Disponible
                        </button>
                        <button
                            className={`btn ${activeFilter === 'Mantenimiento' ? 'btn-warning text-dark active fw-bold' : 'btn-outline-secondary'}`}
                            onClick={() => setActiveFilter('Mantenimiento')}
                        >
                            Mantenimiento
                        </button>
                    </div>
                </div>
            </div>

            {/* RENDERIZADO USANDO LA LISTA FILTRADA */}
            {filteredAreas.length === 0 ? (
                <div className="text-center py-5 mt-4">
                    <MapPin size={80} className="text-muted mb-3 opacity-25" strokeWidth={1} />
                    <h4 className="fw-bold text-dark">No se encontraron áreas</h4>
                    <p className="text-muted">Intenta con otros filtros o crea una nueva área deportiva.</p>
                </div>
            ) : (
                <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
                    {filteredAreas.map((area) => (
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
            )}

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