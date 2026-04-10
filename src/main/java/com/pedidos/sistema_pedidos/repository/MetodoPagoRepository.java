package com.pedidos.sistema_pedidos.repository;

import com.pedidos.sistema_pedidos.domain.model.pago.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {
}