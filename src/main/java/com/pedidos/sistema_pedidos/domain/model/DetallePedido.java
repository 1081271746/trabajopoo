package com.pedidos.sistema_pedidos.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "detalles_pedido")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "La cantidad mínima es 1")
    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;

    @Builder.Default
    @Column(nullable = false)
    private Double descuento = 0.0;

    @Column(nullable = false)
    private Double subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @PrePersist
    @PreUpdate
    public void calcularSubtotalAutomatico() {
        this.subtotal = calcularSubtotal();
    }

    public double calcularSubtotal() {
        double base = precioUnitario * cantidad;
        return base - (base * (descuento / 100.0));
    }

    public void aplicarDescuento(double porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100");
        }
        this.descuento = porcentaje;
        this.subtotal  = calcularSubtotal();
    }
}