package mx.edu.utez.Integradora5A_SIGRAD.dto;

public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String matricula;
    private String telefono;
    private String carrera;
    private String emailInstitucional;
    private String contrasena;
    private String rol;
    private Boolean estado;
    private Boolean validado = false;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Long id, String nombre, String matricula, String telefono, String carrera,
                        String emailInstitucional, String contrasena, String rol, Boolean estado, Boolean validado) {
        this.id = id;
        this.nombre = nombre;
        this.matricula = matricula;
        this.telefono = telefono;
        this.carrera = carrera;
        this.emailInstitucional = emailInstitucional;
        this.contrasena = contrasena;
        this.rol = rol;
        this.estado = estado;
        this.validado = validado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public String getEmailInstitucional() {
        return emailInstitucional;
    }

    public void setEmailInstitucional(String emailInstitucional) {
        this.emailInstitucional = emailInstitucional;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }
}

