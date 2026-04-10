package com.pedidos.sistema_pedidos.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Direccion {

    @NotBlank(message = "La calle es obligatoria")
    private String calle;

    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

    private String departamento;
    private String codigoPostal;

    @NotBlank(message = "El país es obligatorio")
    private String pais;

    public boolean esValida() {
        return calle != null && !calle.isBlank()
                && ciudad != null && !ciudad.isBlank()
                && pais != null && !pais.isBlank();
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s", calle, ciudad, pais);
    }
}