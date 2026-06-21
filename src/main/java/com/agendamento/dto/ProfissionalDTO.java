package com.agendamento.dto;

import lombok.Data;

@Data
public class ProfissionalDTO {

    private Long id;
    private String nome;
    private String email;
    private String especialidade;
    private Boolean ativo;
}