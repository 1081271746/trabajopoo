package com.pedidos.sistema_pedidos.mapper;

import com.pedidos.sistema_pedidos.domain.model.*;
import com.pedidos.sistema_pedidos.domain.model.pago.MetodoPago;
import com.pedidos.sistema_pedidos.dto.cliente.ClienteDTO;
import com.pedidos.sistema_pedidos.dto.pago.PagoDTO;
import com.pedidos.sistema_pedidos.dto.pedido.PedidoDTO;
import com.pedidos.sistema_pedidos.dto.producto.ProductoDTO;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DomainMapper {

    public ClienteDTO.Response toClienteResponse(Cliente cliente) {
        ClienteDTO.Response dto = new ClienteDTO.Response();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setEmail(cliente.getEmail());
        dto.setTelefono(cliente.getTelefono());
        dto.setFechaRegistro(
                cliente.getFechaRegistro() != null
                        ? cliente.getFechaRegistro().toString() : null);
        return dto;
    }

    public ProductoDTO.Response toProductoResponse(Producto producto) {
        ProductoDTO.Response dto = new ProductoDTO.Response();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setStockMinimo(producto.getStockMinimo());
        dto.setCategoriaNombre(
                producto.getCategoria() != null
                        ? producto.getCategoria().getNombre() : null);
        dto.setEnStockMinimo(producto.estaEnStockMinimo());
        return dto;
    }

    public PedidoDTO.DetalleResponse toDetalleResponse(DetallePedido detalle) {
        PedidoDTO.DetalleResponse dto = new PedidoDTO.DetalleResponse();
        dto.setId(detalle.getId());
        dto.setProductoNombre(
                detalle.getProducto() != null
                        ? detalle.getProducto().getNombre() : null);
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setDescuento(detalle.getDescuento());
        dto.setSubtotal(detalle.calcularSubtotal());
        return dto;
    }

    public PedidoDTO.Response toPedidoResponse(Pedido pedido) {
        PedidoDTO.Response dto = new PedidoDTO.Response();
        dto.setId(pedido.getId());
        dto.setEstado(pedido.getEstado().name());
        dto.setFecha(pedido.getFecha() != null ? pedido.getFecha().toString() : null);
        dto.setTotal(pedido.calcularTotal());
        dto.setClienteNombre(
                pedido.getCliente() != null
                        ? pedido.getCliente().getNombre() : null);
        List<PedidoDTO.DetalleResponse> detalles = pedido.getDetalles()
                .stream().map(this::toDetalleResponse).toList();
        dto.setDetalles(detalles);
        return dto;
    }

    public PagoDTO.Response toPagoResponse(Pago pago) {
        PagoDTO.Response dto = new PagoDTO.Response();
        dto.setId(pago.getId());
        dto.setMonto(pago.getMonto());
        dto.setEstado(pago.getEstado().name());
        dto.setNumeroTransaccion(pago.getNumeroTransaccion());
        dto.setFechaConfirmacion(
                pago.getFechaConfirmacion() != null
                        ? pago.getFechaConfirmacion().toString() : null);
        MetodoPago metodo = pago.getMetodoPago();
        dto.setMetodoPago(metodo != null ? metodo.getNombre() : null);
        try {
            dto.setComprobante(pago.generarComprobante());
        } catch (IllegalStateException e) {
            dto.setComprobante(null);
        }
        return dto;
    }
}