package com.agendamento.service;

import com.agendamento.config.JwtUtil;
import com.agendamento.dto.LoginRequestDTO;
import com.agendamento.dto.LoginResponseDTO;
import com.agendamento.dto.RegistroRequestDTO;
import com.agendamento.dto.ClienteDTO;
import com.agendamento.model.Cliente;
import com.agendamento.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private Cliente clienteSalvo;
    private RegistroRequestDTO registroDTO;
    private LoginRequestDTO loginDTO;

    @BeforeEach
    void setUp() {
        registroDTO = new RegistroRequestDTO();
        registroDTO.setNome("João Silva");
        registroDTO.setEmail("joao@email.com");
        registroDTO.setSenha("123456");
        registroDTO.setTelefone("11999999999");

        loginDTO = new LoginRequestDTO();
        loginDTO.setEmail("joao@email.com");
        loginDTO.setSenha("123456");

        clienteSalvo = new Cliente();
        clienteSalvo.setId(1L);
        clienteSalvo.setNome("João Silva");
        clienteSalvo.setEmail("joao@email.com");
        clienteSalvo.setSenha("$2a$10$hashBCrypt");
        clienteSalvo.setTelefone("11999999999");
        clienteSalvo.setCliadoEm(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve registrar cliente com sucesso")
    void deveRegistrarClienteComSucesso() {

        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashBCrypt");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        ClienteDTO resultado = authService.registrar(registroDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getEmail()).isEqualTo("joao@email.com");

        verify(clienteRepository).existsByEmail("joao@email.com");
        verify(passwordEncoder).encode("123456");
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já cadastrado")
    void deveLancarExcecaoQuandoEmailJaCadastrado() {

        when(clienteRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.registrar(registroDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email já cadastrado");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve retornar senha no DTO de resposta")
    void naoDeveRetornarSenhaNoDTO() {

        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashBCrypt");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        ClienteDTO resultado = authService.registrar(registroDTO);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getEmail()).isEqualTo("joao@email.com");
        assertThat(resultado.getTelefone()).isEqualTo("11999999999");
    }

    @Test
    @DisplayName("Deve fazer login com sucesso e retornar token")
    void deveFazerLoginComSucesso() {
        // ARRANGE
        when(clienteRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(clienteSalvo));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.gerarToken(anyString())).thenReturn("token-jwt-fake");

        // ACT
        LoginResponseDTO resultado = authService.login(loginDTO);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.getToken()).isEqualTo("token-jwt-fake");
        assertThat(resultado.getNomeUsuario()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Deve lançar exceção quando email não encontrado")
    void deveLancarExcecaoQuandoEmailNaoEncontrado() {
        // ARRANGE
        when(clienteRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThatThrownBy(() -> authService.login(loginDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email não encontrado");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha incorreta")
    void deveLancarExcecaoQuandoSenhaIncorreta() {
        // ARRANGE
        when(clienteRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(clienteSalvo));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // ACT + ASSERT
        assertThatThrownBy(() -> authService.login(loginDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Senha incorreta");

        verify(jwtUtil, never()).gerarToken(anyString());
    }
}