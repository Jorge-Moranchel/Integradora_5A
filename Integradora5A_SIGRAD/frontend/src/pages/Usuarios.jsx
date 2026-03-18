import React, { useState, useEffect } from 'react';
import UserTable from '../components/users/UserTable';
import UserModal from '../components/users/UserModal';
import { Search, Plus, SearchIcon } from 'lucide-react';

export default function Usuarios() {
    const [showModal, setShowModal] = useState(false);
    const [users, setUsers] = useState([]);
    const [filter, setFilter] = useState('Todos');
    const [searchTerm, setSearchTerm] = useState('');
    const [userToEdit, setUserToEdit] = useState(null); // Estado para el usuario a editar

    const fetchUsers = async () => {
        try {
            const res = await fetch('http://localhost:8080/api/usuarios/listar');
            const data = await res.json();
            setUsers(data);
        } catch (error) { console.error(error); }
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
            user.emailInstitucional.toLowerCase().includes(searchTerm.toLowerCase());
        const isUserActive = user.estado !== false;
        const matchesTab =
            filter === 'Todos' ||
            (filter === 'Activo' && isUserActive) ||
            (filter === 'Bloqueado' && !isUserActive);
        return matchesSearch && matchesTab;
    });

    const totalActivos = users.filter(u => u.estado !== false).length;
    const totalBloqueados = users.filter(u => u.estado === false).length;

    return (
        <div className="animate__animated animate__fadeIn p-4 bg-light">
            <div className="d-flex justify-content-between align-items-center mb-5">
                <div>
                    <h2 className="fw-bold m-0" style={{ fontSize: '2.5rem' }}>Usuarios</h2>
                    <p className="text-muted m-0 mt-1">Administra los usuarios registrados en el sistema</p>
                </div>
                <button
                    className="btn btn-success d-flex align-items-center gap-2 fw-bold"
                    style={{ borderRadius: '10px', padding: '12px 24px', backgroundColor: '#00a854', border: 'none' }}
                    onClick={() => { setUserToEdit(null); setShowModal(true); }}
                >
                    <Plus size={20} /> Nuevo Usuario
                </button>
            </div>

            <div className="row g-4 mb-5">
                <div className="col-md-4">
                    <div className="card h-100 shadow-sm bg-white p-4" style={{ borderRadius: '15px' }}>
                        <p className="text-muted small fw-bold mb-3">Usuarios Activos</p>
                        <h1 className="fw-bold text-success m-0" style={{ fontSize: '4rem' }}>{totalActivos}</h1>
                    </div>
                </div>
                <div className="col-md-4">
                    <div className="card h-100 shadow-sm bg-white p-4" style={{ borderRadius: '15px' }}>
                        <p className="text-muted small fw-bold mb-3">Usuarios Bloqueados</p>
                        <h1 className="fw-bold text-danger m-0" style={{ fontSize: '4rem' }}>{totalBloqueados}</h1>
                    </div>
                </div>
                <div className="col-md-4">
                    <div className="card h-100 shadow-sm bg-white p-4" style={{ borderRadius: '15px' }}>
                        <p className="text-muted small fw-bold mb-3">Total Registrados</p>
                        <h1 className="fw-bold text-primary m-0" style={{ fontSize: '4rem' }}>{users.length}</h1>
                    </div>
                </div>
            </div>

            <div className="card shadow-sm bg-white p-4" style={{ borderRadius: '15px' }}>
                <div className="d-flex justify-content-between align-items-center mb-4 gap-3">
                    <div className="input-group" style={{ maxWidth: '500px' }}>
                        <span className="input-group-text bg-light border-0"><Search size={18} className="text-muted" /></span>
                        <input
                            type="text"
                            className="form-control bg-light border-0 shadow-none py-2"
                            placeholder="Buscar por nombre o email..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>
                    <div className="d-flex gap-2">
                        {['Todos', 'Activo', 'Bloqueado'].map(status => (
                            <button
                                key={status}
                                className={`btn px-4 py-2 ${filter === status ? 'bg-dark text-white fw-semibold' : 'bg-white text-muted border'}`}
                                style={{ borderRadius: '10px' }}
                                onClick={() => setFilter(status)}
                            >
                                {status}
                            </button>
                        ))}
                    </div>
                </div>

                {filteredUsers.length > 0 ? (
                    <UserTable users={filteredUsers} onRefresh={fetchUsers} onEdit={handleEdit} />
                ) : (
                    <div className="text-center py-5 opacity-50">
                        <SearchIcon size={70} className="text-muted mb-4"/>
                        <p className="text-muted fs-5 m-0">No hay usuarios para mostrar.</p>
                    </div>
                )}
            </div>

            <UserModal show={showModal} onClose={handleCloseModal} userToEdit={userToEdit} />
        </div>
    );
}