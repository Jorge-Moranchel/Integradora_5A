package mx.edu.utez.Integradora5A_SIGRAD.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ADMIN")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "MATRICULA") // Campo nuevo para tu Modal
    private String matricula;

    @Column(name = "TELEFONO") // Campo nuevo para tu Modal
    private String telefono;

    @Column(name = "CARRERA") // Campo nuevo para tu Modal
    private String carrera;

    @Column(name = "EMAILINSTITUCIONAL", unique = true)
    private String emailInstitucional;

    @Column(name = "CONTRASENA")
    private String contrasena;

    @Column(name = "ROL")
    private String rol;

    @Column(name = "ESTADO")
    private Boolean estado = true;

    private Boolean validado = false;

    @Column(name = "CODIGO_VERIFICACION")
    private String codigoVerificacion;

    public Usuario() {}

    // --- GETTERS Y SETTERS MANUALES ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }

    public String getEmailInstitucional() { return emailInstitucional; }
    public void setEmailInstitucional(String emailInstitucional) { this.emailInstitucional = emailInstitucional; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public Boolean getValidado() { return validado; }
    public void setValidado(Boolean validado) { this.validado = validado; }

    public String getCodigoVerificacion() {
        return codigoVerificacion;
    }

    public void setCodigoVerificacion(String codigoVerificacion) {
        this.codigoVerificacion = codigoVerificacion;
    }
}