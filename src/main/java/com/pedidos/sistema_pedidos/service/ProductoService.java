package com.pedidos.sistema_pedidos.service;

import com.pedidos.sistema_pedidos.domain.model.Categoria;
import com.pedidos.sistema_pedidos.domain.model.Producto;
import com.pedidos.sistema_pedidos.dto.producto.ProductoDTO;
import com.pedidos.sistema_pedidos.exception.RecursoNoEncontradoException;
import com.pedidos.sistema_pedidos.repository.CategoriaRepository;
import com.pedidos.sistema_pedidos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional
    public Producto crear(ProductoDTO.Request dto) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Categoria", dto.getCategoriaId()));
        Producto producto = Producto.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precio(dto.getPrecio())
                .stock(dto.getStock())
                .stockMinimo(dto.getStockMinimo())
                .categoria(categoria)
                .build();
        return productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));
    }

    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Producto> listarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    @Transactional
    public Producto actualizar(Long id, ProductoDTO.Request dto) {
        Producto producto = buscarPorId(id);
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Categoria", dto.getCategoriaId()));
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.actualizarPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setCategoria(categoria);
        return productoRepository.save(producto);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Producto", id);
        }
        productoRepository.deleteById(id);
    }
}