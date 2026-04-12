import React, { useEffect, useState, useMemo, useRef } from 'react';
import Swal from 'sweetalert2';

export default function ReservaModal({ show, onClose, onSuccess, reservaAEditar }) {
    const [listaAreas, setListaAreas] = useState([]);
    const [listaUsuarios, setListaUsuarios] = useState([]);
    const [busquedaUsuario, setBusquedaUsuario] = useState('');
    const [usuarioMenuAbierto, setUsuarioMenuAbierto] = useState(false);
    const usuarioComboRef = useRef(null);
    const busquedaUsuarioInputRef = useRef(null);

    const [idArea, setIdArea] = useState('');
    const [idUsuario, setIdUsuario] = useState('');
    const [fecha, setFecha] = useState('');
    const [horaInicio, setHoraInicio] = useState('');
    const [horaFin, setHoraFin] = useState('');
    const [descripcion, setDescripcion] = useState('');

    useEffect(() => {
        if (!show) return;
        cargarCatalogos();
    }, [show]);

    useEffect(() => {
        if (!show) {
            setBusquedaUsuario('');
            setUsuarioMenuAbierto(false);
        }
    }, [show]);

    useEffect(() => {
        if (!usuarioMenuAbierto) return;
        const onDoc = (ev) => {
            if (usuarioComboRef.current && !usuarioComboRef.current.contains(ev.target)) {
                setUsuarioMenuAbierto(false);
            }
        };
        document.addEventListener('mousedown', onDoc);
        return () => document.removeEventListener('mousedown', onDoc);
    }, [usuarioMenuAbierto]);

    useEffect(() => {
        if (usuarioMenuAbierto) {
            queueMicrotask(() => busquedaUsuarioInputRef.current?.focus());
        }
    }, [usuarioMenuAbierto]);

    const usuarioSeleccionado = useMemo(
        () => listaUsuarios.find((u) => String(u.id) === String(idUsuario)),
        [listaUsuarios, idUsuario]
    );

    const usuariosFiltrados = useMemo(() => {
        const q = busquedaUsuario.trim().toLowerCase();
        if (!q) return listaUsuarios;
        return listaUsuarios.filter((u) => {
            if (idUsuario && String(u.id) === String(idUsuario)) return true;
            const nombre = (u.nombre || '').toLowerCase();
            const matricula = (u.matricula || '').toLowerCase();
            const email = (u.emailInstitucional || '').toLowerCase();
            const rol = (u.rol || '').toLowerCase();
            return nombre.includes(q) || matricula.includes(q) || email.includes(q) || rol.includes(q);
        });
    }, [listaUsuarios, busquedaUsuario, idUsuario]);

    useEffect(() => {
        if (!show) return;

        if (reservaAEditar) {
            setIdArea(reservaAEditar.area?.id ? String(reservaAEditar.area.id) : '');
            setIdUsuario(reservaAEditar.usuario?.id ? String(reservaAEditar.usuario.id) : '');
            setFecha(reservaAEditar.fecha || '');
            setHoraInicio(reservaAEditar.horaInicio || '');
            setHoraFin(reservaAEditar.horaFin || '');
            setDescripcion(reservaAEditar.descripcion || '');
        } else {
            limpiarFormulario();
        }
    }, [reservaAEditar, show]);

    const cargarCatalogos = async () => {
        try {
            const [resAreas, resUsuarios] = await Promise.all([
                fetch('http://localhost:8080/api/areas/listar'),
                fetch('http://localhost:8080/api/usuarios/listar')
            ]);

            if (resAreas.ok) {
                const areas = await resAreas.json();
                setListaAreas(Array.isArray(areas) ? areas : []);
            }
            if (resUsuarios.ok) {
                const usuarios = await resUsuarios.json();
                setListaUsuarios(Array.isArray(usuarios) ? usuarios : []);
            }
        } catch (e) {
            Swal.fire('Error', 'No se pudieron cargar áreas/usuarios', 'error');
        }
    };

    const limpiarFormulario = () => {
        setIdArea('');
        setIdUsuario('');
        setFecha('');
        setHoraInicio('');
        setHoraFin('');
        setDescripcion('');
        setBusquedaUsuario('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!idUsuario) {
            Swal.fire('Atención', 'Selecciona un usuario', 'warning');
            return;
        }

        const esEdicion = !!reservaAEditar;
        const url = esEdicion
            ? `http://localhost:8080/api/reservas/actualizar/${reservaAEditar.id}`
            : 'http://localhost:8080/api/reservas/crear';
        const method = esEdicion ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    idArea: idArea ? Number(idArea) : null,
                    idUsuario: idUsuario ? Number(idUsuario) : null,
                    fecha,
                    horaInicio,
                    horaFin,
                    descripcion
                })
            });

            if (response.ok) {
                Swal.fire('Éxito', esEdicion ? 'Reserva actualizada' : 'Reserva creada', 'success');
                if (onSuccess) onSuccess();
                onClose();
                return;
            }

            let msg = 'No se pudo guardar la reserva';
            try {
                const data = await response.json();
                msg = data?.mensaje || data?.message || msg;
            } catch (_) {
                try {
                    msg = await response.text();
                } catch (_) {}
            }

            // Error de traslape o cualquier error funcional del backend
            Swal.fire('Error', msg, 'error');
        } catch (error) {
            Swal.fire('Error', 'Backend no responde', 'error');
        }
    };

    if (!show) return null;

    const estadoVista = reservaAEditar?.estado || 'CONFIRMADA';
    const estadoBadgeClass =
        estadoVista === 'CONFIRMADA'
            ? 'bg-success bg-opacity-10 text-success'
            : estadoVista === 'CANCELADA'
              ? 'bg-danger bg-opacity-10 text-danger'
              : 'bg-secondary bg-opacity-10 text-secondary';

    return (
        <div className="modal d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)', zIndex: 1050 }}>
            <div className="modal-dialog modal-lg modal-dialog-centered">
                <div className="modal-content border-0 shadow-sm rounded-4">
                    <div className="modal-header border-0 p-4 pb-0">
                        <h4 className="fw-bold m-0">
                            {reservaAEditar ? 'Editar Reserva' : 'Nueva Reserva'}
                        </h4>
                        <button type="button" className="btn-close" onClick={onClose}></button>
                    </div>

                    <form onSubmit={handleSubmit}>
                        <div className="modal-body p-4">
                            <div className="row mb-3">
                                <div className="col-md-6">
                                    <label className="form-label fw-bold small">Área *</label>
                                    <select
                                        className="form-select bg-light border-0"
                                        value={idArea}
                                        onChange={(e) => setIdArea(e.target.value)}
                                        required
                                    >
                                        <option value="">Seleccionar área...</option>
                                        {listaAreas.map((a) => (
                                            <option key={a.id} value={a.id}>{a.nombre}</option>
                                        ))}
                                    </select>
                                </div>
                                <div className="col-md-6 position-relative" ref={usuarioComboRef}>
                                    <label className="form-label fw-bold small" id="label-usuario-reserva">
                                        Usuario *
                                    </label>
                                    <button
                                        type="button"
                                        className={`form-select bg-light border-0 text-start text-wrap ${!idUsuario ? 'text-muted' : ''}`}
                                        style={{ whiteSpace: 'normal', wordBreak: 'break-word', minHeight: '2.5rem' }}
                                        onClick={() => {
                                            setUsuarioMenuAbierto((o) => {
                                                const next = !o;
                                                if (next) setBusquedaUsuario('');
                                                return next;
                                            });
                                        }}
                                        aria-expanded={usuarioMenuAbierto}
                                        aria-haspopup="listbox"
                                        aria-labelledby="label-usuario-reserva"
                                    >
                                        {usuarioSeleccionado
                                            ? `${usuarioSeleccionado.nombre}${usuarioSeleccionado.matricula ? ` — ${usuarioSeleccionado.matricula}` : ''}`
                                            : 'Seleccionar usuario...'}
                                    </button>
                                    {usuarioMenuAbierto && (
                                        <div
                                            className="position-absolute start-0 end-0 mt-1 rounded-3 border bg-white shadow overflow-hidden"
                                            style={{
                                                zIndex: 2000,
                                                maxHeight: 'min(280px, 50vh)',
                                                display: 'flex',
                                                flexDirection: 'column',
                                                minWidth: 0
                                            }}
                                            role="listbox"
                                        >
                                            <div className="p-2 border-bottom bg-light rounded-top-3">
                                                <input
                                                    ref={busquedaUsuarioInputRef}
                                                    type="search"
                                                    className="form-control form-control-sm border-0 bg-white"
                                                    placeholder="Buscar por nombre, matrícula, correo o rol..."
                                                    value={busquedaUsuario}
                                                    onChange={(e) => setBusquedaUsuario(e.target.value)}
                                                    autoComplete="off"
                                                    aria-label="Buscar usuario"
                                                    onMouseDown={(e) => e.stopPropagation()}
                                                />
                                            </div>
                                            <div
                                                className="overflow-y-auto overflow-x-hidden py-1 rounded-bottom-3"
                                                style={{ minWidth: 0, maxWidth: '100%' }}
                                            >
                                                {usuariosFiltrados.length === 0 ? (
                                                    <div className="px-3 py-2 small text-muted text-wrap">Sin coincidencias</div>
                                                ) : (
                                                    usuariosFiltrados.map((u) => (
                                                        <button
                                                            key={u.id}
                                                            type="button"
                                                            role="option"
                                                            aria-selected={String(u.id) === String(idUsuario)}
                                                            className="dropdown-item py-2 px-3 small text-start text-wrap text-break w-100"
                                                            style={{ whiteSpace: 'normal', wordBreak: 'break-word' }}
                                                            onMouseDown={(e) => e.preventDefault()}
                                                            onClick={() => {
                                                                setIdUsuario(String(u.id));
                                                                setUsuarioMenuAbierto(false);
                                                                setBusquedaUsuario('');
                                                            }}
                                                        >
                                                            {u.nombre}
                                                            {u.matricula ? ` — ${u.matricula}` : ''}
                                                        </button>
                                                    ))
                                                )}
                                            </div>
                                        </div>
                                    )}
                                </div>
                            </div>

                            <div className="row mb-3">
                                <div className="col-md-4">
                                    <label className="form-label fw-bold small">Fecha *</label>
                                    <input
                                        type="date"
                                        className="form-control bg-light border-0"
                                        value={fecha}
                                        onChange={(e) => setFecha(e.target.value)}
                                        required
                                    />
                                </div>
                                <div className="col-md-4">
                                    <label className="form-label fw-bold small">Hora inicio *</label>
                                    <input
                                        type="time"
                                        className="form-control bg-light border-0"
                                        value={horaInicio}
                                        onChange={(e) => setHoraInicio(e.target.value)}
                                        required
                                    />
                                </div>
                                <div className="col-md-4">
                                    <label className="form-label fw-bold small">Hora fin *</label>
                                    <input
                                        type="time"
                                        className="form-control bg-light border-0"
                                        value={horaFin}
                                        onChange={(e) => setHoraFin(e.target.value)}
                                        required
                                    />
                                </div>
                            </div>

                            <div className="mb-3">
                                <label className="form-label fw-bold small">Descripción</label>
                                <textarea
                                    className="form-control bg-light border-0"
                                    rows="3"
                                    value={descripcion}
                                    onChange={(e) => setDescripcion(e.target.value)}
                                    placeholder="Detalle opcional de la reserva"
                                />
                            </div>

                            <div className="mb-2">
                                <label className="form-label fw-bold small">Estado</label>
                                <div className="d-flex flex-column gap-1">
                                    <span
                                        className={`badge ${estadoBadgeClass} fw-bold align-self-start`}
                                        style={{ fontSize: '11px', letterSpacing: '0.5px' }}
                                    >
                                        {estadoVista}
                                    </span>
                                    {!reservaAEditar && (
                                        <p className="text-muted small mb-0">
                                            Al crear la reserva queda <strong>confirmada</strong>. No se puede dar de alta como
                                            completada o cancelada: lo completa el sistema cuando pasa la fecha/hora, y cancelar
                                            solo se hace desde el historial.
                                        </p>
                                    )}
                                </div>
                            </div>
                        </div>

                        <div className="modal-footer border-0 p-4 pt-0">
                            <button type="button" className="btn btn-outline-secondary flex-grow-1" onClick={onClose}>
                                Cancelar
                            </button>
                            <button type="submit" className="btn btn-success flex-grow-1">
                                {reservaAEditar ? 'Guardar Cambios' : 'Crear Reserva'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}

