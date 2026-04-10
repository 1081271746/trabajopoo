package com.pedidos.sistema_pedidos.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "codigos_promocionales")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CodigoPromocional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String codigo;

    @DecimalMin("0.0") @DecimalMax("100.0")
    @Column(name = "porcentaje_descuento", nullable = false)
    private Double porcentajeDescuento;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "usos_maximos", nullable = false)
    private Integer usosMaximos;

    @Builder.Default
    @Column(name = "usos_actuales", nullable = false)
    private Integer usosActuales = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    public boolean estaVigente() {
        return !LocalDate.now().isAfter(fechaVencimiento);
    }

    public boolean esValido() {
        return activo && estaVigente() && usosActuales < usosMaximos;
    }

    public void registrarUso() {
        if (!esValido()) {
            throw new IllegalStateException("El código '" + codigo + "' no es válido");
        }
        this.usosActuales++;
        if (this.usosActuales >= this.usosMaximos) {
            this.activo = false;
        }
    }

    public double getPorcentaje() {
        return porcentajeDescuento;
    }
}