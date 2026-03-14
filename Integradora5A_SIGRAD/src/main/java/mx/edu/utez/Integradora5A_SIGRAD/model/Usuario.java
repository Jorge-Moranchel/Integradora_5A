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

    @Column(name = "EMAILINSTITUCIONAL", unique = true)
    private String emailInstitucional;

    @Column(name = "CONTRASENA")
    private String contrasena;

    @Column(name = "ROL")
    private String rol;

    // Constructor vacío (Obligatorio para JPA)
    public Usuario() {}

    // GETTERS Y SETTERS MANUALES (Esto elimina el error del controlador)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmailInstitucional() { return emailInstitucional; }
    public void setEmailInstitucional(String emailInstitucional) { this.emailInstitucional = emailInstitucional; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}