import React, { useState, useEffect } from 'react';
import UserTable from '../components/users/UserTable';
import UserModal from '../components/users/UserModal';
import { Search, Plus, SearchIcon, ChevronLeft, ChevronRight, Clock } from 'lucide-react';

export default function Usuarios() {
    const [showModal, setShowModal] = useState(false);
    const [users, setUsers] = useState([]);
    const [filter, setFilter] = useState('Todos');
    const [searchTerm, setSearchTerm] = useState('');
    const [userToEdit, setUserToEdit] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 5;

    const fetchUsers = async () => {
        setIsLoading(true);
        try {
            const res = await fetch('http://localhost:8080/api/usuarios/listar');
            const data = await res.json();
            setUsers(data);
        } catch (error) {
            console.error(error);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => { fetchUsers(); }, []);

    const handleEdit = (user) => {
        setUserToEdit(user);
        setShowModal(true);
    };

    const handleCloseModal = () => {
        setShowModal(false);
        setUserToEdit(null);
        fetchUsers();
    };

    const filteredUsers = users.filter(user => {
        const matchesSearch =
            user.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
            user.emailInstitucional.toLowerCase().includes(searchTerm.toLowerCase()) ||
            user.matricula?.toLowerCase().includes(searchTerm.toLowerCase());

        const isUserActive = user.estado !== false;
        const isValidado = user.validado === true;

        const matchesTab =
            filter === 'Todos'       ||
            (filter === 'Activo'     && isUserActive)  ||
            (filter === 'Bloqueado'  && !isUserActive) ||
            (filter === 'Pendientes' && !isValidado);

        return matchesSearch && matchesTab;
    });

    const indexOfLastItem  = currentPage * itemsPerPage;
    const indexOfFirstItem = indexOfLastItem - itemsPerPage;
    const currentItems     = filteredUsers.slice(indexOfFirstItem, indexOfLastItem);
    const totalPages       = Math.ceil(filteredUsers.length / itemsPerPage);

    const paginate = (pageNumber) => setCurrentPage(pageNumber);

    useEffect(() => { setCurrentPage(1); }, [searchTerm, filter]);

    const totalActivos    = users.filter(u => u.estado !== false).length;
    const totalBloqueados = users.filter(u => u.estado === false).length;

    const totalPendientes = users.filter(u => u.validado !== true).length;

    const tabs = [
        { key: 'Todos',      label: 'Todos'                    },
        { key: 'Activo',     label: 'Activos'                  },
        { key: 'Bloqueado',  label: 'Bloqueados'               },
        { key: 'Pendientes', label: 'Pendientes de validación' },
    ];

    return (
        <div className="animate__animated animate__fadeIn p-4 bg-light">
            <div className="d-flex justify-content-between align-items-center mb-5">
                <div>
                    <h2 className="fw-bold m-0" style={{ fontSize: '2.5rem' }}>Usuarios</h2>
                    <p className="text-muted m-0 mt-1">Administra los usuarios registrados en el sistema</p>
                </div>
                <button
                    className="btn btn-success d-flex align-items-center gap-2 fw-bold shadow-sm"
                    style={{ borderRadius: '10px', padding: '12px 24px', backgroundColor: '#00a854', border: 'none' }}
                    onClick={() => { setUserToEdit(null); setShowModal(true); }}
                >
                    <Plus size={20} /> Nuevo Usuario
                </button>
            </div>

            <div className="row g-4 mb-5">
                <div className="col-md-3">
                    <div className="card h-100 shadow-sm bg-white p-4 border-0" style={{ borderRadius: '15px' }}>
                        <p className="text-muted small fw-bold mb-3">Usuarios Activos</p>
                        <h1 className="fw-bold text-success m-0" style={{ fontSize: '3.5rem' }}>{totalActivos}</h1>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card h-100 shadow-sm bg-white p-4 border-0" style={{ borderRadius: '15px' }}>
                        <p className="text-muted small fw-bold mb-3">Usuarios Bloqueados</p>
                        <h1 className="fw-bold text-danger m-0" style={{ fontSize: '3.5rem' }}>{totalBloqueados}</h1>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card h-100 shadow-sm bg-white p-4 border-0" style={{ borderRadius: '15px' }}>
                        <p className="text-muted small fw-bold mb-3">Total Registrados</p>
                        <h1 className="fw-bold text-primary m-0" style={{ fontSize: '3.5rem' }}>{users.length}</h1>
                    </div>
                </div>


                <div
                    className="col-md-3"
                    style={{ cursor: 'pointer' }}
                    onClick={() => setFilter('Pendientes')}
                    title="Clic para filtrar pendientes"
                >
                    <div
                        className="card h-100 shadow-sm p-4 border-0"
                        style={{
                            borderRadius: '15px',
                            backgroundColor: filter === 'Pendientes' ? '#fffbeb' : 'white',
                            border: filter === 'Pendientes' ? '2px solid #f59e0b' : undefined,
                            transition: '0.2s'
                        }}
                    >
                        <div className="d-flex justify-content-between align-items-start">
                            <p className="text-muted small fw-bold mb-3">Pendientes de Validación</p>
                            <Clock size={18} className="text-warning" />
                        </div>
                        <h1 className="fw-bold text-warning m-0" style={{ fontSize: '3.5rem' }}>{totalPendientes}</h1>
                        <p className="text-muted small mt-2 mb-0" style={{ fontSize: '11px' }}>
                            {totalPendientes === 0 ? 'Todos verificados ✓' : 'Clic para ver pendientes →'}
                        </p>
                    </div>
                </div>
            </div>

            <div className="card shadow-sm bg-white p-4 border-0" style={{ borderRadius: '15px' }}>
                <div className="d-flex justify-content-between align-items-center mb-4 gap-3 flex-wrap">
                    <div className="input-group" style={{ maxWidth: '500px' }}>
                        <span className="input-group-text bg-light border-0"><Search size={18} className="text-muted" /></span>
                        <input
                            type="text"
                            className="form-control bg-light border-0 shadow-none py-2"
                            placeholder="Buscar por nombre, email o matrícula..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>


                    <div className="d-flex gap-2 p-1 bg-light rounded-3 flex-wrap">
                        {tabs.map(({ key, label }) => (
                            <button
                                key={key}
                                className={`btn px-3 py-2 border-0 ${filter === key ? 'bg-dark text-white fw-semibold shadow-sm' : 'text-muted'}`}
                                style={{ borderRadius: '8px', transition: '0.3s', fontSize: '13px' }}
                                onClick={() => setFilter(key)}
                            >
                                {label}
                                {/* Badge de conteo: solo aparece en la pestaña Pendientes y solo si hay alguno */}
                                {key === 'Pendientes' && totalPendientes > 0 && (
                                    <span
                                        className="badge bg-warning text-dark ms-2"
                                        style={{ fontSize: '10px', verticalAlign: 'middle' }}
                                    >
                                        {totalPendientes}
                                    </span>
                                )}
                            </button>
                        ))}
                    </div>
                </div>


                {filter === 'Pendientes' && (
                    <div className="alert alert-warning d-flex align-items-center gap-2 py-2 mb-3" style={{ borderRadius: '10px', fontSize: '13px' }}>
                        <Clock size={16} />
                        <span>
                            Mostrando usuarios que <strong>aún no han verificado</strong> su correo electrónico.
                            Su campo <code>validado</code> es <code>false</code> o <code>null</code>.
                        </span>
                    </div>
                )}

                {isLoading ? (
                    <div className="d-flex justify-content-center py-5 my-5">
                        <div className="spinner-border text-primary" role="status" style={{ width: '3rem', height: '3rem' }}>
                            <span className="visually-hidden">Cargando...</span>
                        </div>
                    </div>
                ) : filteredUsers.length > 0 ? (
                    <>
                        <UserTable users={currentItems} onRefresh={fetchUsers} onEdit={handleEdit} />

                        {totalPages > 1 && (
                            <div className="d-flex justify-content-between align-items-center mt-4 pt-3 border-top border-light">
                                <div className="text-muted small">
                                    Mostrando <span className="fw-bold text-dark">{indexOfFirstItem + 1}</span> a{' '}
                                    <span className="fw-bold text-dark">{Math.min(indexOfLastItem, filteredUsers.length)}</span> de{' '}
                                    <span className="fw-bold text-dark">{filteredUsers.length}</span> resultados
                                </div>
                                <nav>
                                    <ul className="pagination m-0 gap-2">
                                        <li className={`page-item ${currentPage === 1 ? 'disabled' : ''}`}>
                                            <button className="page-link" onClick={() => paginate(currentPage - 1)}>
                                                <ChevronLeft size={18} />
                                            </button>
                                        </li>
                                        {[...Array(totalPages)].map((_, i) => (
                                            <li key={i} className={`page-item ${currentPage === i + 1 ? 'active' : ''}`}>
                                                <button className="page-link" onClick={() => paginate(i + 1)}>{i + 1}</button>
                                            </li>
                                        ))}
                                        <li className={`page-item ${currentPage === totalPages ? 'disabled' : ''}`}>
                                            <button className="page-link" onClick={() => paginate(currentPage + 1)}>
                                                <ChevronRight size={18} />
                                            </button>
                                        </li>
                                    </ul>
                                </nav>
                            </div>
                        )}
                    </>
                ) : (
                    <div className="text-center py-5 opacity-50">
                        <SearchIcon size={70} className="text-muted mb-4"/>
                        <p className="text-muted fs-5 m-0">No se encontraron usuarios.</p>
                    </div>
                )}
            </div>

            <UserModal show={showModal} onClose={handleCloseModal} userToEdit={userToEdit} />
        </div>
    );
}