package com.agendamento.controller;

import com.agendamento.dto.AgendamentoDTO;
import com.agendamento.dto.AgendamentoRequestDTO;
import com.agendamento.service.AgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    @PostMapping
    public ResponseEntity<AgendamentoDTO> criar(
            Authentication authentication,
            @Valid @RequestBody AgendamentoRequestDTO dto) {

        String email = authentication.getName();
        AgendamentoDTO resposta = agendamentoService.criar(email, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping("/meus")
    public ResponseEntity<List<AgendamentoDTO>> meusAgendamentos(
            Authentication authentication) {

        String email = authentication.getName();
        List<AgendamentoDTO> lista = agendamentoService.listarDoCliente(email);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoDTO> buscarPorId(@PathVariable Long id) {
        try {
            AgendamentoDTO dto = agendamentoService.buscarPorId(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/{id}/confirmar")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<AgendamentoDTO> confirmar(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        AgendamentoDTO resposta = agendamentoService.confirmar(id, email);
        return ResponseEntity.ok(resposta);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<AgendamentoDTO> cancelar(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        AgendamentoDTO resposta = agendamentoService.cancelar(id, email);
        return ResponseEntity.ok(resposta);
    }

}