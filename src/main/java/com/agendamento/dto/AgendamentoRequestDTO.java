package com.agendamento.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AgendamentoRequestDTO {

    @NotNull(message = "Profissional é obrigatório")
    private Long profissionalId;

    @NotNull(message = "Data e hora de início são obrigatórias")
    @Future(message = "O agendamento deve ser para uma data futura")
    private LocalDateTime dataHoraInicio;

    @NotNull(message = "Data e hora de fim são obrigatórias")
    @Future(message = "O agendamento deve ser para uma data futura")
    private LocalDateTime dataHoraFim;

    private String observacao;
}