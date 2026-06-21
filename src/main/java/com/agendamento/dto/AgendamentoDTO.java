package com.agendamento.dto;

import com.agendamento.model.Agendamento.StatusAgendamento;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AgendamentoDTO {
    private Long id;
    private String nomeCliente;
    private String nomeProfissional;
    private String especialidadeProfissional;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private StatusAgendamento status;
    private String observacao;
}

