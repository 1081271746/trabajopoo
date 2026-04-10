package com.pedidos.sistema_pedidos.repository;

import com.pedidos.sistema_pedidos.domain.model.envio.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
}