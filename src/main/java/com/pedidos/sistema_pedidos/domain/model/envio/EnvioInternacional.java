package com.pedidos.sistema_pedidos.domain.model.envio;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "envios_internacionales")
@DiscriminatorValue("INTERNACIONAL")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class EnvioInternacional extends Envio {

    @Column(name = "impuesto_aduana", nullable = false)
    private Double impuestoAduana;

    @Column(name = "pais_destino", nullable = false)
    private String paisDestino;

    @Column(name = "codigo_aduana")
    private String codigoAduana;

    @Column(name = "plazo_entrega_dias", nullable = false)
    private Integer plazoEntregaDias;

    @Override
    public double calcularCosto() {
        return (tarifaBase + calcularImpuestos()) * calcularFactorVolumen();
    }

    public double calcularImpuestos() {
        return tarifaBase * (impuestoAduana / 100.0);
    }

    @Override
    public String getTipo() { return "INTERNACIONAL"; }

    @Override
    protected int getPlazoEntregaDias() {
        return plazoEntregaDias != null ? plazoEntregaDias : 15;
    }
}