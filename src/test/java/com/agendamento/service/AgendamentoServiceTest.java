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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProfissionalRepository profissionalRepository;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private Cliente cliente;
    private Profissional profissional;
    private AgendamentoRequestDTO requestDTO;
    private Agendamento agendamentoSalvo;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Maria Silva");
        cliente.setEmail("maria@email.com");
        cliente.setSenha("hash");

        profissional = new Profissional();
        profissional.setId(1L);
        profissional.setNome("Dr. Carlos");
        profissional.setEspecialidade("Cardiologia");
        profissional.setEmail("carlos@clinica.com");
        profissional.setAtivo(true);

        requestDTO = new AgendamentoRequestDTO();
        requestDTO.setProfissionalId(1L);
        requestDTO.setDataHoraInicio(LocalDateTime.of(2026, 2, 27, 5, 0));
        requestDTO.setDataHoraFim(LocalDateTime.of(2026, 4, 1, 6, 0));
        requestDTO.setObservacao("Consulta de rotina");

        agendamentoSalvo = new Agendamento();
        agendamentoSalvo.setId(1L);
        agendamentoSalvo.setCliente(cliente);
        agendamentoSalvo.setProfissional(profissional);
        agendamentoSalvo.setDataHoraInicio(requestDTO.getDataHoraInicio());
        agendamentoSalvo.setDataHoraFim(requestDTO.getDataHoraFim());
        agendamentoSalvo.setStatus(StatusAgendamento.PENDENTE);
        agendamentoSalvo.setObservacao("Consulta de rotina");
    }

    @Nested
    @DisplayName("Criação de agendamento")
    class CriacaoAgendamento {

        @Test
        @DisplayName("Deve criar agendamento com sucesso")
        void deveCriarAgendamentoComSucesso() {
            // ARRANGE
            when(clienteRepository.findByEmail(anyString()))
                    .thenReturn(Optional.of(cliente));
            when(profissionalRepository.findById(1L))
                    .thenReturn(Optional.of(profissional));
            when(agendamentoRepository.existeConflitoDeHorario(any(), any(), any()))
                    .thenReturn(false);
            when(agendamentoRepository.save(any(Agendamento.class)))
                    .thenReturn(agendamentoSalvo);

            AgendamentoDTO resultado = agendamentoService.criar(
                    "maria@email.com", requestDTO);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getNomeCliente()).isEqualTo("Maria Silva");
            assertThat(resultado.getNomeProfissional()).isEqualTo("Dr. Carlos");
            assertThat(resultado.getStatus()).isEqualTo(StatusAgendamento.PENDENTE);
            assertThat(resultado.getEspecialidadeProfissional()).isEqualTo("Cardiologia");

            verify(agendamentoRepository).save(any(Agendamento.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando profissional está inativo")
        void deveLancarExcecaoQuandoProfissionalInativo() {

            profissional.setAtivo(false);

            when(clienteRepository.findByEmail(anyString()))
                    .thenReturn(Optional.of(cliente));
            when(profissionalRepository.findById(1L))
                    .thenReturn(Optional.of(profissional));

            assertThatThrownBy(() ->
                    agendamentoService.criar("maria@email.com", requestDTO))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Profissional inativo");

            verify(agendamentoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando horário de início é depois do fim")
        void deveLancarExcecaoQuandoHorarioInvalido() {

            requestDTO.setDataHoraInicio(LocalDateTime.of(2026, 7, 24, 12, 0));
            requestDTO.setDataHoraFim(LocalDateTime.of(2026, 7, 5, 10, 0));

            when(clienteRepository.findByEmail(anyString()))
                    .thenReturn(Optional.of(cliente));
            when(profissionalRepository.findById(1L))
                    .thenReturn(Optional.of(profissional));

            assertThatThrownBy(() ->
                    agendamentoService.criar("maria@email.com", requestDTO))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Horário de início deve ser antes do fim");

            verify(agendamentoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando há conflito de horário")
        void deveLancarExcecaoQuandoHaConflitoDeHorario() {

            when(clienteRepository.findByEmail(anyString()))
                    .thenReturn(Optional.of(cliente));
            when(profissionalRepository.findById(1L))
                    .thenReturn(Optional.of(profissional));
            when(agendamentoRepository.existeConflitoDeHorario(any(), any(), any()))
                    .thenReturn(true);

            assertThatThrownBy(() ->
                    agendamentoService.criar("maria@email.com", requestDTO))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Profissional já tem agendamento nesse horário");

            verify(agendamentoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando cliente não encontrado")
        void deveLancarExcecaoQuandoClienteNaoEncontrado() {

            when(clienteRepository.findByEmail(anyString()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    agendamentoService.criar("naoexiste@email.com", requestDTO))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Cliente não encontrado");

            verify(profissionalRepository, never()).findById(any());
            verify(agendamentoRepository, never()).save(any());
        }
    }


    @Nested
    @DisplayName("Cancelamento de agendamento")
    class CancelamentoAgendamento {

        @Test
        @DisplayName("Deve cancelar agendamento com sucesso")
        void deveCancelarAgendamentoComSucesso() {

            Agendamento cancelado = new Agendamento();
            cancelado.setId(1L);
            cancelado.setCliente(cliente);
            cancelado.setProfissional(profissional);
            cancelado.setStatus(StatusAgendamento.CANCELADO);
            cancelado.setDataHoraInicio(requestDTO.getDataHoraInicio());
            cancelado.setDataHoraFim(requestDTO.getDataHoraFim());

            when(agendamentoRepository.findById(1L))
                    .thenReturn(Optional.of(agendamentoSalvo));
            when(agendamentoRepository.save(any()))
                    .thenReturn(cancelado);

            AgendamentoDTO resultado = agendamentoService.cancelar(
                    1L, "maria@email.com");


            assertThat(resultado.getStatus()).isEqualTo(StatusAgendamento.CANCELADO);
            verify(agendamentoRepository).save(any());
        }
        @Nested
        @DisplayName("Confirmação de agendamento")
        class ConfirmacaoAgendamento {

            @Test
            @DisplayName("Deve confirmar agendamento com sucesso")
            void deveConfirmarAgendamentoComSucesso() {
                // ARRANGE
                Agendamento confirmado = new Agendamento();
                confirmado.setId(1L);
                confirmado.setCliente(cliente);
                confirmado.setProfissional(profissional);
                confirmado.setStatus(StatusAgendamento.CONFIRMADO);
                confirmado.setDataHoraInicio(requestDTO.getDataHoraInicio());
                confirmado.setDataHoraFim(requestDTO.getDataHoraFim());

                when(agendamentoRepository.findById(1L))
                        .thenReturn(Optional.of(agendamentoSalvo));
                when(agendamentoRepository.save(any()))
                        .thenReturn(confirmado);

                AgendamentoDTO resultado = agendamentoService.confirmar(
                        1L, "carlos@clinica.com");

                assertThat(resultado.getStatus()).isEqualTo(StatusAgendamento.CONFIRMADO);
                verify(agendamentoRepository).save(any());
            }

            @Test
            @DisplayName("Deve lançar exceção quando profissional tenta confirmar agendamento de outro")
            void deveLancarExcecaoQuandoProfissionalConfirmaDeOutro() {

                when(agendamentoRepository.findById(1L))
                        .thenReturn(Optional.of(agendamentoSalvo));

                assertThatThrownBy(() ->
                        agendamentoService.confirmar(1L, "outro@clinica.com"))
                        .isInstanceOf(RuntimeException.class)
                        .hasMessage("Você não tem permissão para confirmar este agendamento");

                verify(agendamentoRepository, never()).save(any());
            }

            @Test
            @DisplayName("Deve lançar exceção quando agendamento não está pendente")
            void deveLancarExcecaoQuandoAgendamentoNaoPendente() {

                agendamentoSalvo.setStatus(StatusAgendamento.CONFIRMADO);

                when(agendamentoRepository.findById(1L))
                        .thenReturn(Optional.of(agendamentoSalvo));

                assertThatThrownBy(() ->
                        agendamentoService.confirmar(1L, "carlos@clinica.com"))
                        .isInstanceOf(RuntimeException.class)
                        .hasMessage("Apenas agendamentos pendentes podem ser confirmados");

                verify(agendamentoRepository, never()).save(any());
            }
        }

        @Test
        @DisplayName("Deve lançar exceção quando cliente tenta cancelar agendamento de outro")
        void deveLancarExcecaoQuandoClienteTentaCancelarDeOutro() {

            when(agendamentoRepository.findById(1L))
                    .thenReturn(Optional.of(agendamentoSalvo));

            assertThatThrownBy(() ->
                    agendamentoService.cancelar(1L, "outro@email.com"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Você não tem permissão para cancelar este agendamento");

            verify(agendamentoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando agendamento já está cancelado")
        void deveLancarExcecaoQuandoAgendamentoJaCancelado() {

            agendamentoSalvo.setStatus(StatusAgendamento.CANCELADO);

            when(agendamentoRepository.findById(1L))
                    .thenReturn(Optional.of(agendamentoSalvo));


            assertThatThrownBy(() ->
                    agendamentoService.cancelar(1L, "maria@email.com"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Agendamento já está cancelado");

            verify(agendamentoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Listagem de agendamentos")
    class ListagemAgendamentos {

        @Test
        @DisplayName("Deve listar agendamentos do cliente")
        void deveListarAgendamentosDoCliente() {

            when(clienteRepository.findByEmail(anyString()))
                    .thenReturn(Optional.of(cliente));
            when(agendamentoRepository.findByClienteId(1L))
                    .thenReturn(List.of(agendamentoSalvo));


            var resultado = agendamentoService.listarDoCliente("maria@email.com");


            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getNomeCliente()).isEqualTo("Maria Silva");
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando cliente não tem agendamentos")
        void deveRetornarListaVaziaQuandoSemAgendamentos() {

            when(clienteRepository.findByEmail(anyString()))
                    .thenReturn(Optional.of(cliente));
            when(agendamentoRepository.findByClienteId(1L))
                    .thenReturn(List.of());


            var resultado = agendamentoService.listarDoCliente("maria@email.com");

            assertThat(resultado).isEmpty();
        }
    }
}
