package com.pedidos.sistema_pedidos.domain.model.envio;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "envios_estandar")
@DiscriminatorValue("ESTANDAR")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class EnvioEstandar extends Envio {

    @Column(name = "plazo_entrega_dias", nullable = false)
    private Integer plazoEntregaDias;

    @Override
    public double calcularCosto() {
        return tarifaBase * calcularFactorVolumen();
    }

    @Override
    public String getTipo() { return "ESTANDAR"; }

    @Override
    protected int getPlazoEntregaDias() {
        return plazoEntregaDias != null ? plazoEntregaDias : 5;
    }
}