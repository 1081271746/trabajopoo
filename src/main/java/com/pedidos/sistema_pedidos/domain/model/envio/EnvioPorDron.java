package com.pedidos.sistema_pedidos.domain.model.envio;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "envios_dron")
@DiscriminatorValue("DRON")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class EnvioPorDron extends Envio {

    @Column(name = "tarifa_por_km", nullable = false)
    private Double tarifaPorKm;

    @Column(name = "distancia_maxima", nullable = false)
    private Double distanciaMaxima;

    @Column(name = "capacidad_maxima_peso", nullable = false)
    private Double capacidadMaximaPeso;

    @Column(name = "distancia_km", nullable = false)
    private Double distanciaKm;

    @Override
    public double calcularCosto() {
        if (!validarDistancia(distanciaKm))
            throw new IllegalStateException("Distancia excede el alcance del dron");
        if (!validarPeso(getPeso()))
            throw new IllegalStateException("Peso excede la capacidad del dron");
        return distanciaKm * tarifaPorKm;
    }

    public boolean validarDistancia(double distancia) {
        return distancia > 0 && distancia <= distanciaMaxima;
    }

    public boolean validarPeso(double peso) {
        return peso > 0 && peso <= capacidadMaximaPeso;
    }

    public double calcularAutonomia(double distancia) {
        return distancia * 2 * 1.2;
    }

    @Override
    public String getTipo() { return "DRON"; }

    @Override
    protected int getPlazoEntregaDias() { return 1; }
}