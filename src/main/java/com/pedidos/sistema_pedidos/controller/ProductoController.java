package com.pedidos.sistema_pedidos.controller;

import com.pedidos.sistema_pedidos.dto.producto.ProductoDTO;
import com.pedidos.sistema_pedidos.mapper.DomainMapper;
import com.pedidos.sistema_pedidos.service.ProductoService;
import com.pedidos.sistema_pedidos.service.ServicioInventario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService    productoService;
    private final ServicioInventario servicioInventario;
    private final DomainMapper       mapper;

    @PostMapping
    public ResponseEntity<ProductoDTO.Response> crear(
            @Valid @RequestBody ProductoDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toProductoResponse(productoService.crear(request)));
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO.Response>> listar() {
        List<ProductoDTO.Response> lista = productoService.listarTodos()
                .stream().map(mapper::toProductoResponse).toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO.Response> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(
                mapper.toProductoResponse(productoService.buscarPorId(id)));
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoDTO.Response>> listarPorCategoria(
            @PathVariable Long categoriaId) {
        List<ProductoDTO.Response> lista = productoService
                .listarPorCategoria(categoriaId)
                .stream().map(mapper::toProductoResponse).toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/alertas/stock-minimo")
    public ResponseEntity<List<ProductoDTO.Response>> alertasStockMinimo() {
        List<ProductoDTO.Response> lista = servicioInventario
                .alertasStockMinimo()
                .stream().map(mapper::toProductoResponse).toList();
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO.Response> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoDTO.Request request) {
        return ResponseEntity.ok(
                mapper.toProductoResponse(productoService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}