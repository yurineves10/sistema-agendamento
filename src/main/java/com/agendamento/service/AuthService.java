package com.agendamento.service;

import com.agendamento.config.JwtUtil;
import com.agendamento.dto.*;
import com.agendamento.model.Cliente;
import com.agendamento.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public ClienteDTO registrar(RegistroRequestDTO dto) {
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setSenha(passwordEncoder.encode(dto.getSenha()));
        cliente.setTelefone(dto.getTelefone());

        Cliente salvo = clienteRepository.save(cliente);

        ClienteDTO resposta = new ClienteDTO();
        resposta.setId(salvo.getId());
        resposta.setNome(salvo.getNome());
        resposta.setEmail(salvo.getEmail());
        resposta.setTelefone(salvo.getTelefone());
        resposta.setCriadoem(salvo.getCliadoEm());
        return resposta;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        Cliente cliente = clienteRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Email não encontrado"));

        if (!passwordEncoder.matches(dto.getSenha(), cliente.getSenha())) {
            throw new RuntimeException("Senha incorreta");
        }

        String token = jwtUtil.gerarToken(cliente.getEmail());
        return new LoginResponseDTO(token, cliente.getNome());
    }
}