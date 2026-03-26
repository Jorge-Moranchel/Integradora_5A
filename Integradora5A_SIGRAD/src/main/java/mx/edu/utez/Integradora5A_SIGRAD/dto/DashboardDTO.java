package mx.edu.utez.Integradora5A_SIGRAD.dto;

import java.util.List;
import java.util.Map;

public class DashboardDTO {
    private long reservasActivas;
    private long reservasHoy;
    private long usuariosRegistrados;
    private int tasaOcupacion; // Porcentaje
    private Map<String, Long> reservasPorArea;
    private Map<String, Long> reservasPorMes;

    public long getReservasActivas() { return reservasActivas; }
    public void setReservasActivas(long reservasActivas) { this.reservasActivas = reservasActivas; }

    public long getReservasHoy() { return reservasHoy; }
    public void setReservasHoy(long reservasHoy) { this.reservasHoy = reservasHoy; }

    public long getUsuariosRegistrados() { return usuariosRegistrados; }
    public void setUsuariosRegistrados(long usuariosRegistrados) { this.usuariosRegistrados = usuariosRegistrados; }

    public int getTasaOcupacion() { return tasaOcupacion; }
    public void setTasaOcupacion(int tasaOcupacion) { this.tasaOcupacion = tasaOcupacion; }

    public Map<String, Long> getReservasPorArea() { return reservasPorArea; }
    public void setReservasPorArea(Map<String, Long> reservasPorArea) { this.reservasPorArea = reservasPorArea; }

    public Map<String, Long> getReservasPorMes() { return reservasPorMes; }
    public void setReservasPorMes(Map<String, Long> reservasPorMes) { this.reservasPorMes = reservasPorMes; }
}