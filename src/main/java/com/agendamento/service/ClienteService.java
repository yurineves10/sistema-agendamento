package com.agendamento.service;

import com.agendamento.dto.ClienteDTO;
import com.agendamento.model.Cliente;
import com.agendamento.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteDTO buscarPorEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente nao encontrado"));

        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setEmail(cliente.getEmail());
        dto.setTelefone(cliente.getTelefone());
        dto.setCriadoem(cliente.getCliadoEm());
        return dto;
    }
    public ClienteDTO atualizar(String email, ClienteDTO dadosNovos) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        cliente.setNome(dadosNovos.getNome());
        cliente.setTelefone(dadosNovos.getTelefone());

        Cliente salvo = clienteRepository.save(cliente);

        ClienteDTO dto = new ClienteDTO();
        dto.setId(salvo.getId());
        dto.setNome(salvo.getNome());
        dto.setEmail(salvo.getEmail());
        dto.setTelefone(salvo.getTelefone());
        dto.setCriadoem(salvo.getCliadoEm());
        return dto;
    }
}
