package com.pedidos.sistema_pedidos.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank
    @Email(message = "El email no es válido")
    @Column(nullable = false, unique = true)
    private String email;

    private String telefono;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @ElementCollection
    @CollectionTable(
            name = "cliente_direcciones",
            joinColumns = @JoinColumn(name = "cliente_id")
    )
    @AttributeOverrides({
            @AttributeOverride(name = "calle",        column = @Column(name = "calle")),
            @AttributeOverride(name = "ciudad",       column = @Column(name = "ciudad")),
            @AttributeOverride(name = "departamento", column = @Column(name = "departamento")),
            @AttributeOverride(name = "codigoPostal", column = @Column(name = "codigo_postal")),
            @AttributeOverride(name = "pais",         column = @Column(name = "pais"))
    })
    @Builder.Default
    private List<Direccion> direcciones = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Pedido> pedidos = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDate.now();
        }
    }

    public void agregarDireccion(Direccion direccion) {
        if (!direccion.esValida()) {
            throw new IllegalArgumentException("La dirección no es válida");
        }
        this.direcciones.add(direccion);
    }

    public Direccion getDireccionPrincipal() {
        if (direcciones.isEmpty()) {
            throw new IllegalStateException("El cliente no tiene direcciones registradas");
        }
        return direcciones.get(0);
    }
}