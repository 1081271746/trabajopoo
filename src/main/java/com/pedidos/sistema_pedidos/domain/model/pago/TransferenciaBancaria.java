package com.pedidos.sistema_pedidos.domain.model.pago;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "transferencias_bancarias")
@DiscriminatorValue("TRANSFERENCIA")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class TransferenciaBancaria extends MetodoPago {

    @Column(name = "numero_cuenta", nullable = false)
    private String numeroCuenta;

    @Column(nullable = false)
    private String banco;

    @Override
    public boolean autorizar(double monto) {
        return numeroCuenta != null && !numeroCuenta.isBlank() && monto > 0;
    }

    public String generarReferencia() {
        return "REF-" + banco.toUpperCase().substring(0, 3)
                + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}