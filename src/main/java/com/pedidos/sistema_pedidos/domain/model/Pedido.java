package com.pedidos.sistema_pedidos.domain.model;

import com.pedidos.sistema_pedidos.domain.enums.EstadoPedido;
import com.pedidos.sistema_pedidos.domain.enums.EstadoPago;
import com.pedidos.sistema_pedidos.domain.model.envio.Envio;
import com.pedidos.sistema_pedidos.domain.model.pago.MetodoPago;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<DetallePedido> detalles = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "pago_id")
    private Pago pago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "envio_id")
    private Envio envio;

    @PrePersist
    public void prePersist() {
        this.fecha              = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.estado             = EstadoPedido.PENDIENTE;
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public double calcularTotal() {
        return detalles.stream()
                .mapToDouble(DetallePedido::calcularSubtotal)
                .sum();
    }

    public void cambiarEstado(EstadoPedido nuevoEstado) {
        if (!validarTransicion(nuevoEstado)) {
            throw new IllegalStateException(
                    String.format("Transición inválida: %s → %s", this.estado, nuevoEstado));
        }
        this.estado             = nuevoEstado;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public boolean validarTransicion(EstadoPedido nuevoEstado) {
        return this.estado.puedeTransicionarA(nuevoEstado);
    }

    public DetallePedido agregarDetalle(Producto producto, int cantidad) {
        if (!producto.verificarDisponibilidad(cantidad)) {
            throw new IllegalStateException(
                    "Stock insuficiente para: " + producto.getNombre());
        }
        DetallePedido detalle = DetallePedido.builder()
                .producto(producto)
                .cantidad(cantidad)
                .precioUnitario(producto.getPrecio())
                .descuento(0.0)
                .pedido(this)
                .build();
        detalle.calcularSubtotalAutomatico();
        detalles.add(detalle);
        return detalle;
    }

    public boolean eliminarDetalle(Long idDetalle) {
        return detalles.removeIf(d -> d.getId().equals(idDetalle));
    }

    public boolean confirmarPedido() {
        if (detalles.isEmpty()) {
            throw new IllegalStateException("No se puede confirmar un pedido sin detalles");
        }
        cambiarEstado(EstadoPedido.CONFIRMADO);
        return true;
    }

    public boolean cancelarPedido() {
        cambiarEstado(EstadoPedido.CANCELADO);
        return true;
    }

    public Pago generarPago(MetodoPago metodoPago) {
        if (this.estado != EstadoPedido.CONFIRMADO) {
            throw new IllegalStateException("Solo se puede pagar un pedido confirmado");
        }
        if (this.pago != null) {
            throw new IllegalStateException("Este pedido ya tiene un pago generado");
        }
        Pago nuevoPago = Pago.builder()
                .monto(calcularTotal())
                .metodoPago(metodoPago)
                .build();
        this.pago = nuevoPago;
        return nuevoPago;
    }

    public void asignarEnvio(Envio envio) {
        if (this.estado != EstadoPedido.CONFIRMADO
                && this.estado != EstadoPedido.EN_PREPARACION) {
            throw new IllegalStateException(
                    "Solo se puede asignar envío a pedidos confirmados o en preparación");
        }
        this.envio = envio;
    }

    public List<DetallePedido> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }
}