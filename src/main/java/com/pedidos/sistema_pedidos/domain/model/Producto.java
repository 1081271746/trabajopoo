package com.pedidos.sistema_pedidos.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "productos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @NotNull
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Column(nullable = false)
    private Double precio;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;

    @Min(value = 0)
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    public boolean verificarDisponibilidad(int cantidad) {
        return this.stock >= cantidad;
    }

    public boolean disminuirStock(int cantidad) {
        if (!verificarDisponibilidad(cantidad)) return false;
        this.stock -= cantidad;
        return true;
    }

    public void aumentarStock(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser positiva");
        this.stock += cantidad;
    }

    public void actualizarPrecio(double nuevoPrecio) {
        if (nuevoPrecio <= 0) throw new IllegalArgumentException("El precio debe ser mayor a 0");
        this.precio = nuevoPrecio;
    }

    public boolean estaEnStockMinimo() {
        return this.stock <= this.stockMinimo;
    }
}