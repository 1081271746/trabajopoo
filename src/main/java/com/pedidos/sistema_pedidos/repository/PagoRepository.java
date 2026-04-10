package com.pedidos.sistema_pedidos.repository;

import com.pedidos.sistema_pedidos.domain.enums.EstadoPago;
import com.pedidos.sistema_pedidos.domain.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByNumeroTransaccion(String numeroTransaccion);
    List<Pago> findByEstado(EstadoPago estado);
}