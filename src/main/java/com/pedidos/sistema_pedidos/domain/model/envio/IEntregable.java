package com.pedidos.sistema_pedidos.domain.model.envio;

import java.time.LocalDate;

public interface IEntregable {
    double calcularCosto();
    String getTipo();
    LocalDate estimarFechaEntrega();
}