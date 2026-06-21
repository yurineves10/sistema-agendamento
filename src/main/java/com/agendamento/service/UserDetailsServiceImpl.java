package com.agendamento.service;

import com.agendamento.repository.ClienteRepository;
import com.agendamento.repository.ProfissionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ClienteRepository clienteRepository;
    private final ProfissionalRepository profissionalRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        var clienteOpt = clienteRepository.findByEmail(email);
        if (clienteOpt.isPresent()) {
            var cliente = clienteOpt.get();
            return User.builder()
                    .username(cliente.getEmail())
                    .password(cliente.getSenha())
                    .roles("CLIENTE")
                    .build();
        }

        var profissionalOpt = profissionalRepository.findByEmail(email);
        if (profissionalOpt.isPresent()) {
            var profissional = profissionalOpt.get();
            return User.builder()
                    .username(profissional.getEmail())
                    .password(profissional.getSenha())
                    .roles("PROFISSIONAL")
                    .build();
        }

        throw new UsernameNotFoundException("Usuário não encontrado: " + email);
    }
}