package com.pedidos.sistema_pedidos.dto.producto;

import jakarta.validation.constraints.*;
import lombok.Data;

public class ProductoDTO {

    @Data
    public static class Request {
        @NotBlank(message = "El nombre es obligatorio")
        private String nombre;

        private String descripcion;

        @NotNull
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
        private Double precio;

        @NotNull @Min(0)
        private Integer stock;

        @NotNull @Min(0)
        private Integer stockMinimo;

        @NotNull(message = "La categoría es obligatoria")
        private Long categoriaId;
    }

    @Data
    public static class Response {
        private Long id;
        private String nombre;
        private String descripcion;
        private Double precio;
        private Integer stock;
        private Integer stockMinimo;
        private String categoriaNombre;
        private boolean enStockMinimo;
    }
}