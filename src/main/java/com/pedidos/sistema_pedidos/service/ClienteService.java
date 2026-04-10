package com.pedidos.sistema_pedidos.service;

import com.pedidos.sistema_pedidos.domain.model.Cliente;
import com.pedidos.sistema_pedidos.domain.model.Direccion;
import com.pedidos.sistema_pedidos.dto.cliente.ClienteDTO;
import com.pedidos.sistema_pedidos.exception.RecursoNoEncontradoException;
import com.pedidos.sistema_pedidos.exception.ReglaNegocioException;
import com.pedidos.sistema_pedidos.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public Cliente registrar(ClienteDTO.Request dto) {
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new ReglaNegocioException(
                    "Ya existe un cliente con el email: " + dto.getEmail());
        }
        Cliente cliente = Cliente.builder()
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .password(dto.getPassword())
                .build();
        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", id));
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional
    public Cliente agregarDireccion(Long clienteId, Direccion direccion) {
        Cliente cliente = buscarPorId(clienteId);
        cliente.agregarDireccion(direccion);
        return clienteRepository.save(cliente);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Cliente", id);
        }
        clienteRepository.deleteById(id);
    }
}