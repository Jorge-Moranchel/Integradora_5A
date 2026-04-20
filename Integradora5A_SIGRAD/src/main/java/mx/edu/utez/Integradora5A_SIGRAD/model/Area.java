package mx.edu.utez.Integradora5A_SIGRAD.model;

import jakarta.persistence.*;

@Entity
@Table(name = "AREAS_DEPORTIVAS")
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;
    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String ubicacion;

    @Column(nullable = false)
    private String horaApertura;

    @Column(nullable = false)
    private String horaCierre;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String imagen;

    private String estado = "disponible";

    private String motivoBloqueo;
    private String fechaInicioBloqueo;
    private String fechaFinBloqueo;

    // Constructor vacío (Obligatorio para JPA)
    public Area() {
    }

    // ✅ NUEVO: Constructor optimizado SIN la imagen para que la BD no la descargue
    public Area(Long id, String nombre, String tipo, String ubicacion, String horaApertura, String horaCierre, String estado, String motivoBloqueo, String fechaInicioBloqueo, String fechaFinBloqueo) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.ubicacion = ubicacion;
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
        this.estado = estado;
        this.motivoBloqueo = motivoBloqueo;
        this.fechaInicioBloqueo = fechaInicioBloqueo;
        this.fechaFinBloqueo = fechaFinBloqueo;
        this.imagen = null; // Forzamos a que sea nulo para ahorrar muchísima memoria y tiempo
    }

    // Getters y Setters
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getHoraApertura() {
        return horaApertura;
    }

    public void setHoraApertura(String horaApertura) {
        this.horaApertura = horaApertura;
    }

    public String getHoraCierre() {
        return horaCierre;
    }

    public void setHoraCierre(String horaCierre) {
        this.horaCierre = horaCierre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMotivoBloqueo() {
        return motivoBloqueo;
    }

    public void setMotivoBloqueo(String motivoBloqueo) {
        this.motivoBloqueo = motivoBloqueo;
    }

    public String getFechaInicioBloqueo() {
        return fechaInicioBloqueo;
    }

    public void setFechaInicioBloqueo(String fechaInicioBloqueo) {
        this.fechaInicioBloqueo = fechaInicioBloqueo;
    }

    public String getFechaFinBloqueo() {
        return fechaFinBloqueo;
    }

    public void setFechaFinBloqueo(String fechaFinBloqueo) {
        this.fechaFinBloqueo = fechaFinBloqueo;
    }
}