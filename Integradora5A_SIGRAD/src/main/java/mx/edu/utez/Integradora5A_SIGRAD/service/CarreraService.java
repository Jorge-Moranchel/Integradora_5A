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
        if (carrera.getAbreviatura()== null || carrera.getAbreviatura().trim().isEmpty()){
            throw new Exception("La abreviatura de la carrera es obligatoria");
        }
        return carreraRepository.save(carrera);
    }

    public Carrera actualizar(Long id, Carrera carreraUpdate) throws Exception {
        Carrera existente = carreraRepository.findById(id)
                .orElseThrow(() -> new Exception("Error: Carrera no encontrada"));

        if (carreraUpdate.getNombre() == null || carreraUpdate.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre de la carrera es obligatorio");
        }

        existente.setNombre(carreraUpdate.getNombre());
        existente.setAbreviatura(carreraUpdate.getAbreviatura().trim());
        existente.setDescripcion(carreraUpdate.getDescripcion());
        return carreraRepository.save(existente);
    }

    public Carrera cambiarEstado(Long id) throws Exception {
        Carrera existente = carreraRepository.findById(id)
                .orElseThrow(() -> new Exception("Error: Carrera no encontrada"));

        // Invertimos el estado (Si era true pasa a false, y viceversa)
        existente.setHabilitada(!existente.getHabilitada());
        return carreraRepository.save(existente);
    }

    private String abreviatura(String nombre){
        if (nombre== null || nombre.trim().isEmpty()){
                return "";
        }
        Carrera carrera = new Carrera();
        String [] abreviatura=nombre.trim().split("\\s+");
        StringBuilder iniciales=new StringBuilder();
        for (String inicial: abreviatura){
            if (inicial.length()>2){
                iniciales.append(inicial.charAt(0));
            }
        }
        return iniciales.toString().toUpperCase();
    }
}