package mx.edu.utez.Integradora5A_SIGRAD.dto;

public class BloqueoDTO {
    private String motivoBloqueo;
    private String fechaInicioBloqueo;
    private String fechaFinBloqueo;

    public BloqueoDTO() {}

    public String getMotivoBloqueo() { return motivoBloqueo; }
    public void setMotivoBloqueo(String motivoBloqueo) { this.motivoBloqueo = motivoBloqueo; }

    public String getFechaInicioBloqueo() { return fechaInicioBloqueo; }
    public void setFechaInicioBloqueo(String fechaInicioBloqueo) { this.fechaInicioBloqueo = fechaInicioBloqueo; }

    public String getFechaFinBloqueo() { return fechaFinBloqueo; }
    public void setFechaFinBloqueo(String fechaFinBloqueo) { this.fechaFinBloqueo = fechaFinBloqueo; }
}