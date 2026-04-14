import React, { useState, useEffect } from 'react';
import { Plus, Search, Edit, ShieldCheck, ChevronLeft, ChevronRight, Eye } from "lucide-react";
import Swal from 'sweetalert2';
import RoleModal from '../components/roles/RoleModal';
import RoleViewModal from '../components/roles/RoleViewModal';

export default function Roles() {
    const [showModal, setShowModal] = useState(false);
    const [listaRoles, setListaRoles] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [filtro, setFiltro] = useState('Todas');
    const [roleParaEditar, setRoleParaEditar] = useState(null);
    const [paginaActual, setPaginaActual] = useState(1);
    const elementosPorPagina = 5;

    const [showViewModal, setShowViewModal] = useState(false);
    const [roleParaVer, setRoleParaVer] = useState(null);

    // 1. ESTADO DE CARGA AGREGADO
    const [isLoading, setIsLoading] = useState(true);

    const obtenerRoles = async () => {
        setIsLoading(true); // Encendemos el loading
        try {
            const response = await fetch('http://localhost:8080/api/roles');
            if (response.ok) {
                const data = await response.json();
                setListaRoles(Array.isArray(data) ? data : []);
            }
        } catch (error) {
            console.error("Error al obtener roles:", error);
        } finally {
            setIsLoading(false); // Apagamos el loading sin importar qué pase
        }
    };

    useEffect(() => { obtenerRoles(); }, []);
    useEffect(() => { setPaginaActual(1); }, [searchTerm, filtro]);

    const handleCambiarEstado = async (id) => {
        try {
            const response = await fetch(`http://localhost:8080/api/roles/${id}/estado`, { method: 'PATCH' });
            if (response.ok) {
                obtenerRoles();
                const Toast = Swal.mixin({ toast: true, position: 'top-end', showConfirmButton: false, timer: 2000 });
                Toast.fire({ icon: 'success', title: 'Estado actualizado' });
            }
        } catch (error) {
            Swal.fire('Error', 'No se pudo conectar', 'error');
        }
    };

    const rolesFiltrados = listaRoles.filter(role => {
        const nombreValido = role.nombre ? role.nombre.toLowerCase() : "";
        const coincideTexto = nombreValido.includes(searchTerm.toLowerCase());
        let coincideFiltro = true;
        if (filtro === 'Habilitados') coincideFiltro = role.activo === true || role.activo === 1;
        if (filtro === 'Inhabilitados') coincideFiltro = role.activo === false || role.activo === 0;
        return coincideTexto && coincideFiltro;
    });

    const totalPaginas = Math.ceil(rolesFiltrados.length / elementosPorPagina);
    const indiceUltimo = paginaActual * elementosPorPagina;
    const indicePrimer = indiceUltimo - elementosPorPagina;
    const rolesPaginados = rolesFiltrados.slice(indicePrimer, indiceUltimo);

    const numerosPagina = [];
    for (let i = 1; i <= totalPaginas; i++) {
        numerosPagina.push(i);
    }

    return (
        <div className="p-5 animate__animated animate__fadeIn bg-light" style={{ minHeight: '100vh' }}>
            {/* 👇 ESTILOS DEL SWITCH AGREGADOS 👇 */}
            <style>
                {`
                .custom-switch {
                    width: 48px !important;
                    height: 24px !important;
                    cursor: pointer;
                    background-color: #dee2e6;
                    border: none !important;
                }
                .custom-switch:checked {
                    background-color: #10b981 !important;
                }
                .custom-switch:focus {
                    box-shadow: none !important;
                }
                `}
            </style>

            <div className="d-flex justify-content-between align-items-center mb-5">
                <div>
                    <h2 className="fw-bold m-0" style={{ fontSize: '2.5rem', letterSpacing: '-1.5px' }}>Gestión de Roles</h2>
                    <p className="text-muted m-0 mt-1">Administra los permisos y niveles de acceso al sistema</p>
                </div>
                <button
                    className="btn btn-success d-flex align-items-center gap-2 fw-bold shadow-sm"
                    style={{ borderRadius: '12px', padding: '12px 24px', backgroundColor: '#10b981', border: 'none' }}
                    onClick={() => { setRoleParaEditar(null); setShowModal(true); }}
                >
                    <Plus size={20} /> Nuevo Rol
                </button>
            </div>

            <div className="card shadow-sm border-0 p-4 rounded-4 bg-white">
                <div className="d-flex justify-content-between align-items-center mb-4 gap-3">
                    <div className="position-relative flex-grow-1" style={{ maxWidth: '450px' }}>
                        <Search className="position-absolute top-50 translate-middle-y ms-3 text-muted" size={18} />
                        <input
                            type="text"
                            className="form-control bg-light border-0 ps-5 py-2 shadow-none"
                            placeholder="Buscar por nombre de rol..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            style={{ borderRadius: '12px' }}
                        />
                    </div>
                    <div className="d-flex gap-2 p-1 bg-light rounded-3">
                        {['Todas', 'Habilitados', 'Inhabilitados'].map(est => (
                            <button
                                key={est}
                                className={`btn btn-sm px-4 py-2 border-0 ${filtro === est ? 'bg-dark text-white shadow' : 'text-muted'}`}
                                style={{ borderRadius: '10px', transition: '0.3s', fontWeight: '600' }}
                                onClick={() => setFiltro(est)}
                            >
                                {est}
                            </button>
                        ))}
                    </div>
                </div>

                {/* 2. RENDERIZADO CONDICIONAL CON EL LOADING AZUL */}
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
                                <tr className="text-muted small">
                                    <th className="ps-4">NOMBRE DEL ROL</th>
                                    <th className="text-center">ESTADO</th>
                                    <th className="text-center pe-4">ACCIONES</th>
                                </tr>
                                </thead>
                                <tbody>
                                {rolesPaginados.length > 0 ? (
                                    rolesPaginados.map((role) => (
                                        <tr key={role.id}>
                                            <td className="ps-4">
                                                <div className="d-flex align-items-center gap-3">
                                                    <div className="p-2 rounded-3 bg-primary bg-opacity-10 text-primary">
                                                        <ShieldCheck size={20} />
                                                    </div>
                                                    <span className={`fw-bold ${(role.activo === false || role.activo === 0) && 'text-muted opacity-50'}`}>
                                                        {role.nombre}
                                                    </span>
                                                </div>
                                            </td>

                                            {/* 👇 ESTADO: SWITCH CON ETIQUETA INFERIOR 👇 */}
                                            <td className="py-3 text-center">
                                                <div className="d-flex flex-column align-items-center gap-1">
                                                    <div className="form-check form-switch m-0 d-flex justify-content-center p-0">
                                                        <input
                                                            className="form-check-input custom-switch m-0 shadow-none"
                                                            type="checkbox"
                                                            role="switch"
                                                            checked={role.activo === true || role.activo === 1}
                                                            onChange={() => handleCambiarEstado(role.id)}
                                                        />
                                                    </div>
                                                    <span className={`badge ${(role.activo === true || role.activo === 1) ? 'bg-success bg-opacity-10 text-success' : 'bg-danger bg-opacity-10 text-danger'}`} style={{fontSize: '10px'}}>
                                                        {(role.activo === true || role.activo === 1) ? 'ACTIVO' : 'INACTIVO'}
                                                    </span>
                                                </div>
                                            </td>

                                            <td className="text-center pe-4">
                                                <div className="d-flex justify-content-center gap-2">
                                                    <button
                                                        className="btn btn-light btn-sm border-0 rounded-3 p-2"
                                                        title="Ver detalles"
                                                        onClick={() => { setRoleParaVer(role); setShowViewModal(true); }}
                                                    >
                                                        <Eye size={18} className="text-info" />
                                                    </button>

                                                    <button
                                                        className="btn btn-light btn-sm border-0 rounded-3 p-2"
                                                        title="Editar rol"
                                                        onClick={() => { setRoleParaEditar(role); setShowModal(true); }}
                                                    >
                                                        <Edit size={18} className="text-primary" />
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan="4" className="text-center py-5 opacity-25">
                                            <Search size={40} className="mb-2" />
                                            <p className="m-0">No se encontraron roles.</p>
                                        </td>
                                    </tr>
                                )}
                                </tbody>
                            </table>
                        </div>

                        {/* Paginador */}
                        <div className="d-flex justify-content-between align-items-center mt-4">
                            <span className="text-muted small">
                                Mostrando <b>{rolesFiltrados.length > 0 ? indicePrimer + 1 : 0}</b> a <b>{Math.min(indiceUltimo, rolesFiltrados.length)}</b> de <b>{rolesFiltrados.length}</b> resultados
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
                                        className={`btn btn-sm px-3 ${paginaActual === num ? 'btn-primary' : 'btn-light border text-primary'}`}
                                        onClick={() => setPaginaActual(num)}
                                        style={{ borderRadius: '4px', fontWeight: 'bold' }}
                                    >
                                        {num}
                                    </button>
                                ))}

                                <button
                                    className="btn btn-light border btn-sm px-2"
                                    onClick={() => setPaginaActual(p => Math.min(totalPaginas, p + 1))}
                                    disabled={paginaActual === totalPaginas || totalPaginas === 0}
                                >
                                    <ChevronRight size={16} />
                                </button>
                            </div>
                        </div>
                    </>
                )}
            </div>

            <RoleModal show={showModal} onClose={() => setShowModal(false)} onRefresh={obtenerRoles} roleToEdit={roleParaEditar} />
            <RoleViewModal show={showViewModal} onClose={() => setShowViewModal(false)} role={roleParaVer} />
        </div>
    );
}