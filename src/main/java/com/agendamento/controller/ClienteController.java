package com.agendamento.controller;

import com.agendamento.dto.ClienteDTO;
import com.agendamento.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping("/meu-perfil")
    public ResponseEntity<ClienteDTO> meuPerfil(Authentication authentication) {
        String email = authentication.getName();
        ClienteDTO dto = clienteService.buscarPorEmail(email);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/meu-perfil")
    public ResponseEntity<ClienteDTO> atualizar(
            Authentication authentication,
            @RequestBody ClienteDTO dadosNovos) {

        String email = authentication.getName();
        ClienteDTO dto = clienteService.atualizar(email, dadosNovos);
        return ResponseEntity.ok(dto);
    }
}