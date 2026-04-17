package mx.edu.utez.Integradora5A_SIGRAD.model;

import jakarta.persistence.*;

@Entity
@Table(name = "divisiones")
public class Division {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Boolean habilitada = true;

    public Division() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Boolean getHabilitada() { return habilitada; }
    public void setHabilitada(Boolean habilitada) { this.habilitada = habilitada; }
}