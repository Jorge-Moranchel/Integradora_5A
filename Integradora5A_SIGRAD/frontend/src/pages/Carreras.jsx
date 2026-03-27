import React, { useState, useEffect } from 'react';
import { Plus, Search, Edit, Power, GraduationCap, ChevronLeft, ChevronRight } from "lucide-react";
import Swal from 'sweetalert2';
import CarreraModal from '../components/carreras/CarreraModal';

export default function Carreras() {
    const [showModal, setShowModal] = useState(false);
    const [listaCarreras, setListaCarreras] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [filtro, setFiltro] = useState('Todas');
    const [carreraParaEditar, setCarreraParaEditar] = useState(null);
    const [paginaActual, setPaginaActual] = useState(1);
    const elementosPorPagina = 5;

    const obtenerCarreras = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/carreras/listar');
            if (response.ok) {
                const data = await response.json();
                setListaCarreras(Array.isArray(data) ? data : []);
            }
        } catch (error) {
            console.error("Error al obtener carreras:", error);
        }
    };

    useEffect(() => { obtenerCarreras(); }, []);
    useEffect(() => { setPaginaActual(1); }, [searchTerm, filtro]);

    const handleCambiarEstado = async (id) => {
        try {
            const response = await fetch(`http://localhost:8080/api/carreras/estado/${id}`, { method: 'PUT' });
            if (response.ok) {
                obtenerCarreras();
                Swal.fire({ icon: 'success', title: 'Estado actualizado', timer: 1500, showConfirmButton: false });
            } else {
                let msg = 'No se pudo actualizar el estado';
                try {
                    const data = await response.json();
                    msg = data?.mensaje || data?.message || msg;
                } catch (_) {
                    try {
                        msg = await response.text();
                    } catch (_) {}
                }
                Swal.fire('Error', msg, 'error');
            }
        } catch (error) {
            Swal.fire('Error', 'Problema de conexión', 'error');
        }
    };

    const carrerasFiltradas = listaCarreras.filter(carrera => {
        const coincideTexto = carrera.nombre.toLowerCase().includes(searchTerm.toLowerCase());
        let coincideFiltro = true;
        if (filtro === 'Habilitadas') coincideFiltro = carrera.habilitada === true;
        if (filtro === 'Inhabilitadas') coincideFiltro = carrera.habilitada === false;
        return coincideTexto && coincideFiltro;
    });

    const totalPaginas = Math.ceil(carrerasFiltradas.length / elementosPorPagina);
    const indiceUltimo = paginaActual * elementosPorPagina;
    const indicePrimer = indiceUltimo - elementosPorPagina;
    const carrerasPaginadas = carrerasFiltradas.slice(indicePrimer, indiceUltimo);

    const numerosPagina = [];
    for (let i = 1; i <= totalPaginas; i++) {
        numerosPagina.push(i);
    }

    return (
        <div className="p-5 bg-light" style={{ minHeight: '100vh' }}>
            <div className="d-flex justify-content-between align-items-center mb-5">
                <div>
                    <h2 className="fw-bold m-0" style={{ fontSize: '2.5rem' }}>Administración de Carreras</h2>
                    <p className="text-muted">Gestiona el catálogo de carreras del sistema</p>
                </div>
                <button
                    className="btn btn-success d-flex align-items-center gap-2 fw-bold"
                    style={{ borderRadius: '12px', padding: '12px 24px', backgroundColor: '#10b981', border: 'none' }}
                    onClick={() => { setCarreraParaEditar(null); setShowModal(true); }}
                >
                    <Plus size={20} /> Nueva Carrera
                </button>
            </div>

            <div className="card shadow-sm border-0 p-4 rounded-4 bg-white">
                <div className="d-flex justify-content-between align-items-center mb-4 gap-3">
                    <div className="position-relative flex-grow-1" style={{ maxWidth: '450px' }}>
                        <Search className="position-absolute top-50 translate-middle-y ms-3 text-muted" size={18} />
                        <input
                            type="text"
                            className="form-control bg-light border-0 ps-5 py-2 shadow-none"
                            placeholder="Buscar carrera..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            style={{ borderRadius: '12px' }}
                        />
                    </div>
                    <div className="d-flex gap-2">
                        {['Todas', 'Habilitadas', 'Inhabilitadas'].map(estado => (
                            <button
                                key={estado}
                                className={`btn px-4 py-2 ${filtro === estado ? 'bg-dark text-white fw-bold' : 'bg-white text-muted border'}`}
                                style={{ borderRadius: '10px' }}
                                onClick={() => setFiltro(estado)}
                            >
                                {estado}
                            </button>
                        ))}
                    </div>
                </div>

                <div className="table-responsive">
                    <table className="table table-hover align-middle">
                        <thead>
                        <tr className="small text-muted">
                            <th>ID</th>
                            <th>NOMBRE DE LA CARRERA</th>
                            <th className="text-center">ESTADO</th>
                            <th className="text-center">ACCIONES</th>
                        </tr>
                        </thead>
                        <tbody>
                        {carrerasPaginadas.map((carrera) => (
                            <tr key={carrera.id}>
                                <td className="text-muted">#{carrera.id}</td>
                                <td>
                                    <div className="d-flex align-items-center gap-2">
                                        <div className="p-2 rounded bg-success bg-opacity-10 text-success"><GraduationCap size={18} /></div>
                                        <span className={`fw-bold ${!carrera.habilitada && 'text-decoration-line-through text-muted'}`}>
                                            {carrera.nombre}
                                            {carrera.abreviatura ? (
                                                <span className="ms-2 badge bg-secondary bg-opacity-10 text-secondary border-0" style={{ fontSize: '11px' }}>
                                                    {carrera.abreviatura}
                                                </span>
                                            ) : null}
                                        </span>
                                    </div>
                                </td>
                                <td className="text-center">
                                    <span className={`badge rounded-pill ${carrera.habilitada ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'}`}>{carrera.habilitada ? 'Habilitada' : 'Inhabilitada'}</span>
                                </td>
                                <td className="text-center">
                                    <div className="d-flex justify-content-center gap-2">
                                        <button className="btn btn-light btn-sm border" onClick={() => { setCarreraParaEditar(carrera); setShowModal(true); }}><Edit size={16} className="text-primary" /></button>
                                        <button className="btn btn-light btn-sm border" onClick={() => handleCambiarEstado(carrera.id)}><Power size={16} className={carrera.habilitada ? "text-danger" : "text-success"} /></button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>

                <div className="d-flex justify-content-between align-items-center mt-4 pt-3 border-top">
                    <span className="text-muted small">
                        Mostrando <b>{indicePrimer + 1}</b> a <b>{Math.min(indiceUltimo, carrerasFiltradas.length)}</b> de <b>{carrerasFiltradas.length}</b> resultados
                    </span>
                    <div className="d-flex gap-1">
                        <button className="btn btn-light border btn-sm px-2" onClick={() => setPaginaActual(p => Math.max(1, p - 1))} disabled={paginaActual === 1}><ChevronLeft size={16} /></button>
                        {numerosPagina.map(num => (
                            <button key={num} className={`btn btn-sm px-3 ${paginaActual === num ? 'btn-primary' : 'btn-light border text-primary'}`} onClick={() => setPaginaActual(num)} style={{ borderRadius: '4px', fontWeight: 'bold' }}>{num}</button>
                        ))}
                        <button className="btn btn-light border btn-sm px-2" onClick={() => setPaginaActual(p => Math.min(totalPaginas, p + 1))} disabled={paginaActual === totalPaginas}><ChevronRight size={16} /></button>
                    </div>
                </div>
            </div>

            <CarreraModal show={showModal} onClose={() => setShowModal(false)} onRefresh={obtenerCarreras} carreraToEdit={carreraParaEditar} />
        </div>
    );
}
