import React, { useEffect, useState } from 'react';
import Swal from 'sweetalert2';

export default function ReservaModal({ show, onClose, onSuccess, reservaAEditar }) {
    const [listaAreas, setListaAreas] = useState([]);
    const [listaUsuarios, setListaUsuarios] = useState([]);

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
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

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
                                <div className="col-md-6">
                                    <label className="form-label fw-bold small">Usuario *</label>
                                    <select
                                        className="form-select bg-light border-0"
                                        value={idUsuario}
                                        onChange={(e) => setIdUsuario(e.target.value)}
                                        required
                                    >
                                        <option value="">Seleccionar usuario...</option>
                                        {listaUsuarios.map((u) => (
                                            <option key={u.id} value={u.id}>
                                                {u.nombre} {u.matricula ? `- ${u.matricula}` : ''}
                                            </option>
                                        ))}
                                    </select>
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
                                <select className="form-select bg-light border-0" value="CONFIRMADA" disabled>
                                    <option value="CONFIRMADA">CONFIRMADA</option>
                                </select>
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

