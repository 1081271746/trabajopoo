package com.pedidos.sistema_pedidos.domain.model.pago;

import jakarta.persistence.*;
import lombok.*;
import java.time.YearMonth;

@Entity
@Table(name = "tarjetas_credito")
@DiscriminatorValue("TARJETA")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class TarjetaCredito extends MetodoPago {

    @Column(name = "ultimos_digitos", length = 4)
    private String ultimosDigitos;

    private String franquicia;

    @Column(name = "fecha_expiracion", length = 5)
    private String fechaExpiracion;

    @Override
    public boolean autorizar(double monto) {
        return estaVigente() && monto > 0;
    }

    public boolean validarCVV(String cvv) {
        return cvv != null && cvv.matches("\\d{3,4}");
    }

    public boolean estaVigente() {
        if (fechaExpiracion == null || !fechaExpiracion.matches("\\d{2}/\\d{2}")) return false;
        String[] partes = fechaExpiracion.split("/");
        int mes  = Integer.parseInt(partes[0]);
        int anio = 2000 + Integer.parseInt(partes[1]);
        return !YearMonth.of(anio, mes).isBefore(YearMonth.now());
    }
}