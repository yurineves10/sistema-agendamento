package com.agendamento.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfissionalRegistroRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 80, message = "Nome deve ter entre 2 e 80 caracteres")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    @NotBlank(message = "Especialidade é obrigatória")
    private String especialidade;
}