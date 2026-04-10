package com.pedidos.sistema_pedidos.controller;

import com.pedidos.sistema_pedidos.domain.enums.EstadoPedido;
import com.pedidos.sistema_pedidos.domain.model.CodigoPromocional;
import com.pedidos.sistema_pedidos.domain.model.Pago;
import com.pedidos.sistema_pedidos.domain.model.Pedido;
import com.pedidos.sistema_pedidos.dto.pago.PagoDTO;
import com.pedidos.sistema_pedidos.dto.pedido.PedidoDTO;
import com.pedidos.sistema_pedidos.exception.RecursoNoEncontradoException;
import com.pedidos.sistema_pedidos.mapper.DomainMapper;
import com.pedidos.sistema_pedidos.repository.CodigoPromocionalRepository;
import com.pedidos.sistema_pedidos.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService               pedidoService;
    private final CodigoPromocionalRepository codigoRepo;
    private final DomainMapper                mapper;

    @PostMapping
    public ResponseEntity<PedidoDTO.Response> crear(
            @Valid @RequestBody PedidoDTO.Request request) {
        Pedido pedido = pedidoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toPedidoResponse(pedido));
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO.Response>> listar() {
        List<PedidoDTO.Response> lista = pedidoService.listarTodos()
                .stream().map(mapper::toPedidoResponse).toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO.Response> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(
                mapper.toPedidoResponse(pedidoService.buscarPorId(id)));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoDTO.Response>> listarPorCliente(
            @PathVariable Long clienteId) {
        List<PedidoDTO.Response> lista = pedidoService
                .listarPorCliente(clienteId)
                .stream().map(mapper::toPedidoResponse).toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoDTO.Response>> listarPorEstado(
            @PathVariable String estado) {
        EstadoPedido estadoEnum = EstadoPedido.valueOf(estado.toUpperCase());
        List<PedidoDTO.Response> lista = pedidoService
                .listarPorEstado(estadoEnum)
                .stream().map(mapper::toPedidoResponse).toList();
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<PedidoDTO.Response> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(
                mapper.toPedidoResponse(pedidoService.confirmar(id)));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoDTO.Response> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody PedidoDTO.CambioEstadoRequest request) {
        EstadoPedido nuevoEstado = EstadoPedido.valueOf(
                request.getNuevoEstado().toUpperCase());
        return ResponseEntity.ok(
                mapper.toPedidoResponse(
                        pedidoService.cambiarEstado(id, nuevoEstado)));
    }

    @PostMapping("/{id}/pago")
    public ResponseEntity<PagoDTO.Response> procesarPago(
            @PathVariable Long id,
            @Valid @RequestBody PagoDTO.Request request) {
        Pago pago = pedidoService.procesarPago(id, request.getMetodoPagoId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toPagoResponse(pago));
    }

    @PostMapping("/{id}/envio/{envioId}")
    public ResponseEntity<PedidoDTO.Response> asignarEnvio(
            @PathVariable Long id,
            @PathVariable Long envioId) {
        return ResponseEntity.ok(
                mapper.toPedidoResponse(
                        pedidoService.asignarEnvio(id, envioId)));
    }

    @PostMapping("/{id}/descuento")
    public ResponseEntity<PedidoDTO.Response> aplicarDescuento(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String codigoStr = body.get("codigo");
        CodigoPromocional codigo = codigoRepo.findByCodigo(codigoStr)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Código promocional '" + codigoStr + "' no encontrado"));
        return ResponseEntity.ok(
                mapper.toPedidoResponse(
                        pedidoService.aplicarCodigoPromocional(id, codigo)));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<PedidoDTO.Response> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(
                mapper.toPedidoResponse(pedidoService.cancelar(id)));
    }
}