package com.pedidos.sistema_pedidos.domain.enums;

public enum EstadoPedido {
    PENDIENTE,
    CONFIRMADO,
    EN_PREPARACION,
    ENVIADO,
    ENTREGADO,
    CANCELADO,
    REEMBOLSADO;

    public boolean puedeTransicionarA(EstadoPedido siguiente) {
        return switch (this) {
            case PENDIENTE      -> siguiente == CONFIRMADO || siguiente == CANCELADO;
            case CONFIRMADO     -> siguiente == EN_PREPARACION || siguiente == CANCELADO;
            case EN_PREPARACION -> siguiente == ENVIADO || siguiente == CANCELADO;
            case ENVIADO        -> siguiente == ENTREGADO;
            case ENTREGADO      -> siguiente == REEMBOLSADO;
            case CANCELADO, REEMBOLSADO -> false;
        };
    }
}