package com.agendamento.repository;

import com.agendamento.model.Agendamento;
import com.agendamento.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByClienteId(Long cliente);

    List<Agendamento> findByProfissionalId(Long profissional);

    @Query("""
        SELECT COUNT(a) > 0 FROM Agendamento a
        WHERE a.profissional = :profissional
        AND a.status <> 'CANCELADO'
        AND (
            a.dataHoraInicio < :fim
            AND a.dataHoraFim > :inicio
        )
    """)
    boolean existeConflitoDeHorario(
            @Param("profissional") Profissional profissional,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}
