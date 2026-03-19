package mx.edu.utez.Integradora5A_SIGRAD.model;

import jakarta.persistence.*;

@Entity
@Table(name = "RESERVA")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la tabla USUARIOS (Llave foránea)
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // Relación con la tabla AREAS_DEPORTIVAS (Llave foránea)
    @ManyToOne
    @JoinColumn(name = "id_area", nullable = false)
    private Area area;

    @Column(nullable = false, length = 20)
    private String fecha;

    @Column(name = "hora_inicio", nullable = false, length = 10)
    private String horaInicio;

    @Column(name = "hora_fin", nullable = false, length = 10)
    private String horaFin;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false, length = 20)
    private String estado = "CONFIRMADA";

    // Constructor vacío obligatorio para Spring Boot
    public Reserva() {}

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Area getArea() { return area; }
    public void setArea(Area area) { this.area = area; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}