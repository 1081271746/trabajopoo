package com.pedidos.sistema_pedidos.repository;

import com.pedidos.sistema_pedidos.domain.model.CodigoPromocional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CodigoPromocionalRepository extends JpaRepository<CodigoPromocional, Long> {

    Optional<CodigoPromocional> findByCodigo(String codigo);

    @Query("""
            SELECT c FROM CodigoPromocional c
            WHERE c.activo = true
            AND c.fechaVencimiento >= CURRENT_DATE
            AND c.usosActuales < c.usosMaximos
            """)
    List<CodigoPromocional> findCodigosVigentes();
}