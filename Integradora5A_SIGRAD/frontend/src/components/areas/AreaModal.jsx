import React from 'react';
import { MapPin, X, Image as ImageIcon, Plus } from 'lucide-react';

export default function AreaModal({ show, onClose }) {
  // Si 'show' es falso, el componente no se renderiza (evita errores visuales)
  if (!show) return null;

  return (
    <div className="modal d-block" style={{ backgroundColor: 'rgba(0,0,0,0.6)', zIndex: 1050 }}>
      <div className="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
        <div className="modal-content border-0 shadow-lg rounded-4">
          
          {/* Header del Modal */}
          <div className="modal-header border-0 p-4 pb-0">
            <div className="d-flex align-items-center gap-2">
              <div className="bg-success-subtle p-2 rounded-circle text-success d-flex align-items-center justify-content-center">
                <MapPin size={24} />
              </div>
              <h4 className="modal-title fw-bold text-dark">Crear Nueva Área Deportiva</h4>
            </div>
            {/* Botón X que cierra el modal */}
            <button type="button" className="btn-close shadow-none" onClick={onClose}></button>
          </div>

          <div className="modal-body p-4">
            <h6 className="fw-bold mb-3 text-dark">Información Básica</h6>
            
            <div className="mb-4">
              <label className="form-label small fw-bold text-dark">Nombre del Área <span className="text-danger">*</span></label>
              <input type="text" className="form-control bg-light border-0 py-2" placeholder="Ej: Cancha de Fútbol Principal" />
            </div>

            <div className="mb-3">
              <label className="form-label small fw-bold text-dark">Tipo de Deporte <span className="text-danger">*</span></label>
              <select className="form-select bg-light border-0 py-2 text-muted">
                <option>Seleccionar deporte...</option>
              </select>
            </div>

            {/* Banner de ayuda azul */}
            <div className="border border-primary border-dashed rounded-3 p-2 mb-4 text-center" style={{ borderStyle: 'dashed', backgroundColor: '#f0f7ff' }}>
              <a href="#" className="text-primary text-decoration-none small fw-bold d-flex align-items-center justify-content-center gap-2">
                <Plus size={16} /> ¿No encuentras tu deporte? Agrégalo aquí
              </a>
            </div>

            {/* Subida de Imagen */}
            <div className="mb-4">
              <label className="form-label small fw-bold text-dark">Imagen del Área Deportiva <span className="text-danger">*</span></label>
              <div className="border border-2 border-dashed rounded-4 p-5 text-center bg-white" style={{ cursor: 'pointer' }}>
                <ImageIcon size={48} className="text-muted mb-2" strokeWidth={1} />
                <p className="mb-1 fw-bold text-dark">Haz clic para subir una imagen</p>
                <p className="text-muted small mb-0">PNG, JPG, JPEG hasta 5MB</p>
              </div>
            </div>

            {/* Horarios */}
            <h6 className="fw-bold mb-3 text-dark">Horarios y Disponibilidad</h6>
            <div className="row">
              <div className="col-md-6 mb-3">
                <label className="form-label small fw-bold text-dark">Horario de Disponibilidad <span className="text-danger">*</span></label>
                <input type="text" className="form-control bg-light border-0 py-2" placeholder="Ej: 8:00 AM - 10:00 PM" />
              </div>
              <div className="col-md-6 mb-3">
                <label className="form-label small fw-bold text-dark">Estado Inicial <span className="text-danger">*</span></label>
                <select className="form-select bg-light border-0 py-2">
                  <option>Disponible</option>
                  <option>Ocupada</option>
                  <option>Mantenimiento</option>
                </select>
              </div>
            </div>
          </div>

          {/* Footer con los botones de acción */}
          <div className="modal-footer border-0 p-4 pt-0 d-flex gap-3">
            <button 
              type="button" 
              className="btn btn-outline-secondary flex-grow-1 py-2 fw-bold border-2 shadow-none" 
              style={{ borderRadius: '10px' }}
              onClick={onClose} // Función para cerrar al hacer clic en Cancelar
            >
              Cancelar
            </button>
            <button 
              type="button" 
              className="btn btn-success flex-grow-1 py-2 fw-bold d-flex align-items-center justify-content-center gap-2 shadow-none" 
              style={{ borderRadius: '10px', backgroundColor: '#00a854', border: 'none' }}
            >
              <MapPin size={18} /> Crear Área Deportiva
            </button>
          </div>

        </div>
      </div>
    </div>
  );
}