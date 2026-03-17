package mx.edu.utez.Integradora5A_SIGRAD.service;

import mx.edu.utez.Integradora5A_SIGRAD.model.Carrera;
import mx.edu.utez.Integradora5A_SIGRAD.repository.CarreraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CarreraService {

    @Autowired
    private CarreraRepository carreraRepository;

    @Transactional(readOnly = true)
    public List<Carrera> listarTodas() {
        return carreraRepository.findAll();
    }

    public Carrera guardar(Carrera carrera) throws Exception {
        // Validación: que el nombre no sea nulo ni vacío
        if (carrera.getNombre() == null || carrera.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre de la carrera es obligatorio");
        }
        return carreraRepository.save(carrera);
    }
}