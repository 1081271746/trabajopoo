package com.pedidos.sistema_pedidos.service;

import com.pedidos.sistema_pedidos.domain.model.Categoria;
import com.pedidos.sistema_pedidos.exception.RecursoNoEncontradoException;
import com.pedidos.sistema_pedidos.exception.ReglaNegocioException;
import com.pedidos.sistema_pedidos.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional
    public Categoria crear(String nombre, String descripcion) {
        if (categoriaRepository.existsByNombre(nombre)) {
            throw new ReglaNegocioException(
                    "Ya existe una categoría con el nombre: " + nombre);
        }
        return categoriaRepository.save(
                Categoria.builder().nombre(nombre).descripcion(descripcion).build());
    }

    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria", id));
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    @Transactional
    public Categoria actualizar(Long id, String nombre, String descripcion) {
        Categoria categoria = buscarPorId(id);
        if (!categoria.getNombre().equals(nombre)
                && categoriaRepository.existsByNombre(nombre)) {
            throw new ReglaNegocioException(
                    "Ya existe una categoría con el nombre: " + nombre);
        }
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = buscarPorId(id);
        if (!categoria.getProductos().isEmpty()) {
            throw new ReglaNegocioException(
                    "No se puede eliminar la categoría '"
                            + categoria.getNombre() + "' porque tiene productos asociados");
        }
        categoriaRepository.deleteById(id);
    }
}