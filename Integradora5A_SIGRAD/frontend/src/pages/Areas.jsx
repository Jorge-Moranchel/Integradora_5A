import React, { useState } from 'react';
import { Search, Plus, MapPin } from 'lucide-react';
import AreaModal from '../components/areas/AreaModal';

export default function Areas() {
  // Estado para controlar la visibilidad del modal
  const [showModal, setShowModal] = useState(false);

  return (
    <div className="p-5 animate__animated animate__fadeIn">
      {/* Header */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold mb-1 text-dark">Áreas Deportivas</h2>
          <p className="text-muted small">Gestiona todas las canchas y espacios deportivos</p>
        </div>
        {/* Este botón ahora SÍ tiene la función para abrir */}
        <button 
          className="btn btn-success d-flex align-items-center gap-2 px-4 py-2 fw-bold"
          onClick={() => setShowModal(true)}
        >
          <Plus size={18} /> Nueva Área
        </button>
      </div>

      {/* Barra de Búsqueda y Filtros */}
      <div className="search-card p-3 mb-5 shadow-sm bg-white rounded-3 border">
        <div className="d-flex align-items-center gap-3">
          <div className="input-group" style={{ maxWidth: '450px' }}>
            <span className="input-group-text bg-white border-end-0 text-muted">
              <Search size={18} />
            </span>
            <input 
              type="text" 
              className="form-control border-start-0 shadow-none" 
              placeholder="Buscar por nombre o tipo..." 
            />
          </div>

          <div className="btn-group shadow-sm">
            <button className="btn active fw-bold">Todos</button>
            <button className="btn">Disponible</button>
            <button className="btn">Ocupada</button>
            <button className="btn">Mantenimiento</button>
          </div>
        </div>
      </div>

      {/* Pantalla vacía (Empty State) */}
      <div className="text-center py-5 mt-4">
        <MapPin size={80} className="text-muted mb-3 opacity-25" strokeWidth={1} />
        <h4 className="fw-bold text-dark">No se encontraron áreas</h4>
        <p className="text-muted">Intenta con otros filtros o crea una nueva área deportiva.</p>
      </div>

      {/* Llamada al Modal pasando las propiedades necesarias */}
      <AreaModal 
        show={showModal} 
        onClose={() => setShowModal(false)} 
      />
    </div>
  );
}