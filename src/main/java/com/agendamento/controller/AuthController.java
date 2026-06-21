package com.agendamento.controller;

import com.agendamento.dto.ClienteDTO;
import com.agendamento.dto.LoginRequestDTO;
import com.agendamento.dto.LoginResponseDTO;
import com.agendamento.dto.RegistroRequestDTO;
import com.agendamento.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    public ResponseEntity<ClienteDTO> registro(@Valid @RequestBody RegistroRequestDTO dto) {
        ClienteDTO resposta = authService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        LoginResponseDTO resposta = authService.login(dto);
        return ResponseEntity.ok(resposta);
    }

}