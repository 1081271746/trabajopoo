package com.pedidos.sistema_pedidos.service;

import com.pedidos.sistema_pedidos.domain.model.Cliente;
import com.pedidos.sistema_pedidos.domain.model.CodigoPromocional;
import com.pedidos.sistema_pedidos.domain.model.Pedido;
import com.pedidos.sistema_pedidos.exception.ReglaNegocioException;
import com.pedidos.sistema_pedidos.repository.CodigoPromocionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServicioDescuento {

    private final CodigoPromocionalRepository codigoRepo;

    public double porVolumen(int cantidadTotal, double precioUnitario) {
        if (cantidadTotal > 50) return precioUnitario * 0.15;
        if (cantidadTotal > 20) return precioUnitario * 0.10;
        if (cantidadTotal > 10) return precioUnitario * 0.05;
        return 0.0;
    }

    public double porCliente(Cliente cliente, double total) {
        int historial = cliente.getPedidos().size();
        if (historial > 20) return total * 0.07;
        if (historial > 5)  return total * 0.03;
        return 0.0;
    }

    @Transactional
    public double porCodigoPromo(CodigoPromocional codigo, double total) {
        if (!codigo.esValido()) {
            throw new ReglaNegocioException(
                    "El código '" + codigo.getCodigo() + "' no es válido o está vencido");
        }
        double descuento = total * (codigo.getPorcentaje() / 100.0);
        codigo.registrarUso();
        codigoRepo.save(codigo);
        return descuento;
    }

    public double calcularTotal(Pedido pedido) {
        double total = pedido.calcularTotal();
        int cantidadTotal = pedido.getDetalles().stream()
                .mapToInt(d -> d.getCantidad()).sum();
        double descuentoVolumen = porVolumen(cantidadTotal, total);
        double descuentoCliente = porCliente(pedido.getCliente(), total);
        return Math.max(0, total - descuentoVolumen - descuentoCliente);
    }
}