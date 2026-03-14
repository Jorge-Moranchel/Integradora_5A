package mx.edu.utez.Integradora5A_SIGRAD.service;

import mx.edu.utez.Integradora5A_SIGRAD.model.Rol;
import mx.edu.utez.Integradora5A_SIGRAD.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolService {
    @Autowired
    private RolRepository rolRepository;

    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    public void cambiarEstado(Long id) {
        Rol rol = rolRepository.findById(id).orElseThrow();
        rol.setActivo(!rol.getActivo());
        rolRepository.save(rol);
    }
}