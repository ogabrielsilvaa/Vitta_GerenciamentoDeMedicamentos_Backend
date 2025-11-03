package com.vitta.vittaBackend.repository;

import com.vitta.vittaBackend.entity.Agendamento;
import com.vitta.vittaBackend.enums.agendamento.AgendamentoStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer> {

    /**
     * Retorna uma lista de agendamentos ativos de um usuário, opcionalmente filtrada por data.
     * A consulta busca por agendamentos, tratamento e medicamento correspondentes,
     * que pertencem ao usuário logado e o status é maior que 0.
     *
     * @param usuarioId O ID do usuário dono dos agendamentos.
     * @param dataInicio A data/hora inicial do filtro (se nulo, não filtra por início).
     * @param dataFim A data/hora final do filtro (se nulo, não filtra por fim).
     * @return Uma lista de entidades {@link Agendamento} ativas para o usuário especificado.
     */
    @Query(
            "SELECT a FROM Agendamento a " +
                    "JOIN FETCH a.tratamento t " +
                    "WHERE a.usuario.id = :usuarioId AND a.status > 0" +
                    "AND (:dataInicio IS NULL OR a.horarioDoAgendamento >= :dataInicio) " +
                    "AND (:dataFim IS NULL OR a.horarioDoAgendamento <= :dataFim) " +
                    "ORDER BY a.horarioDoAgendamento ASC"
    )
    List<Agendamento> listarAgendamentosAtivos(
            @Param("usuarioId") Integer usuarioId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    /**
     * Busca um agendamento específico pelo seu ID e pelo ID do usuário proprietário.
     * Garante que um usuário só possa acessar seus próprios agendamentos.
     *
     * @param agendamentoId O ID do agendamento a ser buscado.
     * @param usuarioId O ID do usuário que deve ser o proprietário do agendamento.
     */
    @Query("SELECT a FROM Agendamento a WHERE a.id = :agendamentoId AND a.usuario.id = :usuarioId AND a.status >= 0")
    Agendamento listarAgendamentoPorId(@Param("agendamentoId") Integer agendamentoId, @Param("usuarioId") Integer usuarioId);

    /**
     * Realiza a exclusão lógica de um agendamento, garantindo que pertença ao usuário correto.
     * Esta consulta de atualização (UPDATE) altera o status do agendamento para 0 (inativo).
     *
     * @param agendamentoId O ID do agendamento a ser desativado.
     * @param usuarioId O ID do usuário proprietário do agendamento.
     */
    @Modifying
    @Transactional
    @Query("UPDATE Agendamento a SET a.status = 0 WHERE a.id = :agendamentoId AND a.usuario.id = :usuarioId")
    void apagarLogicoAgendamento(@Param("agendamentoId") Integer agendamentoId, @Param("usuarioId") Integer usuarioId);

    /**
     * Encontra o primeiro agendamento associado a um ID de tratamento específico.
     * "findFirst" - Limita o resultado ao primeiro encontrado (equivalente a "findTop").
     * "ByTratamentoId" - Filtra pela propriedade 'id' da entidade relacionada 'tratamento'.
     *
     * @param tratamentoId o ID do tratamento a ser buscado.
     * @param usuarioId o ID do usuário proprietário.
     * @return um Optional contendo o primeiro agendamento encontrado, ou um Optional vazio se nenhum for encontrado.
     */
    @Query("SELECT a FROM Agendamento a\n" +
            "WHERE a.tratamento.id = :tratamentoId\n" +
            "  AND a.usuario.id = :usuarioId\n" +
            "ORDER BY a.id ASC\n" +
            "LIMIT 1")
    Optional<Agendamento> obterPrimeiroAgendamentoDeTratamentoEspecifico(Integer tratamentoId, Integer usuarioId);

    /**
     * Deleta todos os agendamentos de um tratamento que estão com status PENDENTE
     * e cuja data/hora é futura em relação ao momento atual.
     *
     * @param tratamentoId O ID do tratamento.
     * @param usuarioId O ID do usuário proprietário dos agendamentos.
     * @param status O status do agendamento.
     * @param agora O momento atual, para garantir que apenas agendamentos futuros sejam apagados.
     */
    @Modifying
    @Query("DELETE FROM Agendamento a\n" +
            "WHERE a.tratamento.id = :tratamentoId\n" +
            "  AND a.usuario.id = :usuarioId\n" +
            "  AND a.status = :status\n" +
            "  AND a.horarioDoAgendamento > :agora")
    void deletarAgendamentosFuturosPendentesDeTratamento(
            @Param("tratamentoId") Integer tratamentoId,
            @Param("usuarioId") Integer usuarioId,
            @Param("status") AgendamentoStatus status,
            @Param("agora") LocalDateTime agora
    );

    /**
     * Encontra todos os agendamentos de um usuário específico que ocorrem dentro de um intervalo de tempo.
     *
     * @param usuarioId O ID do usuário para o qual os agendamentos serão buscados.
     * @param inicioDoDia O timestamp inicial do período de busca.
     * @param fimDoDia O timestamp final do período de busca.
     * @return Uma lista de {@link Agendamento} que correspondem aos critérios.
     */
    @Query("SELECT a FROM Agendamento a WHERE a.usuario.id = :usuarioId AND a.horarioDoAgendamento BETWEEN :inicioDoDia AND :fimDoDia")
    List<Agendamento> findAllByUsuarioAndPeriodo(
            @Param("usuarioId") Integer usuarioId,
            @Param("inicioDoDia") LocalDateTime inicioDoDia,
            @Param("fimDoDia") LocalDateTime fimDoDia
    );

    /**
     * Encontra todos os agendamentos associados a um tratamento específico e a um usuário.
     *
     * @param tratamentoId O ID do tratamento.
     * @param usuarioId    O ID do usuário proprietário do tratamento.
     * @return Uma lista de entidades {@link Agendamento} completas.
     */
    @Query("SELECT a FROM Agendamento a WHERE a.tratamento.id = :tratamentoId AND a.usuario.id = :usuarioId")
    List<Agendamento> findAllByTratamentoIdAndUsuarioId(
            @Param("tratamentoId") Integer tratamentoId,
            @Param("usuarioId") Integer usuarioId);

    /**
     * Conta o número de agendamentos para um tratamento específico que possuem um determinado status.
     *
     * @param tratamentoId O ID do tratamento.
     * @param status O status do agendamento a ser contado.
     * @return O número de agendamentos que correspondem aos critérios.
     */
    @Query("SELECT COUNT(a) FROM Agendamento a WHERE a.tratamento.id = :tratamentoId AND a.status = :status")
    long countByTratamentoIdAndStatus(@Param("tratamentoId") Integer tratamentoId, @Param("status") AgendamentoStatus status);
}
