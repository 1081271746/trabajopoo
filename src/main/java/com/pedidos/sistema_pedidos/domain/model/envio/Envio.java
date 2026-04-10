package com.pedidos.sistema_pedidos.domain.model.envio;

import com.pedidos.sistema_pedidos.domain.model.Direccion;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "envios")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_envio", discriminatorType = DiscriminatorType.STRING)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public abstract class Envio implements IEntregable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double peso;

    private Double volumen;

    @Column(name = "fecha_envio")
    private LocalDate fechaEnvio;

    @Column(name = "fecha_entrega_estimada")
    private LocalDate fechaEntregaEstimada;

    @Column(name = "tarifa_base", nullable = false)
    protected Double tarifaBase;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "calle",        column = @Column(name = "origen_calle")),
            @AttributeOverride(name = "ciudad",       column = @Column(name = "origen_ciudad")),
            @AttributeOverride(name = "departamento", column = @Column(name = "origen_departamento")),
            @AttributeOverride(name = "codigoPostal", column = @Column(name = "origen_codigo_postal")),
            @AttributeOverride(name = "pais",         column = @Column(name = "origen_pais"))
    })
    private Direccion origen;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "calle",        column = @Column(name = "destino_calle")),
            @AttributeOverride(name = "ciudad",       column = @Column(name = "destino_ciudad")),
            @AttributeOverride(name = "departamento", column = @Column(name = "destino_departamento")),
            @AttributeOverride(name = "codigoPostal", column = @Column(name = "destino_codigo_postal")),
            @AttributeOverride(name = "pais",         column = @Column(name = "destino_pais"))
    })
    private Direccion destino;

    @Override
    public LocalDate estimarFechaEntrega() {
        if (fechaEntregaEstimada != null) return fechaEntregaEstimada;
        return LocalDate.now().plusDays(getPlazoEntregaDias());
    }

    @Override
    public abstract double calcularCosto();

    @Override
    public abstract String getTipo();

    protected abstract int getPlazoEntregaDias();

    protected double calcularFactorVolumen() {
        if (volumen == null || peso == null || peso == 0) return 1.0;
        double pesoVolumetrico = volumen / 5000.0;
        return Math.max(peso, pesoVolumetrico) / peso;
    }
}