package com.pedidos.sistema_pedidos.domain.model;

import com.pedidos.sistema_pedidos.domain.enums.EstadoPago;
import com.pedidos.sistema_pedidos.domain.model.pago.MetodoPago;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estado;

    @Column(name = "numero_transaccion", unique = true)
    private String numeroTransaccion;

    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metodo_pago_id", nullable = false)
    private MetodoPago metodoPago;

    @PrePersist
    public void prePersist() {
        this.fecha  = LocalDateTime.now();
        this.estado = EstadoPago.PENDIENTE;
    }

    public boolean procesar() {
        if (this.estado != EstadoPago.PENDIENTE) {
            throw new IllegalStateException("El pago ya fue procesado");
        }
        this.estado = EstadoPago.PROCESANDO;
        boolean autorizado = metodoPago.autorizar(monto);
        if (autorizado) {
            this.estado              = EstadoPago.COMPLETADO;
            this.fechaConfirmacion   = LocalDateTime.now();
            this.numeroTransaccion   = "TXN-" + System.currentTimeMillis();
        } else {
            this.estado = EstadoPago.FALLIDO;
        }
        return autorizado;
    }

    public String generarComprobante() {
        if (this.estado != EstadoPago.COMPLETADO) {
            throw new IllegalStateException("Solo se puede generar comprobante de pagos completados");
        }
        return String.format("COMPROBANTE | Txn: %s | Monto: $%.2f | Método: %s | Fecha: %s",
                numeroTransaccion, monto, metodoPago.getNombre(), fechaConfirmacion);
    }

    public boolean reembolsar() {
        if (this.estado != EstadoPago.COMPLETADO) {
            throw new IllegalStateException("Solo se pueden reembolsar pagos completados");
        }
        this.estado = EstadoPago.REEMBOLSADO;
        return true;
    }
}