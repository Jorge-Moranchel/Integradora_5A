import React, { useState, useEffect } from 'react';
import { MapPin, Image as ImageIcon, Clock } from 'lucide-react';

// 1. Agregamos 'areaToEdit' a las propiedades que recibe el modal
export default function AreaModal({ show, onClose, fetchAreas, areaToEdit }) {
  const [formData, setFormData] = useState({
    nombre: '',
    ubicacion: '',
    horaApertura: '',
    horaCierre: '',
    imagen: ''
  });

  // 2. EL CEREBRO DEL MODAL: Si nos mandan un área para editar, rellenamos los campos.
  // Si no (es una nueva), los dejamos en blanco.
  useEffect(() => {
    if (areaToEdit) {
      setFormData({
        nombre: areaToEdit.nombre,
        ubicacion: areaToEdit.ubicacion,
        horaApertura: areaToEdit.horaApertura,
        horaCierre: areaToEdit.horaCierre,
        imagen: '' // La imagen se queda vacía por si no quiere cambiarla
      });
    } else {
      setFormData({
        nombre: '', ubicacion: '', horaApertura: '', horaCierre: '', imagen: ''
      });
    }
  }, [areaToEdit, show]);

  if (!show) return null;

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setFormData({ ...formData, imagen: reader.result });
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (formData.horaApertura >= formData.horaCierre) {
      alert('Error: La hora de apertura debe ser menor a la hora de cierre.');
      return;
    }

    // 3. DECISIÓN DE RUTA: ¿Vamos a Crear o a Actualizar?
    const url = areaToEdit
        ? `http://localhost:8080/api/areas/actualizar/${areaToEdit.id}`
        : 'http://localhost:8080/api/areas/registrar';

    // Si es editar usamos PUT, si es crear usamos POST
    const metodo = areaToEdit ? 'PUT' : 'POST';

    try {
      const response = await fetch(url, {
        method: metodo,
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
      });

      if (response.ok) {
        alert(areaToEdit ? 'Área actualizada correctamente' : 'Área deportiva registrada correctamente');
        fetchAreas();
        onClose();
      } else {
        const errorText = await response.text();
        alert(`Error del servidor: ${errorText}`);
      }
    } catch (error) {
      console.error('Error al conectar con el servidor:', error);
      alert('Error de conexión con el servidor.');
    }
  };

  return (
      <div className="modal d-block" style={{ backgroundColor: 'rgba(0,0,0,0.6)', zIndex: 1050 }}>
        <div className="modal-dialog modal-lg modal-dialog-centered">
          <form onSubmit={handleSubmit} className="modal-content border-0 shadow-lg rounded-4">

            <div className="modal-header border-0 p-4 pb-0">
              <div className="d-flex align-items-center gap-2">
                <div className="bg-success-subtle p-2 rounded-circle text-success">
                  <MapPin size={24} />
                </div>
                {/* 4. Título dinámico */}
                <h4 className="modal-title fw-bold text-dark">
                  {areaToEdit ? 'Editar Área Deportiva' : 'Crear Nueva Área Deportiva'}
                </h4>
              </div>
              <button type="button" className="btn-close shadow-none" onClick={onClose}></button>
            </div>

            <div className="modal-body p-4">
              <h6 className="fw-bold mb-3 text-dark">Información de la Zona</h6>

              <div className="mb-3">
                <label className="form-label small fw-bold">Nombre del Área <span className="text-danger">*</span></label>
                <input
                    type="text"
                    required
                    className="form-control bg-light border-0 py-2"
                    placeholder="Ej: Cancha de Fútbol Principal"
                    value={formData.nombre}
                    onChange={(e) => setFormData({...formData, nombre: e.target.value})}
                />
              </div>

              <div className="mb-3">
                <label className="form-label small fw-bold">Descripción de la Ubicación <span className="text-danger">*</span></label>
                <textarea
                    required
                    className="form-control bg-light border-0 py-2"
                    rows="2"
                    placeholder="Ej: Detrás del edificio 5, junto al estacionamiento"
                    value={formData.ubicacion}
                    onChange={(e) => setFormData({...formData, ubicacion: e.target.value})}
                ></textarea>
              </div>

              <div className="mb-4">
                <label className="form-label small fw-bold">
                  Imagen del Área
                  {/* 5. Si estamos editando, la imagen no es obligatoria */}
                  {areaToEdit && <span className="text-muted fw-normal ms-1">(Opcional si no deseas cambiarla)</span>}
                  {!areaToEdit && <span className="text-danger">*</span>}
                </label>
                <input
                    type="file"
                    required={!areaToEdit}
                    accept="image/*"
                    className="form-control bg-light border-0"
                    onChange={handleImageChange}
                />
              </div>

              <h6 className="fw-bold mb-3 text-dark">Horarios de Disponibilidad</h6>
              <div className="row">
                <div className="col-md-6 mb-3">
                  <label className="form-label small fw-bold text-dark">
                    <Clock size={14} className="me-1"/> Hora Apertura <span className="text-danger">*</span>
                  </label>
                  <input
                      type="time"
                      required
                      className="form-control bg-light border-0 py-2"
                      value={formData.horaApertura}
                      onChange={(e) => setFormData({...formData, horaApertura: e.target.value})}
                  />
                </div>
                <div className="col-md-6 mb-3">
                  <label className="form-label small fw-bold text-dark">
                    <Clock size={14} className="me-1"/> Hora Cierre <span className="text-danger">*</span>
                  </label>
                  <input
                      type="time"
                      required
                      className="form-control bg-light border-0 py-2"
                      value={formData.horaCierre}
                      onChange={(e) => setFormData({...formData, horaCierre: e.target.value})}
                  />
                </div>
              </div>
            </div>

            <div className="modal-footer border-0 p-4 pt-0 d-flex gap-3">
              <button type="button" className="btn btn-outline-secondary flex-grow-1 py-2 fw-bold rounded-3 shadow-none" onClick={onClose}>
                Cancelar
              </button>
              <button type="submit" className="btn btn-success flex-grow-1 py-2 fw-bold rounded-3 shadow-none">
                {/* 6. Botón dinámico */}
                {areaToEdit ? 'Guardar Cambios' : 'Guardar Área'}
              </button>
            </div>

          </form>
        </div>
      </div>
  );
}