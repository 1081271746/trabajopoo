package com.pedidos.sistema_pedidos.service;

import com.pedidos.sistema_pedidos.domain.enums.EstadoPago;
import com.pedidos.sistema_pedidos.domain.enums.EstadoPedido;
import com.pedidos.sistema_pedidos.domain.model.*;
import com.pedidos.sistema_pedidos.domain.model.envio.Envio;
import com.pedidos.sistema_pedidos.domain.model.pago.MetodoPago;
import com.pedidos.sistema_pedidos.dto.pedido.PedidoDTO;
import com.pedidos.sistema_pedidos.exception.RecursoNoEncontradoException;
import com.pedidos.sistema_pedidos.exception.ReglaNegocioException;
import com.pedidos.sistema_pedidos.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository     pedidoRepository;
    private final ClienteRepository    clienteRepository;
    private final ProductoRepository   productoRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final EnvioRepository      envioRepository;

    private final ServicioInventario   servicioInventario;
    private final ServicioNotificacion servicioNotificacion;
    private final ServicioDescuento    servicioDescuento;

    @Transactional
    public Pedido crear(PedidoDTO.Request dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente", dto.getClienteId()));

        Pedido pedido = Pedido.builder().cliente(cliente).build();

        for (PedidoDTO.DetalleRequest detalleDto : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDto.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Producto", detalleDto.getProductoId()));
            servicioInventario.verificarStock(producto, detalleDto.getCantidad());
            pedido.agregarDetalle(producto, detalleDto.getCantidad());
        }

        Pedido guardado = pedidoRepository.save(pedido);

        guardado.getDetalles().forEach(detalle ->
                servicioInventario.reservarStock(
                        detalle.getProducto(), detalle.getCantidad()));

        log.info("Pedido #{} creado para '{}'",
                guardado.getId(), cliente.getNombre());
        return guardado;
    }

    @Transactional(readOnly = true)
    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido", id));
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new RecursoNoEncontradoException("Cliente", clienteId);
        }
        return pedidoRepository.findByClienteId(clienteId);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    @Transactional
    public Pedido confirmar(Long pedidoId) {
        Pedido pedido = buscarPorId(pedidoId);

        int cantidadTotal = pedido.getDetalles().stream()
                .mapToInt(DetallePedido::getCantidad).sum();

        pedido.getDetalles().forEach(detalle -> {
            double descuento = servicioDescuento.porVolumen(
                    cantidadTotal, detalle.getPrecioUnitario());
            double porcentaje = (descuento / detalle.getPrecioUnitario()) * 100.0;
            if (porcentaje > 0) detalle.aplicarDescuento(porcentaje);
        });

        pedido.confirmarPedido();
        Pedido confirmado = pedidoRepository.save(pedido);
        servicioNotificacion.notificarEstado(confirmado);
        log.info("Pedido #{} confirmado. Total: ${}",
                confirmado.getId(), confirmado.calcularTotal());
        return confirmado;
    }

    @Transactional
    public Pago procesarPago(Long pedidoId, Long metodoPagoId) {
        Pedido pedido = buscarPorId(pedidoId);
        MetodoPago metodoPago = metodoPagoRepository.findById(metodoPagoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "MetodoPago", metodoPagoId));

        Pago pago = pedido.generarPago(metodoPago);
        boolean exitoso = pago.procesar();

        pedidoRepository.save(pedido);
        servicioNotificacion.notificarPago(pago);

        if (exitoso) {
            pedido.cambiarEstado(EstadoPedido.EN_PREPARACION);
            pedidoRepository.save(pedido);
            servicioNotificacion.notificarEstado(pedido);
            log.info("Pago completado para pedido #{}. Txn: {}",
                    pedidoId, pago.getNumeroTransaccion());
        } else {
            log.warn("Pago fallido para pedido #{}. Liberando stock...", pedidoId);
            pedido.getDetalles().forEach(d ->
                    servicioInventario.liberarStock(
                            d.getProducto(), d.getCantidad()));
            pedido.cancelarPedido();
            pedidoRepository.save(pedido);
            throw new ReglaNegocioException(
                    "El pago fue rechazado por el método de pago seleccionado");
        }
        return pago;
    }

    @Transactional
    public Pedido cambiarEstado(Long pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = buscarPorId(pedidoId);
        pedido.cambiarEstado(nuevoEstado);
        Pedido actualizado = pedidoRepository.save(pedido);
        servicioNotificacion.notificarEstado(actualizado);
        return actualizado;
    }

    @Transactional
    public Pedido cancelar(Long pedidoId) {
        Pedido pedido = buscarPorId(pedidoId);

        pedido.getDetalles().forEach(d ->
                servicioInventario.liberarStock(d.getProducto(), d.getCantidad()));

        if (pedido.getPago() != null
                && pedido.getPago().getEstado() == EstadoPago.COMPLETADO) {
            pedido.getPago().reembolsar();
            pedido.cambiarEstado(EstadoPedido.REEMBOLSADO);
        } else {
            pedido.cancelarPedido();
        }

        Pedido cancelado = pedidoRepository.save(pedido);
        servicioNotificacion.notificarEstado(cancelado);
        log.info("Pedido #{} cancelado", pedidoId);
        return cancelado;
    }

    @Transactional
    public Pedido asignarEnvio(Long pedidoId, Long envioId) {
        Pedido pedido = buscarPorId(pedidoId);
        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Envio", envioId));
        pedido.asignarEnvio(envio);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido aplicarCodigoPromocional(Long pedidoId, CodigoPromocional codigo) {
        Pedido pedido = buscarPorId(pedidoId);
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new ReglaNegocioException(
                    "Solo se puede aplicar un código a pedidos en estado PENDIENTE");
        }
        double totalActual = pedido.calcularTotal();
        double descuento   = servicioDescuento.porCodigoPromo(codigo, totalActual);
        double porcentaje  = (descuento / totalActual) * 100.0;
        pedido.getDetalles().forEach(d -> d.aplicarDescuento(porcentaje));
        log.info("Código '{}' aplicado al pedido #{}",
                codigo.getCodigo(), pedidoId);
        return pedidoRepository.save(pedido);
    }
}