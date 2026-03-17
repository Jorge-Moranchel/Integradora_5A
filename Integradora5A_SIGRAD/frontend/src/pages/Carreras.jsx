import React, { useState, useEffect } from 'react';
import { Plus, Search, Edit, Trash2, SearchIcon, GraduationCap } from "lucide-react";
import CarreraModal from '../components/carreras/CarreraModal'; // Asegúrate de tener este archivo

export default function Carreras() {
    const [showModal, setShowModal] = useState(false);
    const [listaCarreras, setListaCarreras] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [filtro, setFiltro] = useState('Todas');

    const obtenerCarreras = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/carreras/listar');
            const data = await response.json();
            setListaCarreras(data);
        } catch (error) {
            console.error("Error al obtener carreras:", error);
        }
    };

    useEffect(() => {
        obtenerCarreras();
    }, []);

    // Lógica de búsqueda
    const carrerasFiltradas = listaCarreras.filter(carrera =>
        carrera.nombre.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const cardStyle = {
        borderRadius: '15px',
        border: '1px solid #e9ecef',
        padding: '2rem'
    };

    return (
        <div className="p-5 animate__animated animate__fadeIn bg-light" style={{ minHeight: '100vh' }}>

            {/* Header Alineado al estilo Usuarios */}
            <div className="d-flex justify-content-between align-items-center mb-5">
                <div>
                    <h2 className="fw-bold m-0" style={{ fontSize: '2.5rem' }}>Administración de Carreras</h2>
                    <p className="text-muted m-0 mt-1">Gestiona las carreras disponibles para el registro de usuarios</p>
                </div>
                <button
                    className="btn btn-success d-flex align-items-center gap-2 fw-bold"
                    style={{ borderRadius: '10px', padding: '12px 24px', backgroundColor: '#00a854', border: 'none' }}
                    onClick={() => setShowModal(true)}
                >
                    <Plus size={20} /> Nueva Carrera
                </button>
            </div>

            {/* Tarjeta de Resumen */}
            <div className="row g-4 mb-5">
                <div className="col-md-3">
                    <div className="card h-100 shadow-sm bg-white" style={cardStyle}>
                        <p className="text-muted small fw-bold mb-3">Carreras Registradas</p>
                        <h1 className="fw-bold text-success m-0" style={{ fontSize: '4rem' }}>{listaCarreras.length}</h1>
                    </div>
                </div>
            </div>

            {/* Card Principal de Tabla */}
            <div className="card shadow-sm bg-white" style={{ ...cardStyle, padding: '1.5rem' }}>

                {/* Buscador y Filtros (Estilo Referencia) */}
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

                {/* Tabla estilizada */}
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
                        {carrerasFiltradas.length > 0 ? (
                            carrerasFiltradas.map((carrera) => (
                                <tr key={carrera.id}>
                                    <td className="text-muted">#{carrera.id}</td>
                                    <td>
                                        <div className="d-flex align-items-center gap-2">
                                            <div className="bg-success-subtle p-2 rounded text-success">
                                                <GraduationCap size={18} />
                                            </div>
                                            <span className="fw-bold">{carrera.nombre}</span>
                                        </div>
                                    </td>
                                    <td className="text-center">
                                            <span className="badge rounded-pill bg-success-subtle text-success px-3">
                                                Habilitada
                                            </span>
                                    </td>
                                    <td className="text-center">
                                        <div className="d-flex justify-content-center gap-2">
                                            <button className="btn btn-light btn-sm border" title="Editar">
                                                <Edit size={16} className="text-primary" />
                                            </button>
                                            <button className="btn btn-light btn-sm border" title="Eliminar">
                                                <Trash2 size={16} className="text-danger" />
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="4" className="text-center py-5 opacity-50">
                                    <SearchIcon size={50} className="mb-3" />
                                    <p className="m-0">No se encontraron carreras en la base de datos.</p>
                                </td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Modal de Registro */}
            <CarreraModal
                show={showModal}
                onClose={() => setShowModal(false)}
                onRefresh={obtenerCarreras}
            />
        </div>
    );
}