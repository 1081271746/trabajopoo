package com.pedidos.sistema_pedidos.dto.pago;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class PagoDTO {

    @Data
    public static class Request {
        @NotNull(message = "El método de pago es obligatorio")
        private Long metodoPagoId;
    }

    @Data
    public static class Response {
        private Long id;
        private Double monto;
        private String estado;
        private String numeroTransaccion;
        private String fechaConfirmacion;
        private String metodoPago;
        private String comprobante;
    }
}