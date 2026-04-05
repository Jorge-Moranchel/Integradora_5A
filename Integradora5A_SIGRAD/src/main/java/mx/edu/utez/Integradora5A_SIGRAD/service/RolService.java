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

    public Rol guardar(Rol rol) {
        return rolRepository.save(rol);
    }

    // 👇 NUEVO MÉTODO PARA EDITAR EL ROL 👇
    public Rol actualizar(Long id, Rol datosActualizados) {
        Rol rolExistente = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        rolExistente.setNombre(datosActualizados.getNombre());
        rolExistente.setDescripcion(datosActualizados.getDescripcion());
        // No actualizamos el estado aquí, de eso se encarga cambiarEstado()

        return rolRepository.save(rolExistente);
    }

    public void cambiarEstado(Long id) {
        Rol rol = rolRepository.findById(id).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        rol.setActivo(!rol.getActivo());
        rolRepository.save(rol);
    }
}