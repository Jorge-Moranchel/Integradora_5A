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

    // Si la reserva no es CONFIRMADA, el modal es solo lectura
    const soloLectura = reservaAEditar && reservaAEditar.estado !== 'CONFIRMADA';

    // Obtener la fecha de hoy en formato YYYY-MM-DD para bloquear el calendario
    const getHoyStr = () => {
        const ahora = new Date();
        return ahora.getFullYear() + '-' +
            String(ahora.getMonth() + 1).padStart(2, '0') + '-' +
            String(ahora.getDate()).padStart(2, '0');
    };
    const hoyStr = getHoyStr();

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

        // ✅ VALIDACIÓN 1: Orden de las horas
        if (horaInicio >= horaFin) {
            Swal.fire('Atención', 'La hora de salida debe ser posterior a la hora de entrada.', 'warning');
            return;
        }

        // ✅ VALIDACIÓN 2: Evitar horas en el pasado para el día de hoy
        if (fecha === hoyStr) {
            const ahora = new Date();
            const minutosActuales = ahora.getHours() * 60 + ahora.getMinutes();

            const [h, m] = horaInicio.split(':').map(Number);
            const minutosInicio = h * 60 + m;

            if (minutosInicio < minutosActuales) {
                Swal.fire('Atención', 'No puedes seleccionar una hora que ya pasó.', 'warning');
                return;
            }
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
                try { msg = await response.text(); } catch (_) {}
            }
            Swal.fire('Error', msg, 'error');
        } catch (error) {
            Swal.fire('Error', 'Backend no responde', 'error');
        }
    };

    if (!show) return null;

    const tituloModal =
        !reservaAEditar       ? 'Nueva Reserva'  :
            soloLectura       ? 'Detalle de Reserva' :
                'Editar Reserva';

    return (
        <div className="modal d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)', zIndex: 1050 }}>
            <div className="modal-dialog modal-lg modal-dialog-centered">
                <div className="modal-content border-0 shadow-sm rounded-4">
                    <div className="modal-header border-0 p-4 pb-0">
                        <h4 className="fw-bold m-0">{tituloModal}</h4>
                        <button type="button" className="btn-close" onClick={onClose}></button>
                    </div>

                    <form onSubmit={handleSubmit}>
                        <div className="modal-body p-4">
                            <div className="row mb-3">
                                <div className="col-md-6">
                                    <label className="form-label fw-bold small">Área *</label>
                                    {soloLectura ? (
                                        <p className="form-control-plaintext fw-semibold ps-1">
                                            {reservaAEditar.area?.nombre || 'N/A'}
                                        </p>
                                    ) : (
                                        <select
                                            className="form-select bg-light border-0"
                                            value={idArea}
                                            onChange={(e) => setIdArea(e.target.value)}
                                            required
                                        >
                                            <option value="">Seleccionar Área...</option>
                                            {listaAreas.map((a) => (
                                                <option key={a.id} value={a.id}>{a.nombre}</option>
                                            ))}
                                        </select>
                                    )}
                                </div>
                                <div className="col-md-6" ref={soloLectura ? null : usuarioComboRef}>
                                    <label className="form-label fw-bold small" id="label-usuario-reserva">
                                        Usuario *
                                    </label>
                                    {soloLectura ? (
                                        <p className="form-control-plaintext fw-semibold ps-1">
                                            {reservaAEditar.usuario?.nombre || 'N/A'}
                                            {reservaAEditar.usuario?.matricula ? ` — ${reservaAEditar.usuario.matricula}` : ''}
                                        </p>
                                    ) : (
                                        <div className="position-relative" ref={usuarioComboRef}>
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
                                            >
                                                {usuarioSeleccionado ? `${usuarioSeleccionado.nombre} (${usuarioSeleccionado.matricula || 'Sin matricula'})` : 'Seleccionar usuario...'}
                                            </button>
                                            {usuarioMenuAbierto && (
                                                <div className="position-absolute w-100 bg-white border rounded shadow-sm mt-1 z-3" style={{ maxHeight: '200px', overflowY: 'auto' }}>
                                                    <input
                                                        ref={busquedaUsuarioInputRef}
                                                        type="text"
                                                        className="form-control border-0 border-bottom sticky-top top-0"
                                                        placeholder="Buscar usuario..."
                                                        value={busquedaUsuario}
                                                        onChange={(e) => setBusquedaUsuario(e.target.value)}
                                                    />
                                                    {usuariosFiltrados.map(u => (
                                                        <div
                                                            key={u.id}
                                                            className="p-2 border-bottom"
                                                            style={{ cursor: 'pointer' }}
                                                            onClick={() => {
                                                                setIdUsuario(String(u.id));
                                                                setUsuarioMenuAbierto(false);
                                                            }}
                                                        >
                                                            {u.nombre} <span className="text-muted small">({u.matricula || 'N/A'})</span>
                                                        </div>
                                                    ))}
                                                </div>
                                            )}
                                        </div>
                                    )}
                                </div>
                            </div>

                            {/* Fila de Fechas y Horas */}
                            <div className="row mb-3">
                                <div className="col-md-4">
                                    <label className="form-label fw-bold small">Fecha *</label>
                                    <input
                                        type="date"
                                        className="form-control bg-light border-0"
                                        value={fecha}
                                        min={hoyStr} /* ✅ Bloquea el calendario visualmente en HTML */
                                        onChange={(e) => setFecha(e.target.value)}
                                        required
                                        disabled={soloLectura}
                                    />
                                </div>
                                <div className="col-md-4">
                                    <label className="form-label fw-bold small">Hora Inicio *</label>
                                    <input
                                        type="time"
                                        className="form-control bg-light border-0"
                                        value={horaInicio}
                                        onChange={(e) => setHoraInicio(e.target.value)}
                                        required
                                        disabled={soloLectura}
                                    />
                                </div>
                                <div className="col-md-4">
                                    <label className="form-label fw-bold small">Hora Fin *</label>
                                    <input
                                        type="time"
                                        className="form-control bg-light border-0"
                                        value={horaFin}
                                        onChange={(e) => setHoraFin(e.target.value)}
                                        required
                                        disabled={soloLectura}
                                    />
                                </div>
                            </div>

                            <div className="mb-3">
                                <label className="form-label fw-bold small">Motivo / Descripción (Opcional)</label>
                                <textarea
                                    className="form-control bg-light border-0"
                                    rows="3"
                                    value={descripcion}
                                    onChange={(e) => setDescripcion(e.target.value)}
                                    disabled={soloLectura}
                                />
                            </div>
                        </div>

                        <div className="modal-footer border-0 p-4 pt-0">
                            <button type="button" className="btn btn-light fw-bold px-4" onClick={onClose}>Cancelar</button>
                            {!soloLectura && (
                                <button type="submit" className="btn btn-primary fw-bold px-4">
                                    {reservaAEditar ? 'Actualizar Reserva' : 'Guardar Reserva'}
                                </button>
                            )}
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}