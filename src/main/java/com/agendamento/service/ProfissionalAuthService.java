package com.agendamento.service;

import com.agendamento.config.JwtUtil;
import com.agendamento.dto.LoginRequestDTO;
import com.agendamento.dto.LoginResponseDTO;
import com.agendamento.dto.ProfissionalDTO;
import com.agendamento.dto.ProfissionalRegistroRequestDTO;
import com.agendamento.model.Profissional;
import com.agendamento.repository.ProfissionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfissionalAuthService {

    private final ProfissionalRepository profissionalRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public ProfissionalDTO registrar(ProfissionalRegistroRequestDTO dto) {

        if (profissionalRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        Profissional profissional = new Profissional();
        profissional.setNome(dto.getNome());
        profissional.setEmail(dto.getEmail());
        profissional.setSenha(passwordEncoder.encode(dto.getSenha()));
        profissional.setEspecialidade(dto.getEspecialidade());
        profissional.setAtivo(true);

        Profissional salvo = profissionalRepository.save(profissional);

        return converterParaDTO(salvo);
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {

        Profissional profissional = profissionalRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Email não encontrado"));

        if (!passwordEncoder.matches(dto.getSenha(), profissional.getSenha())) {
            throw new RuntimeException("Senha incorreta");
        }

        String token = jwtUtil.gerarToken(profissional.getEmail());
        return new LoginResponseDTO(token, profissional.getNome());
    }

    public ProfissionalDTO buscarPorEmail(String email) {
        Profissional profissional = profissionalRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
        return converterParaDTO(profissional);
    }

    private ProfissionalDTO converterParaDTO(Profissional p) {
        ProfissionalDTO dto = new ProfissionalDTO();
        dto.setId(p.getId());
        dto.setNome(p.getNome());
        dto.setEmail(p.getEmail());
        dto.setEspecialidade(p.getEspecialidade());
        dto.setAtivo(p.getAtivo());
        return dto;
    }
}