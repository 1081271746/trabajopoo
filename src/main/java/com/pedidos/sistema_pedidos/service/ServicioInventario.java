package com.pedidos.sistema_pedidos.service;

import com.pedidos.sistema_pedidos.domain.model.Producto;
import com.pedidos.sistema_pedidos.exception.ReglaNegocioException;
import com.pedidos.sistema_pedidos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicioInventario {

    private final ProductoRepository    productoRepository;
    private final ServicioNotificacion  servicioNotificacion;

    @Transactional(readOnly = true)
    public boolean verificarStock(Producto producto, int cantidad) {
        return producto.verificarDisponibilidad(cantidad);
    }

    @Transactional
    public boolean reservarStock(Producto producto, int cantidad) {
        if (!verificarStock(producto, cantidad)) {
            throw new ReglaNegocioException(
                    "Stock insuficiente para '" + producto.getNombre()
                            + "'. Disponible: " + producto.getStock()
                            + ", solicitado: " + cantidad);
        }
        producto.disminuirStock(cantidad);
        productoRepository.save(producto);
        if (producto.estaEnStockMinimo()) {
            log.warn("Producto '{}' alcanzó stock mínimo: {}",
                    producto.getNombre(), producto.getStock());
            servicioNotificacion.notificarStockMinimo(producto);
        }
        return true;
    }

    @Transactional
    public void liberarStock(Producto producto, int cantidad) {
        producto.aumentarStock(cantidad);
        productoRepository.save(producto);
        log.info("Stock liberado para '{}': +{}", producto.getNombre(), cantidad);
    }

    @Transactional(readOnly = true)
    public List<Producto> alertasStockMinimo() {
        return productoRepository.findProductosEnStockMinimo();
    }
}