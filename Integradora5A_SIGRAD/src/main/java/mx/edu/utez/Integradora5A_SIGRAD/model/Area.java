package mx.edu.utez.Integradora5A_SIGRAD.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "areas_deportivas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String tipoDeporte;
    private String horario;
    private String estado; // Disponible, Ocupada, Mantenimiento

    @Lob
    @Column(name = "imagen", columnDefinition = "CLOB")
    private String imagen; // Guardaremos la imagen en formato Base64
}