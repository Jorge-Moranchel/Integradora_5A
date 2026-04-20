import React, { useEffect, useState } from 'react';
import Swal from 'sweetalert2';

export default function Registro() {
    const [roles, setRoles] = useState([]);
    const [divisiones, setDivisiones] = useState([]);
    const [rol, setRol] = useState('');
    const [nombre, setNombre] = useState('');
    const [matricula, setMatricula] = useState('');
    const [telefono, setTelefono] = useState('');
    const [carrera, setCarrera] = useState('');
    const [emailInstitucional, setEmailInstitucional] = useState('');
    const [contrasena, setContrasena] = useState('');

    const rolUp = (rol || '').toUpperCase();
    const esAdministrativo = rolUp === 'ADMINISTRATIVO';
    const esCodigoTrabajador = rolUp === 'DOCENTE' || esAdministrativo;

    useEffect(() => {
        const cargarRoles = async () => {
            try {
                const res = await fetch('http://localhost:8080/api/roles');
                if (!res.ok) return;
                const data = await res.json();
                const activos = Array.isArray(data) ? data.filter(r => r?.activo !== false) : [];
                setRoles(activos);
                if (!rol && activos.length > 0) setRol(activos[0].nombre || '');
            } catch (e) {
                // sin alert para no molestar, solo no carga
            }
        };
        cargarRoles();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    useEffect(() => {
        const cargarDivisiones = async () => {
            try {
                const res = await fetch('http://localhost:8080/api/divisiones/listar');
                if (!res.ok) return;
                const data = await res.json();
                const lista = Array.isArray(data) ? data : [];
                setDivisiones(lista.filter((d) => d.habilitada));
            } catch (e) {
                setDivisiones([]);
            }
        };
        cargarDivisiones();
    }, []);

    useEffect(() => {
        setMatricula('');
        setCarrera('');
    }, [rol]);

    const labelMatricula = esCodigoTrabajador ? 'Código de Trabajador' : 'Matrícula';

    const handleRegistro = async (e) => {
        e.preventDefault();

        try {
            const res = await fetch('http://localhost:8080/api/usuarios/registrar', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    rol,
                    nombre,
                    matricula, // estudiante=matrícula; docente y administrativo=código de trabajador
                    telefono,
                    carrera,
                    emailInstitucional,
                    contrasena
                })
            });

            let data = null;
            try {
                data = await res.json();
            } catch (_) {
                data = null;
            }

            if (res.ok) {
                Swal.fire({
                    icon: 'success',
                    title: '¡Listo!',
                    text: data?.message || 'Registro enviado',
                    showConfirmButton: false,
                    timer: 2000,
                    background: '#fff',
                    iconColor: '#00a854'
                });
            } else {
                const msg = data?.mensaje || data?.message || 'Ocurrió un error';
                Swal.fire({
                    icon: 'error',
                    title: '¡Ups!',
                    text: msg,
                    confirmButtonColor: '#d33',
                    confirmButtonText: 'Ok'
                });
            }
        } catch (err) {
            Swal.fire('Error', 'El servidor no responde. Revisa la conexión.', 'error');
        }
    };

    return (
        <div className="container py-4">
            <div className="card shadow-sm border-0 rounded-4 p-4">
                <h3 className="fw-bold mb-3">Registro</h3>

                <form onSubmit={handleRegistro}>
                    <div className="mb-3">
                        <label className="form-label fw-bold small">Rol</label>
                        <select
                            className="form-select bg-light border-0 py-2"
                            value={rol}
                            onChange={(e) => setRol(e.target.value)}
                            required
                        >
                            {roles.map((r) => (
                                <option key={r.id || r.nombre} value={r.nombre}>
                                    {r.nombre}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold small">Nombre</label>
                        <input
                            type="text"
                            className="form-control bg-light border-0 py-2"
                            value={nombre}
                            onChange={(e) => setNombre(e.target.value)}
                            required
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold small">{labelMatricula}</label>
                        <input
                            type="text"
                            className="form-control bg-light border-0 py-2"
                            value={matricula}
                            onChange={(e) => setMatricula(e.target.value)}
                            required
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold small">Teléfono</label>
                        <input
                            type="text"
                            className="form-control bg-light border-0 py-2"
                            value={telefono}
                            onChange={(e) => setTelefono(e.target.value)}
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold small">
                            {esAdministrativo ? 'División académica' : 'Carrera'}
                        </label>
                        {esAdministrativo ? (
                            <select
                                className="form-select bg-light border-0 py-2"
                                value={carrera}
                                onChange={(e) => setCarrera(e.target.value)}
                                required
                            >
                                <option value="">Seleccionar división...</option>
                                {divisiones.map((d) => (
                                    <option key={d.id} value={d.nombre}>
                                        {d.nombre}
                                    </option>
                                ))}
                            </select>
                        ) : (
                            <input
                                type="text"
                                className="form-control bg-light border-0 py-2"
                                value={carrera}
                                onChange={(e) => setCarrera(e.target.value)}
                            />
                        )}
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold small">Correo institucional</label>
                        <input
                            type="email"
                            className="form-control bg-light border-0 py-2"
                            value={emailInstitucional}
                            onChange={(e) => setEmailInstitucional(e.target.value)}
                            placeholder="matricula@utez.edu.mx"
                            required
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold small">Contraseña</label>
                        <input
                            type="password"
                            className="form-control bg-light border-0 py-2"
                            value={contrasena}
                            onChange={(e) => setContrasena(e.target.value)}
                            required
                        />
                    </div>

                    <button type="submit" className="btn btn-success fw-bold" style={{ backgroundColor: '#00a854', border: 'none' }}>
                        Registrarme
                    </button>
                </form>
            </div>
        </div>
    );
}