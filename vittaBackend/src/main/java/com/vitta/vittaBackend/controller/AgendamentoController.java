package com.vitta.vittaBackend.controller;

import com.vitta.vittaBackend.dto.request.agendamento.AgendamentoAtualizarDTORequest;
import com.vitta.vittaBackend.dto.request.agendamento.AgendamentoDTORequest;
import com.vitta.vittaBackend.dto.request.medicamentoHistorico.RegistrarUsoDTORequest;
import com.vitta.vittaBackend.dto.response.agenda.AgendaDoDiaDTOResponse;
import com.vitta.vittaBackend.dto.response.agendamento.AgendamentoDTOResponse;
import com.vitta.vittaBackend.dto.response.medicamentoHistorico.MedicamentoHistoricoDTOResponse;
import com.vitta.vittaBackend.security.UserDetailsImpl;
import com.vitta.vittaBackend.service.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller REST para gerenciar as operações de Agendamentos do usuário autenticado.
 * <p>
 * Expõe os endpoints protegidos para listar, criar, atualizar e deletar
 * agendamentos, garantindo que um usuário só possa manipular seus próprios dados
 * através da validação do token JWT em cada requisição.
 */
@RestController
@RequestMapping("/api/agendamentos")
@Tag(name = "Agendamento", description = "API para gerenciamento dos agendamentos do usuário autenticado.")
public class AgendamentoController {

    private AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) { this.agendamentoService = agendamentoService; }

    /**
     * Lista todos os agendamentos do usuário autenticado.
     * @param userDetails O principal do usuário autenticado, injetado pelo Spring Security.
     * @return Uma lista de agendamentos do usuário.
     */
    @GetMapping("/listar")
    @Operation(summary = "Listar meus Agendamentos.", description = "Endpoint para listar todos os Agendamentos do usuário logado." +
    "Permite filtrar por um intervalo de datas (dataInicio e dataFim).")
    public ResponseEntity<List<AgendamentoDTOResponse>> listarAgendamentos(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate dataFim
            ) {
        Integer usuarioId = userDetails.getUserId();

        List<AgendamentoDTOResponse> agendamentos = agendamentoService.listarAgendamentosDoUsuario(usuarioId, dataInicio, dataFim);

        return ResponseEntity.ok(agendamentos);
    }

    /**
     * Busca um agendamento específico pelo ID, garantindo que pertença ao usuário autenticado.
     * @param agendamentoId O ID do agendamento a ser buscado.
     * @param userDetails O principal do usuário autenticado.
     * @return O agendamento encontrado.
     */
    @GetMapping("/listarAgendamentoPorId/{agendamentoId}")
    @Operation(summary = "Listar o Agendamento pelo ID dele.", description = "Endpoint para listar um Agendamento específico do usuário logado.")
    public ResponseEntity<AgendamentoDTOResponse> buscarAgendamentoPorId(
            @PathVariable("agendamentoId") Integer agendamentoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Integer usuarioId = userDetails.getUserId();
        AgendamentoDTOResponse agendamentoDTOResponse = agendamentoService.listarAgendamentoPorId(agendamentoId, usuarioId);

            return ResponseEntity.ok(agendamentoDTOResponse);
    }

    /**
     * Lista todos os agendamentos de um tratamento específico do usuário autenticado.
     *
     * @param tratamentoId O ID do tratamento a ser consultado, vindo da URL.
     * @param userDetails  O principal do usuário autenticado.
     * @return ResponseEntity com a lista de agendamentos (status 200 OK).
     */
    @GetMapping("listarAgendamentosDoTratamento/{tratamentoId}")
    @Operation(summary = "Listar os Agendamentos pelo ID do Tratamento.",
            description = "Endpoint para listar Agendamentos que pertencem a um Tratamento, do usuário logado.")
    public ResponseEntity<List<AgendamentoDTOResponse>> buscarAgendamentosDoTratamento(
            @PathVariable("tratamentoId") Integer tratamentoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Integer usuarioId = userDetails.getUserId();
        List<AgendamentoDTOResponse> agendamentoDTOResponse = agendamentoService.listarAgendamentosDoTratamento(tratamentoId, usuarioId);
        return  ResponseEntity.ok(agendamentoDTOResponse);
    }

    /**
     * Cria um novo agendamento para o usuário autenticado.
     * @param agendamentoDTORequest O corpo da requisição com os dados do novo agendamento.
     * @param userDetails O principal do usuário autenticado.
     * @return O agendamento recém-criado.
     */
    @PostMapping("/cadastrar")
    @Operation(summary = "Criar novo Agendamento.", description = "Endpoint para criar um novo registro de Agendamento para o usuário logado.")
    public ResponseEntity<AgendamentoDTOResponse> cadastrarAgendamento(
            @Valid @RequestBody AgendamentoDTORequest agendamentoDTORequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Integer usuarioId = userDetails.getUserId();
        AgendamentoDTOResponse agendamentoDTOResponse = agendamentoService.criarAgendamento(agendamentoDTORequest, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamentoDTOResponse);
    }

    /**
     * Atualiza um agendamento existente do usuário autenticado.
     * @param agendamentoId O ID do agendamento a ser atualizado.
     * @param agendamentoAtualizarDTORequest O corpo da requisição com os dados para atualização.
     * @param userDetails O principal do usuário autenticado.
     * @return O agendamento atualizado.
     */
    @PutMapping("/atualizar/{agendamentoId}")
    @Operation(summary = "Atualizar todos os dados do Agendamento.", description = "Endpoint para atualizar o registro do Agendamento existente do usuário logado.")
    public ResponseEntity<AgendamentoDTOResponse> atualizarAgendamentoPorId(
            @PathVariable("agendamentoId") Integer agendamentoId,
            @RequestBody @Valid AgendamentoAtualizarDTORequest agendamentoAtualizarDTORequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Integer usuarioId = userDetails.getUserId();
        AgendamentoDTOResponse agendamentoAtualizado = agendamentoService.atualizarAgendamento(agendamentoId, usuarioId, agendamentoAtualizarDTORequest);
        return ResponseEntity.ok(agendamentoAtualizado);
    }

    /**
     * Realiza a exclusão lógica de um agendamento do usuário autenticado.
     * @param agendamentoId O ID do agendamento a ser deletado.
     * @param userDetails O principal do usuário autenticado.
     * @return Resposta sem conteúdo.
     */
    @DeleteMapping("/deletar/{agendamentoId}")
    @Operation(summary = "Deletar todos os dados do Agendamento.", description = "Endpoint para deletar o registro do Agendamento do usuário logado.")
    public ResponseEntity<Void> deletarAgendamento(
            @PathVariable("agendamentoId") Integer agendamentoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Integer usuarioId = userDetails.getUserId();
        agendamentoService.deletarAgendamento(agendamentoId, usuarioId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Conclui um agendamento registrando o uso do medicamento.
     *
     * @param agendamentoId ID do agendamento a ser concluído.
     * @param requestDTO Dados do uso do medicamento.
     * @param userDetails Usuário autenticado pelo Spring Security.
     * @return ResponseEntity com o histórico do uso recém-criado.
     */
    @PostMapping("/concluirAgendamento/{agendamentoId}")
    @Operation(summary = "Concluir um Agendamento.", description = "Endpoint para concluir um Agendamento, automaticamente mudando seu status.")
    public ResponseEntity<MedicamentoHistoricoDTOResponse> registrarUso(
            @PathVariable("agendamentoId") Integer agendamentoId,
            @Valid @RequestBody RegistrarUsoDTORequest requestDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Integer usuarioId = userDetails.getUserId();
        MedicamentoHistoricoDTOResponse respostaDTO = agendamentoService.concluirAgendamento(agendamentoId, requestDTO, usuarioId);

        return ResponseEntity.ok(respostaDTO);
    }

}
