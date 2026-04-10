package com.pedidos.sistema_pedidos.dto.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class ClienteDTO {

    @Data
    public static class Request {
        @NotBlank(message = "El nombre es obligatorio")
        private String nombre;

        @NotBlank @Email(message = "Email inválido")
        private String email;

        private String telefono;

        @NotBlank(message = "La contraseña es obligatoria")
        private String password;
    }

    @Data
    public static class Response {
        private Long id;
        private String nombre;
        private String email;
        private String telefono;
        private String fechaRegistro;
    }
}
