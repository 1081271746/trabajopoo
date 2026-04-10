package com.pedidos.sistema_pedidos.exception;

public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
    public RecursoNoEncontradoException(String entidad, Long id) {
        super(entidad + " con id " + id + " no encontrado");
    }
}