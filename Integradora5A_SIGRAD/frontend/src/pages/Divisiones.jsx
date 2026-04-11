import React, { useState, useEffect } from 'react';
import { Plus, Search, Edit, Building2, ChevronLeft, ChevronRight } from "lucide-react";
import Swal from 'sweetalert2';
import DivisionModal from '../components/divisiones/DivisionModal';

export default function Divisiones() {
    const [showModal, setShowModal] = useState(false);
    const [listaDivisiones, setListaDivisiones] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [filtro, setFiltro] = useState('Todas');
    const [divisionParaEditar, setDivisionParaEditar] = useState(null);
    const [paginaActual, setPaginaActual] = useState(1);
    const elementosPorPagina = 5;
    const [isLoading, setIsLoading] = useState(true);

    const obtenerDivisiones = async () => {
        setIsLoading(true);
        try {
            const response = await fetch('http://localhost:8080/api/divisiones/listar');
            if (response.ok) {
                const data = await response.json();
                setListaDivisiones(Array.isArray(data) ? data : []);
            }
        } catch (error) {
            console.error("Error al obtener divisiones:", error);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => { obtenerDivisiones(); }, []);
    useEffect(() => { setPaginaActual(1); }, [searchTerm, filtro]);

    const handleCambiarEstado = async (id) => {
        try {
            const response = await fetch(`http://localhost:8080/api/divisiones/estado/${id}`, { method: 'PUT' });
            if (response.ok) {
                obtenerDivisiones();
                const Toast = Swal.mixin({ toast: true, position: 'top-end', showConfirmButton: false, timer: 1500 });
                Toast.fire({ icon: 'success', title: 'Estado actualizado' });
            }
        } catch (error) {
            Swal.fire('Error', 'Problema de conexión', 'error');
        }
    };

    const divisionesFiltradas = listaDivisiones.filter(division => {
        const coincideTexto = division.nombre.toLowerCase().includes(searchTerm.toLowerCase());
        let coincideFiltro = true;
        if (filtro === 'Habilitadas') coincideFiltro = division.habilitada === true;
        if (filtro === 'Inhabilitadas') coincideFiltro = division.habilitada === false;
        return coincideTexto && coincideFiltro;
    });

    const totalPaginas = Math.ceil(divisionesFiltradas.length / elementosPorPagina);
    const indiceUltimo = paginaActual * elementosPorPagina;
    const indicePrimer = indiceUltimo - elementosPorPagina;
    const divisionesPaginadas = divisionesFiltradas.slice(indicePrimer, indiceUltimo);
    const numerosPagina = [];
    for (let i = 1; i <= totalPaginas; i++) { numerosPagina.push(i); }

    return (
        <div className="p-5 bg-light" style={{ minHeight: '100vh' }}>
            <style>{`
                .custom-switch {
                    width: 48px !important; height: 24px !important;
                    cursor: pointer; background-color: #dee2e6; border: none !important;
                }
                .custom-switch:checked { background-color: #10b981 !important; }
                .custom-switch:focus { box-shadow: none !important; }
            `}</style>

            <div className="d-flex justify-content-between align-items-center mb-5">
                <div>
                    <h2 className="fw-bold m-0" style={{ fontSize: '2.5rem', letterSpacing: '-1.5px' }}>Administración de Divisiones</h2>
                    <p className="text-muted m-0 mt-1">Gestiona el catálogo de divisiones académicas</p>
                </div>
                <button
                    className="btn btn-success d-flex align-items-center gap-2 fw-bold shadow-sm"
                    style={{ borderRadius: '12px', padding: '12px 24px', backgroundColor: '#10b981', border: 'none' }}
                    onClick={() => { setDivisionParaEditar(null); setShowModal(true); }}
                >
                    <Plus size={20} /> Nueva División
                </button>
            </div>

            <div className="card shadow-sm border-0 p-4 rounded-4 bg-white">
                <div className="d-flex justify-content-between align-items-center mb-4 gap-3">
                    <div className="position-relative flex-grow-1" style={{ maxWidth: '450px' }}>
                        <Search className="position-absolute top-50 translate-middle-y ms-3 text-muted" size={18} />
                        <input
                            type="text"
                            className="form-control bg-light border-0 ps-5 py-2 shadow-none"
                            placeholder="Buscar división..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            style={{ borderRadius: '12px' }}
                        />
                    </div>
                    <div className="d-flex gap-2 p-1 bg-light rounded-3">
                        {['Todas', 'Habilitadas', 'Inhabilitadas'].map(estado => (
                            <button
                                key={estado}
                                className={`btn btn-sm px-4 py-2 border-0 ${filtro === estado ? 'bg-dark text-white shadow' : 'text-muted'}`}
                                style={{ borderRadius: '10px', transition: '0.3s', fontWeight: '600' }}
                                onClick={() => setFiltro(estado)}
                            >{estado}</button>
                        ))}
                    </div>
                </div>

                {isLoading ? (
                    <div className="d-flex justify-content-center py-5 my-5">
                        <div className="spinner-border text-primary" role="status" style={{ width: '3rem', height: '3rem' }}>
                            <span className="visually-hidden">Cargando...</span>
                        </div>
                    </div>
                ) : (
                    <>
                        <div className="table-responsive">
                            <table className="table table-hover align-middle">
                                <thead>
                                <tr className="small text-muted text-uppercase" style={{ letterSpacing: '1px' }}>
                                    <th className="ps-4">ID</th>
                                    <th>Nombre de la División</th>
                                    <th className="text-center">Estado</th>
                                    <th className="text-center pe-4">Acciones</th>
                                </tr>
                                </thead>
                                <tbody>
                                {divisionesPaginadas.length > 0 ? (
                                    divisionesPaginadas.map((division) => (
                                        <tr key={division.id}>
                                            <td className="ps-4 text-muted fw-bold">#{division.id}</td>
                                            <td>
                                                <div className="d-flex align-items-center gap-3">
                                                    <div className="p-2 rounded-3 bg-primary bg-opacity-10 text-primary">
                                                        <Building2 size={20} />
                                                    </div>
                                                    <span className={`fw-bolder fs-5 ${!division.habilitada && 'text-muted opacity-50'}`} style={{ color: '#111827' }}>
                                                            {division.nombre}
                                                        </span>
                                                </div>
                                            </td>
                                            <td className="py-3 text-center">
                                                <div className="d-flex flex-column align-items-center gap-1">
                                                    <div className="form-check form-switch m-0 d-flex justify-content-center p-0">
                                                        <input
                                                            className="form-check-input custom-switch m-0 shadow-none"
                                                            type="checkbox"
                                                            role="switch"
                                                            checked={division.habilitada}
                                                            onChange={() => handleCambiarEstado(division.id)}
                                                        />
                                                    </div>
                                                    <span className={`badge ${division.habilitada ? 'bg-success bg-opacity-10 text-success' : 'bg-danger bg-opacity-10 text-danger'}`} style={{ fontSize: '10px' }}>
                                                            {division.habilitada ? 'HABILITADA' : 'INHABILITADA'}
                                                        </span>
                                                </div>
                                            </td>
                                            <td className="text-center pe-4">
                                                <button
                                                    className="btn btn-light btn-sm border-0 rounded-3 p-2"
                                                    onClick={() => { setDivisionParaEditar(division); setShowModal(true); }}
                                                >
                                                    <Edit size={18} className="text-primary" />
                                                </button>
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan="4" className="text-center py-5 opacity-25">
                                            <Search size={40} className="mb-2" />
                                            <p className="m-0">No se encontraron resultados.</p>
                                        </td>
                                    </tr>
                                )}
                                </tbody>
                            </table>
                        </div>

                        <div className="d-flex justify-content-between align-items-center mt-4 pt-3 border-top">
                            <span className="text-muted small">
                                Mostrando <b>{divisionesFiltradas.length > 0 ? indicePrimer + 1 : 0}</b> a <b>{Math.min(indiceUltimo, divisionesFiltradas.length)}</b> de <b>{divisionesFiltradas.length}</b> resultados
                            </span>
                            <div className="d-flex gap-1">
                                <button className="btn btn-light border btn-sm px-2" onClick={() => setPaginaActual(p => Math.max(1, p - 1))} disabled={paginaActual === 1}>
                                    <ChevronLeft size={16} />
                                </button>
                                {numerosPagina.map(num => (
                                    <button key={num}
                                            className={`btn btn-sm px-3 ${paginaActual === num ? 'btn-primary shadow-sm' : 'btn-light border text-primary'}`}
                                            onClick={() => setPaginaActual(num)}
                                            style={{ borderRadius: '4px', fontWeight: 'bold' }}
                                    >{num}</button>
                                ))}
                                <button className="btn btn-light border btn-sm px-2" onClick={() => setPaginaActual(p => Math.min(totalPaginas, p + 1))} disabled={paginaActual === totalPaginas || totalPaginas === 0}>
                                    <ChevronRight size={16} />
                                </button>
                            </div>
                        </div>
                    </>
                )}
            </div>

            <DivisionModal
                show={showModal}
                onClose={() => setShowModal(false)}
                onRefresh={obtenerDivisiones}
                divisionToEdit={divisionParaEditar}
            />
        </div>
    );
}