package com.pedidos.sistema_pedidos.domain.model.pago;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Entity
@Table(name = "metodos_paypal")
@DiscriminatorValue("PAYPAL")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PayPal extends MetodoPago {

    @Email
    @Column(name = "email_cuenta", nullable = false)
    private String emailCuenta;

    @Override
    public boolean autorizar(double monto) {
        return emailCuenta != null && !emailCuenta.isBlank() && monto > 0;
    }

    public String redirigirAuth() {
        return "https://paypal.com/auth?email=" + emailCuenta;
    }
}