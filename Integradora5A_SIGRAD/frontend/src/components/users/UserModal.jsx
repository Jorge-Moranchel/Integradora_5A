import React, { useState, useEffect } from 'react';
import { UserPlus, EyeOff, Eye, Save } from 'lucide-react';
import Swal from 'sweetalert2';

export default function UserModal({ show, onClose, userToEdit }) {
  const [nombre, setNombre] = useState('');
  const [matricula, setMatricula] = useState('');
  const [telefono, setTelefono] = useState('');
  const [email, setEmail] = useState('');
  const [carrera, setCarrera] = useState('');
  const [rol, setRol] = useState('');
  const [password, setPassword] = useState('');
  const [showPass, setShowPass] = useState(false);

  const [listaCarreras, setListaCarreras] = useState([]);
  const [listaRoles, setListaRoles] = useState([]);

  useEffect(() => {
    if (show) {
      cargarCatalogos();
      if (userToEdit) {
        setNombre(userToEdit.nombre);
        setMatricula(userToEdit.matricula);
        setTelefono(userToEdit.telefono || '');
        setEmail(userToEdit.emailInstitucional);
        setCarrera(userToEdit.carrera || '');
        setRol(userToEdit.rol || '');
        setPassword('');
      } else {
        limpiarCampos();
      }
    }
  }, [show, userToEdit]);

  const cargarCatalogos = async () => {
    try {
      const [resC, resR] = await Promise.all([
        fetch('http://localhost:8080/api/carreras/listar'),
        fetch('http://localhost:8080/api/roles')
      ]);
      setListaCarreras(await resC.json());
      setListaRoles(await resR.json());
    } catch (e) { console.error(e); }
  };

  const limpiarCampos = () => {
    setNombre(''); setMatricula(''); setTelefono(''); setEmail('');
    setCarrera(''); setRol(''); setPassword('');
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    const url = userToEdit ? `http://localhost:8080/api/usuarios/actualizar/${userToEdit.id}` : 'http://localhost:8080/api/usuarios/registrar';
    const method = userToEdit ? 'PUT' : 'POST';

    try {
      const response = await fetch(url, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          nombre, matricula, telefono, carrera,
          emailInstitucional: email,
          contrasena: password,
          rol
        }),
      });

      if (response.ok) {
        Swal.fire('¡Éxito!', userToEdit ? 'Usuario actualizado' : 'Usuario registrado', 'success');
        onClose();
      } else {
        const data = await response.json();
        Swal.fire('Error', data.message, 'error');
      }
    } catch (error) { Swal.fire('Error', 'Backend no responde', 'error'); }
  };

  if (!show) return null;

  return (
      <div className="modal d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)', zIndex: 1050 }}>
        <div className="modal-dialog modal-lg modal-dialog-centered">
          <div className="modal-content border-0 shadow-lg rounded-4 text-dark">
            <div className="modal-header border-0 p-4 pb-0">
              <h4 className="fw-bold text-dark">
                {userToEdit ? <Save className="me-2 text-primary"/> : <UserPlus className="me-2 text-success"/>}
                {userToEdit ? 'Editar Usuario' : 'Registrar Nuevo Usuario'}
              </h4>
              <button type="button" className="btn-close" onClick={onClose}></button>
            </div>
            <div className="modal-body p-4">
              <div className="mb-3">
                <label className="form-label small fw-bold">Nombre Completo *</label>
                <input
                    type="text"
                    className="form-control bg-light border-0"
                    value={nombre}
                    onChange={(e)=>setNombre(e.target.value)}
                    placeholder="Ej. Jorge Moranchel"
                    required
                />
              </div>
              <div className="row mb-3">
                <div className="col-md-6">
                  <label className="form-label small fw-bold">Matrícula *</label>
                  <input
                      type="text"
                      className="form-control bg-light border-0"
                      value={matricula}
                      onChange={(e)=>setMatricula(e.target.value)}
                      placeholder="20243DS059"
                      required
                  />
                </div>
                <div className="col-md-6">
                  <label className="form-label small fw-bold">Teléfono</label>
                  <input
                      type="text"
                      className="form-control bg-light border-0"
                      value={telefono}
                      onChange={(e)=>setTelefono(e.target.value)}
                      placeholder="+52 777 123 4567"
                  />
                </div>
              </div>
              <div className="row mb-3">
                <div className="col-md-6">
                  <label className="form-label small fw-bold">Correo Institucional *</label>
                  <input
                      type="email"
                      className="form-control bg-light border-0"
                      value={email}
                      onChange={(e)=>setEmail(e.target.value)}
                      placeholder="matricula@utez.edu.mx"
                      required
                  />
                </div>
                <div className="col-md-6">
                  <label className="form-label small fw-bold">Carrera *</label>
                  <select className="form-select bg-light border-0" value={carrera} onChange={(e)=>setCarrera(e.target.value)} required>
                    <option value="">Seleccionar carrera...</option>
                    {listaCarreras.map(c => <option key={c.id} value={c.nombre}>{c.nombre}</option>)}
                  </select>
                </div>
              </div>
              <div className="row mb-3">
                <div className="col-md-6">
                  <label className="form-label small fw-bold">Rol *</label>
                  <select className="form-select bg-light border-0" value={rol} onChange={(e)=>setRol(e.target.value)} required>
                    <option value="">Seleccionar rol...</option>
                    {listaRoles.filter(r=>r.activo).map(r => <option key={r.id} value={r.nombre}>{r.nombre}</option>)}
                  </select>
                </div>
                <div className="col-md-6">
                  <label className="form-label small fw-bold">{userToEdit ? 'Nueva Contraseña (Opcional)' : 'Contraseña *'}</label>
                  <div className="input-group">
                    <input
                        type={showPass ? "text" : "password"}
                        className="form-control bg-light border-0"
                        value={password}
                        onChange={(e)=>setPassword(e.target.value)}
                        placeholder="••••••••"
                    />
                    <button className="btn btn-light border-0" type="button" onClick={()=>setShowPass(!showPass)}>{showPass ? <Eye size={18}/> : <EyeOff size={18}/>}</button>
                  </div>
                </div>
              </div>
            </div>
            <div className="modal-footer border-0 p-4 pt-0">
              <button className="btn btn-outline-secondary flex-grow-1" style={{borderRadius: '10px'}} onClick={onClose}>Cancelar</button>
              <button className="btn btn-success flex-grow-1" style={{backgroundColor: '#00a854', borderRadius: '10px'}} onClick={handleRegister}>
                {userToEdit ? 'Guardar Cambios' : 'Registrar Usuario'}
              </button>
            </div>
          </div>
        </div>
      </div>
  );
}