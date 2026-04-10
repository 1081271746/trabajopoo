package com.pedidos.sistema_pedidos.repository;

import com.pedidos.sistema_pedidos.domain.enums.EstadoPedido;
import com.pedidos.sistema_pedidos.domain.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByClienteIdAndEstado(Long clienteId, EstadoPedido estado);

    @Query("""
            SELECT DISTINCT p FROM Pedido p
            JOIN p.detalles d
            WHERE d.producto.id = :productoId
            AND p.estado NOT IN ('CANCELADO', 'REEMBOLSADO')
            """)
    List<Pedido> findPedidosActivosByProductoId(@Param("productoId") Long productoId);
}