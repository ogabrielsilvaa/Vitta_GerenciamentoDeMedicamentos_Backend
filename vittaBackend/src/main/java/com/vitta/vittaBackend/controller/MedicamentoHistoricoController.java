package com.vitta.vittaBackend.controller;

import com.vitta.vittaBackend.dto.request.medicamentoHistorico.MedicamentoHistoricoDTORequest;
import com.vitta.vittaBackend.dto.request.medicamentoHistorico.MedicamentoHistoricoAtualizarDTORequest;
import com.vitta.vittaBackend.dto.response.medicamentoHistorico.MedicamentoHistoricoDTOResponse;
import com.vitta.vittaBackend.security.UserDetailsImpl;
import com.vitta.vittaBackend.service.MedicamentoHistoricoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciar as operações de MedicamentoHistorico do usuário autenticado.
 * <p>
 * Expõe os endpoints protegidos para listar, criar, atualizar e deletar
 * históricos, garantindo que um usuário só possa manipular seus próprios dados
 * através da validação do token JWT em cada requisição.
 */
@RestController
@RequestMapping("/api/medicamentoHistoricos")
@Tag(name = "Uso de Medicamento Historico",
        description = "API para gerenciamento dos históricos de uso de mendicamentos do usuário autenticado.")
public class MedicamentoHistoricoController {

    private MedicamentoHistoricoService medicamentoHistoricoService;

    public MedicamentoHistoricoController(MedicamentoHistoricoService medicamentoHistoricoService) { this.medicamentoHistoricoService = medicamentoHistoricoService; }

    /**
     * Lista todos os históricos do usuário autenticado.
     * @param userDetails O principal do usuário autenticado, injetado pelo Spring Security.
     * @return Uma lista de históricos do usuário.
     */
    @GetMapping("/listar")
    @Operation(summary = "Listar meus Históricos de Uso de Medicamentos.",
            description = "Endpoint para listar todos os Históricos de Uso de Medicamentos do usuário logado.")
    public ResponseEntity<List<MedicamentoHistoricoDTOResponse>> listarMedicamentosHistoricos(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer usuarioId = userDetails.getUserId();
        return ResponseEntity.ok(medicamentoHistoricoService.listarMedicamentosHistoricos(usuarioId));
    }

    /**
     * Busca um histórico específico pelo ID, garantindo que pertença ao usuário autenticado.
     * @param medicamentoHistoricoId o ID do histórico a ser buscado.
     * @param userDetails O principal do usuário autenticado.
     * @return O histórico encontrado.
     */
    @GetMapping("/listarMedicamentoHistoricoPorId/{medicamentoHistoricoId}")
    @Operation(summary = "Listar o Histórico de Uso de Medicamento pelo ID dele.",
            description = "Endpoint para listar um Histórico de Uso de Medicamento específico do usuário logado.")
    public ResponseEntity<MedicamentoHistoricoDTOResponse> buscarMedicamentoHistoricoPorId(
            @PathVariable("medicamentoHistoricoId") Integer medicamentoHistoricoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Integer usuarioId = userDetails.getUserId();
        MedicamentoHistoricoDTOResponse medicamentoHistoricoDTOResponse = medicamentoHistoricoService.listarMedicamentoHistoricoPorId(
                medicamentoHistoricoId, usuarioId
        );

            return ResponseEntity.ok(medicamentoHistoricoDTOResponse);
    }

    /**
     * Cria um novo histórico para o usuário autenticado.
     * @param historicoDTORequest O corpo da requisição com os dados do novo histórico.
     * @param userDetails O principal do usuário autenticado.
     * @return O histórico recém-criado.
     */
    @PostMapping("/cadastrar")
    @Operation(summary = "Criar novo Histórico de Uso de Medicamento.",
            description = "Endpoint para criar um novo registro de Histórico de Uso de Medicamento para o usuário logado.")
    public ResponseEntity<MedicamentoHistoricoDTOResponse> cadastrarMedicamentoHistorico(
            @Valid @RequestBody MedicamentoHistoricoDTORequest historicoDTORequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Integer usuarioId = userDetails.getUserId();
        MedicamentoHistoricoDTOResponse medicamentoHistoricoDTOResponse = medicamentoHistoricoService.cadastrarMedicamentoHistorico(
                historicoDTORequest, usuarioId
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(medicamentoHistoricoDTOResponse);
    }

    /**
     * Atualiza um histórico existente do usuário autenticado.
     * @param medicamentoHistoricoId O ID do histórico a ser atualizado.
     * @param historicoAtualizarDTORequest O corpo da requisição com os dados para atualização.
     * @param userDetails O principal do usuário autenticado.
     * @return O histórico atualizado.
     */
    @PutMapping("/atualizar/{medicamentoHistoricoId}")
    @Operation(summary = "Atualizar todos os dados do Histórico de Uso de Medicamento.",
            description = "Endpoint para atualizar o registro do Histórico de Uso de Medicamento existente do usuário logado.")
    public ResponseEntity<MedicamentoHistoricoDTOResponse> atualizarMedicamentoHistoricoPorId(
            @PathVariable("medicamentoHistoricoId") Integer medicamentoHistoricoId,
            @RequestBody @Valid MedicamentoHistoricoAtualizarDTORequest historicoAtualizarDTORequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Integer usuarioId = userDetails.getUserId();
        MedicamentoHistoricoDTOResponse medicamentoHistoricoAtualizado = medicamentoHistoricoService.atualizarMedicamentoHistorico(
                medicamentoHistoricoId, usuarioId, historicoAtualizarDTORequest
        );
        return ResponseEntity.ok(medicamentoHistoricoAtualizado);
    }

    /**
     * Realiza a exclusão lógica de um histórico do usuário autenticado.
     * @param medicamentoHistoricoId O ID do histórico a ser deletado.
     * @param userDetails O principal do usuário autenticado.
     * @return Resposta sem conteúdo.
     */
    @DeleteMapping("/deletar/{medicamentoHistoricoId}")
    @Operation(summary = "Deletar todos os dados do Histórico de Uso de Medicamento.",
            description = "Endpoint para deletar o registro do Histórico de Uso de Medicamento do usuário logado.")
    public ResponseEntity<Void> deletarMedicamentoHistorico(
            @PathVariable("medicamentoHistoricoId") Integer medicamentoHistoricoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Integer usuarioId = userDetails.getUserId();
        medicamentoHistoricoService.deletarMedicamentoHistorico(medicamentoHistoricoId, usuarioId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retorna os dados do histórico do mês atual (JSON) para o App gerar o PDF.
     */
    @GetMapping("/relatorio-mensal-dados")
    public ResponseEntity<List<MedicamentoHistoricoDTOResponse>> buscarDadosRelatorioMensal(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer usuarioId = userDetails.getUserId();

        List<MedicamentoHistoricoDTOResponse> dados = medicamentoHistoricoService.listarDadosRelatorioMensal(usuarioId);

        return ResponseEntity.ok(dados);
    }

}
