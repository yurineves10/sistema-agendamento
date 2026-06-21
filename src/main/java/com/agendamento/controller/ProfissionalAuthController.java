package com.agendamento.controller;

import com.agendamento.dto.LoginRequestDTO;
import com.agendamento.dto.LoginResponseDTO;
import com.agendamento.dto.ProfissionalDTO;
import com.agendamento.dto.ProfissionalRegistroRequestDTO;
import com.agendamento.service.ProfissionalAuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profissionais/auth")
@RequiredArgsConstructor
public class ProfissionalAuthController {

    private final ProfissionalAuthService profissionalAuthService;

    @PostMapping("/registro")
    public ResponseEntity<ProfissionalDTO> registro(
            @Valid @RequestBody ProfissionalRegistroRequestDTO dto) {
        ProfissionalDTO resposta = profissionalAuthService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto) {
        LoginResponseDTO resposta = profissionalAuthService.login(dto);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/meu-perfil")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProfissionalDTO> meuPerfil(Authentication authentication) {
        String email = authentication.getName();
        ProfissionalDTO dto = profissionalAuthService.buscarPorEmail(email);
        return ResponseEntity.ok(dto);
    }
}