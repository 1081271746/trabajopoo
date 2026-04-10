package com.pedidos.sistema_pedidos.dto.pedido;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

public class PedidoDTO {

    @Data
    public static class Request {
        @NotNull(message = "El cliente es obligatorio")
        private Long clienteId;

        @NotEmpty(message = "El pedido debe tener al menos un detalle")
        private List<DetalleRequest> detalles;
    }

    @Data
    public static class DetalleRequest {
        @NotNull private Long productoId;
        @Min(1)  private Integer cantidad;
    }

    @Data
    public static class Response {
        private Long id;
        private String estado;
        private String fecha;
        private Double total;
        private String clienteNombre;
        private List<DetalleResponse> detalles;
    }

    @Data
    public static class DetalleResponse {
        private Long id;
        private String productoNombre;
        private Integer cantidad;
        private Double precioUnitario;
        private Double descuento;
        private Double subtotal;
    }

    @Data
    public static class CambioEstadoRequest {
        @NotBlank
        private String nuevoEstado;
    }
}