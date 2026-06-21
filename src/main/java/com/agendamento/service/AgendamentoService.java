package com.agendamento.service;

import com.agendamento.dto.AgendamentoDTO;
import com.agendamento.dto.AgendamentoRequestDTO;
import com.agendamento.model.Agendamento;
import com.agendamento.model.Agendamento.StatusAgendamento;
import com.agendamento.model.Cliente;
import com.agendamento.model.Profissional;
import com.agendamento.repository.AgendamentoRepository;
import com.agendamento.repository.ClienteRepository;
import com.agendamento.repository.ProfissionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository clienteRepository;
    private final ProfissionalRepository profissionalRepository;

    public AgendamentoDTO criar(String emailCliente, AgendamentoRequestDTO dto) {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Profissional profissional = profissionalRepository.findById(dto.getProfissionalId())
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        if (!profissional.getAtivo()) {
            throw new RuntimeException("Profissional inativo");
        }

        if (dto.getDataHoraInicio().isAfter(dto.getDataHoraFim())) {
            throw new RuntimeException("Horário de início deve ser antes do fim");
        }

        boolean temConflito = agendamentoRepository.existeConflitoDeHorario(
                profissional,
                dto.getDataHoraInicio(),
                dto.getDataHoraFim()
        );

        if (temConflito) {
            throw new RuntimeException("Profissional já tem agendamento nesse horário");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setProfissional(profissional);
        agendamento.setDataHoraInicio(dto.getDataHoraInicio());
        agendamento.setDataHoraFim(dto.getDataHoraFim());
        agendamento.setObservacao(dto.getObservacao());
        agendamento.setStatus(StatusAgendamento.PENDENTE);

        return converterParaDTO(agendamentoRepository.save(agendamento));
    }

    public List<AgendamentoDTO> listarDoCliente(String emailCliente) {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        return agendamentoRepository.findByClienteId(cliente.getId())
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public AgendamentoDTO buscarPorId(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        return converterParaDTO(agendamento);
    }

    public AgendamentoDTO cancelar(Long id, String emailCliente) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        if (!agendamento.getCliente().getEmail().equals(emailCliente)) {
            throw new RuntimeException("Você não tem permissão para cancelar este agendamento");
        }

        if (agendamento.getStatus() == StatusAgendamento.CANCELADO) {
            throw new RuntimeException("Agendamento já está cancelado");
        }

        agendamento.setStatus(StatusAgendamento.CANCELADO);
        return converterParaDTO(agendamentoRepository.save(agendamento));
    }
    public AgendamentoDTO confirmar(Long id, String emailProfissional) {

        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        if (!agendamento.getProfissional().getEmail().equals(emailProfissional)) {
            throw new RuntimeException(
                    "Você não tem permissão para confirmar este agendamento");
        }

      
        if (agendamento.getStatus() != StatusAgendamento.PENDENTE) {
            throw new RuntimeException(
                    "Apenas agendamentos pendentes podem ser confirmados");
        }

        agendamento.setStatus(StatusAgendamento.CONFIRMADO);
        return converterParaDTO(agendamentoRepository.save(agendamento));
    }

    public AgendamentoDTO converterParaDTO(Agendamento a) {
        AgendamentoDTO dto = new AgendamentoDTO();
        dto.setId(a.getId());
        dto.setNomeCliente(a.getCliente().getNome());
        dto.setNomeProfissional(a.getProfissional().getNome());
        dto.setEspecialidadeProfissional(a.getProfissional().getEspecialidade());
        dto.setDataHoraInicio(a.getDataHoraInicio());
        dto.setDataHoraFim(a.getDataHoraFim());
        dto.setStatus(a.getStatus());
        dto.setObservacao(a.getObservacao());
        return dto;
    }
}