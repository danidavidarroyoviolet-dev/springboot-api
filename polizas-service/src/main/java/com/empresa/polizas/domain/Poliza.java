package com.empresa.polizas.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polizas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPoliza tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPoliza estado;

    @Column(nullable = false)
    private LocalDate fechaInicioVigencia;

    @Column(nullable = false)
    private LocalDate fechaFinVigencia;

    @Column(nullable = false)
    private Double canonMensual;

    @Column(nullable = false)
    private Double prima;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Riesgo> riesgos = new ArrayList<>();
    
    // Métodos helper para consistencia
    public void calcularPrima() {
        if (fechaInicioVigencia != null && fechaFinVigencia != null && canonMensual != null) {
            long meses = java.time.temporal.ChronoUnit.MONTHS.between(fechaInicioVigencia, fechaFinVigencia);
            // Asumiendo que vigencia incluye el mes final o reglas específicas, ajustamos si es necesario.
            // Para simplicidad: canon * meses. Si meses es 0, al menos 1?
             if (meses <= 0) meses = 1; 
            this.prima = canonMensual * meses;
        }
    }
}
