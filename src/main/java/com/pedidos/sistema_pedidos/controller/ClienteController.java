package com.pedidos.sistema_pedidos.controller;

import com.pedidos.sistema_pedidos.domain.model.Cliente;
import com.pedidos.sistema_pedidos.domain.model.Direccion;
import com.pedidos.sistema_pedidos.dto.cliente.ClienteDTO;
import com.pedidos.sistema_pedidos.mapper.DomainMapper;
import com.pedidos.sistema_pedidos.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;
    private final DomainMapper   mapper;

    @PostMapping
    public ResponseEntity<ClienteDTO.Response> registrar(
            @Valid @RequestBody ClienteDTO.Request request) {
        Cliente cliente = clienteService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toClienteResponse(cliente));
    }

    @GetMapping
    public ResponseEntity<List<ClienteDTO.Response>> listar() {
        List<ClienteDTO.Response> lista = clienteService.listarTodos()
                .stream().map(mapper::toClienteResponse).toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO.Response> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(
                mapper.toClienteResponse(clienteService.buscarPorId(id)));
    }

    @PostMapping("/{id}/direcciones")
    public ResponseEntity<ClienteDTO.Response> agregarDireccion(
            @PathVariable Long id,
            @Valid @RequestBody Direccion direccion) {
        Cliente cliente = clienteService.agregarDireccion(id, direccion);
        return ResponseEntity.ok(mapper.toClienteResponse(cliente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}