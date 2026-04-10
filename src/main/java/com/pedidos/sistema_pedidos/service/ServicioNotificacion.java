package com.pedidos.sistema_pedidos.service;

import com.pedidos.sistema_pedidos.domain.model.Pago;
import com.pedidos.sistema_pedidos.domain.model.Pedido;
import com.pedidos.sistema_pedidos.domain.model.Producto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ServicioNotificacion {

    @Value("${notificacion.proveedor:LOG}")
    private String proveedor;

    public boolean enviarEmail(String destino, String asunto, String mensaje) {
        log.info("[EMAIL][{}] Para: {} | Asunto: {} | Mensaje: {}",
                proveedor, destino, asunto, mensaje);
        return true;
    }

    public boolean enviarSMS(String telefono, String mensaje) {
        log.info("[SMS][{}] Para: {} | Mensaje: {}", proveedor, telefono, mensaje);
        return true;
    }

    public void notificarEstado(Pedido pedido) {
        String email  = pedido.getCliente().getEmail();
        String nombre = pedido.getCliente().getNombre();
        String asunto = "Tu pedido #" + pedido.getId() + " cambió de estado";
        String msg    = String.format("Hola %s, tu pedido #%d está en estado: %s",
                nombre, pedido.getId(), pedido.getEstado());
        enviarEmail(email, asunto, msg);
    }

    public void notificarPago(Pago pago) {
        log.info("[NOTIF] Pago #{} — estado: {} — monto: ${}",
                pago.getId(), pago.getEstado(), pago.getMonto());
    }

    public void notificarStockMinimo(Producto producto) {
        String msg = String.format(
                "[ALERTA] Producto '%s' tiene stock %d <= mínimo %d",
                producto.getNombre(), producto.getStock(), producto.getStockMinimo());
        log.warn(msg);
        enviarEmail("logistica@empresa.com", "Alerta stock mínimo", msg);
    }
}
