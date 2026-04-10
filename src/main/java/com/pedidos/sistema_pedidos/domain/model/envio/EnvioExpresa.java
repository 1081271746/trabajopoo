package com.pedidos.sistema_pedidos.domain.model.envio;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "envios_expresa")
@DiscriminatorValue("EXPRESA")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class EnvioExpresa extends Envio {

    @Column(name = "tarifa_adicional", nullable = false)
    private Double tarifaAdicional;

    @Column(name = "porcentaje_urgencia", nullable = false)
    private Double porcentajeUrgencia;

    @Column(name = "plazo_entrega_dias", nullable = false)
    private Integer plazoEntregaDias;

    @Override
    public double calcularCosto() {
        return (tarifaBase + calcularCostoUrgencia()) * calcularFactorVolumen();
    }

    public double calcularCostoUrgencia() {
        return tarifaAdicional * (1 + porcentajeUrgencia / 100.0);
    }

    @Override
    public String getTipo() { return "EXPRESA"; }

    @Override
    protected int getPlazoEntregaDias() {
        return plazoEntregaDias != null ? plazoEntregaDias : 2;
    }
}