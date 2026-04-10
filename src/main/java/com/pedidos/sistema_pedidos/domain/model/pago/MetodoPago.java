package com.pedidos.sistema_pedidos.domain.model.pago;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "metodos_pago")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_metodo", discriminatorType = DiscriminatorType.STRING)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public abstract class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    @DecimalMin("0.0")
    @Column(nullable = false)
    private Double comision;

    @Column(name = "requiere_autorizacion", nullable = false)
    private Boolean requiereAutorizacion;

    public abstract boolean autorizar(double monto);

    public double calcularComision(double monto) {
        return monto * (comision / 100.0);
    }

    public boolean validarMetodo() {
        return nombre != null && !nombre.isBlank() && comision >= 0;
    }
}