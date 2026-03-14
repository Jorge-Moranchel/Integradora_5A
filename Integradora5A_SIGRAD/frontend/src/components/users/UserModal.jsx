import React from 'react';
import { UserPlus, X, EyeOff } from 'lucide-react';

export default function UserModal({ show, onClose }) {
  if (!show) return null;

  return (
    <div className="modal d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)', zIndex: 1050 }}>
      <div className="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
        <div className="modal-content border-0 shadow-lg rounded-4">
          
          {/* Header del Modal */}
          <div className="modal-header border-0 p-4 pb-0">
            <div className="d-flex align-items-center gap-2">
              <div className="bg-success-subtle p-2 rounded-circle text-success d-flex align-items-center justify-content-center">
                <UserPlus size={24} />
              </div>
              <h4 className="modal-title fw-bold">Registrar Nuevo Usuario</h4>
            </div>
            <button type="button" className="btn-close shadow-none" onClick={onClose}></button>
          </div>

          <div className="modal-body p-4">
            {/* Sección: Información Personal */}
            <h6 className="fw-bold mb-3 border-bottom pb-2">Información Personal</h6>
            <div className="mb-4">
              <label className="form-label small fw-semibold">Nombre Completo <span className="text-danger">*</span></label>
              <input type="text" className="form-control bg-light border-0 py-2" placeholder="Ej: Juan Pérez García" />
            </div>

            <div className="row mb-4">
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Matrícula <span className="text-danger">*</span></label>
                <input type="text" className="form-control bg-light border-0 py-2" placeholder="Ej: E2024001 o D2024001" />
              </div>
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Teléfono <span className="text-danger">*</span></label>
                <input type="text" className="form-control bg-light border-0 py-2" placeholder="Ej: +54 11 1234-5678" />
              </div>
            </div>

            {/* Sección: Información Académica */}
            <h6 className="fw-bold mb-3 border-bottom pb-2">Información Académica</h6>
            <div className="row mb-4">
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Correo Institucional <span className="text-danger">*</span></label>
                <input type="email" className="form-control bg-light border-0 py-2" placeholder="Ej: usuario@institucion.edu" />
              </div>
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Carrera <span className="text-danger">*</span></label>
                <select className="form-select bg-light border-0 py-2 text-muted">
                  <option>Seleccionar carrera...</option>
                </select>
              </div>
            </div>

            <div className="row mb-3">
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Rol <span className="text-danger">*</span></label>
                <select className="form-select bg-light border-0 py-2">
                  <option>Estudiante</option>
                  <option>Docente</option>
                  <option>Administrador</option>
                </select>
              </div>
              <div className="col-md-6">
                <label className="form-label small fw-semibold">Contraseña <span className="text-danger">*</span></label>
                <div className="input-group">
                  <input type="password" disable className="form-control bg-light border-0 py-2" placeholder="Mínimo 8 caracteres" />
                  <span className="input-group-text bg-light border-0 text-muted"><EyeOff size={18}/></span>
                </div>
                <p className="text-muted mt-1" style={{ fontSize: '11px' }}>La contraseña debe tener al menos 8 caracteres</p>
              </div>
            </div>
          </div>

          {/* Footer del Modal (Los botones de tu segunda imagen) */}
          <div className="modal-footer border-0 p-4 pt-0 d-flex gap-3">
            <button 
              type="button" 
              className="btn btn-outline-secondary flex-grow-1 py-2 fw-bold border-2" 
              style={{ borderRadius: '10px' }}
              onClick={onClose}
            >
              Cancelar
            </button>
            <button 
              type="button" 
              className="btn btn-success flex-grow-1 py-2 fw-bold d-flex align-items-center justify-content-center gap-2" 
              style={{ borderRadius: '10px', backgroundColor: '#00a854', border: 'none' }}
            >
              <UserPlus size={18} /> Registrar Usuario
            </button>
          </div>

        </div>
      </div>
    </div>
  );
}