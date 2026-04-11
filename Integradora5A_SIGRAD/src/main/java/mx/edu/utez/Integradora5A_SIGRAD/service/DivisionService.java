package mx.edu.utez.Integradora5A_SIGRAD.service;

import mx.edu.utez.Integradora5A_SIGRAD.model.Division;
import mx.edu.utez.Integradora5A_SIGRAD.repository.DivisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DivisionService {

    @Autowired
    private DivisionRepository divisionRepository;

    public List<Division> listar() {
        return divisionRepository.findAll();
    }

    public Division guardar(Division division) {
        division.setHabilitada(true);
        return divisionRepository.save(division);
    }

    public Division actualizar(Long id, Division datos) {
        Optional<Division> optional = divisionRepository.findById(id);
        if (optional.isPresent()) {
            Division division = optional.get();
            division.setNombre(datos.getNombre());
            return divisionRepository.save(division);
        }
        return null;
    }

    public boolean cambiarEstado(Long id) {
        Optional<Division> optional = divisionRepository.findById(id);
        if (optional.isPresent()) {
            Division division = optional.get();
            division.setHabilitada(!division.getHabilitada());
            divisionRepository.save(division);
            return true;
        }
        return false;
    }
}