package com.vitta.vittaBackend.controller;

import com.vitta.vittaBackend.dto.request.tratamento.TratamentoAtualizarDTORequest;
import com.vitta.vittaBackend.dto.request.tratamento.TratamentoDTORequest;
import com.vitta.vittaBackend.dto.response.tratamento.TratamentoDTOResponse;
import com.vitta.vittaBackend.security.UserDetailsImpl;
import com.vitta.vittaBackend.service.TratamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciar as operações de Tratamentos do usuário autenticado.
 * <p>
 * Expõe os endpoints protegidos para listar, criar, atualizar e deletar
 * tratamentos, garantindo que um usuário só possa manipular seus próprios dados
 * através da validação do token JWT em cada requisição.
 */
@RestController
@RequestMapping("/api/tratamentos")
@Tag(name = "Tratamento", description = "API para gerenciamento de tratamentos do usuário autenticado.")
public class TratamentoController {

    private TratamentoService tratamentoService;

    public TratamentoController(TratamentoService tratamentoService) { this.tratamentoService = tratamentoService; }

    /**
     * Lista todos os tratamentos do usuário autenticado.
     * @param userDetails O principal do usuário autenticado, injetado pelo Spring Security.
     * @return Uma lista de tratamentos do usuário.
     */
    @GetMapping("/listar")
    @Operation(summary = "Listar meus Tratamentos.",
            description = "Endpoint para listar todos os Tratamentos do usuário logado.")
    public ResponseEntity <List<TratamentoDTOResponse>> listarTratamentos(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer usuarioId = userDetails.getUserId();
        return ResponseEntity.ok(tratamentoService.listarTratamentos(usuarioId));
    }

    /**
     * Busca um tratamento específico pelo ID, garantindo que pertença ao usuário autenticado.
     * @param tratamentoId O ID do tratamento a ser buscado.
     * @param userDetails O principal do usuário autenticado.
     * @return O tratamento encontrado.
     */
    @GetMapping("/listarTratamentoPorId/{tratamentoId}")
    @Operation(summary = "Listar Tratamento pelo ID dele.",
            description = "Endpoint para listar um Tratamento específico do usuário logado.")
    public ResponseEntity<TratamentoDTOResponse> buscarTratamentoPorId(
            @PathVariable("tratamentoId") Integer tratamentoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Integer usuarioId = userDetails.getUserId();
        TratamentoDTOResponse tratamentoDTOResponse = tratamentoService.listarTratamentoPorId(tratamentoId, usuarioId);

            return ResponseEntity.ok(tratamentoDTOResponse);
    }

    /**
     * Cria um novo tratamento para o usuário autenticado.
     * @param tratamentoDTORequest O corpo da requisição com os dados do novo tratamento.
     * @param userDetails O principal do usuário autenticado.
     * @return O tratamento recém-criado.
     */
    @PostMapping("/cadastrar")
    @Operation(summary = "Criar novo Tratamento.",
            description = "Endpoint para criar um novo registro de Tratamento para o usuário logado.")
    public ResponseEntity<TratamentoDTOResponse> cadastrarTratamento(
            @Valid @RequestBody TratamentoDTORequest tratamentoDTORequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Integer usuarioId = userDetails.getUserId();
        TratamentoDTOResponse tratamentoDTOResponse = tratamentoService.cadastrarTratamento(tratamentoDTORequest, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(tratamentoDTOResponse);
    }

    /**
     * Atualiza um tratamento existente do usuário autenticado.
     * @param tratamentoId O ID do tratamento a ser atualizado.
     * @param tratamentoAtualizarDTORequest O corpo da requisição com os dados para atualização.
     * @param userDetails O principal do usuário autenticado.
     * @return O tratamento atualizado.
     */
    @PutMapping("/atualizar/{tratamentoId}")
    @Operation(summary = "Atualizar todos os dados do Tratamento.",
            description = "Endpoint para atualizar o registro do Tratamento, pelo ID.")
    public ResponseEntity<TratamentoDTOResponse> atualizarTratamentoPorId(
            @PathVariable("tratamentoId") Integer tratamentoId,
            @RequestBody @Valid TratamentoAtualizarDTORequest tratamentoAtualizarDTORequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Integer usuarioId = userDetails.getUserId();
        TratamentoDTOResponse tratamentoAtualizado = tratamentoService.atualizarTratamento(tratamentoId, usuarioId, tratamentoAtualizarDTORequest);
        return ResponseEntity.ok(tratamentoAtualizado);
    }

    /**
     * Realiza a exclusão lógica de um tratamento do usuário autenticado.
     * @param tratamentoId O ID do tratamento a ser deletado.
     * @param userDetails O principal do usuário autenticado.
     * @return Resposta sem conteúdo.
     */
    @DeleteMapping("/deletar/{tratamentoId}")
    @Operation(summary = "Deletar todos os dados do Tratamento.",
            description = "Endpoint para deletar o registro do Tratamento, pelo ID.")
    public ResponseEntity<Void> deletarTratamento(
            @PathVariable("tratamentoId") Integer tratamentoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Integer usuarioId = userDetails.getUserId();
        tratamentoService.deletarLogico(tratamentoId, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/encerrar/{tratamentoId}")
    @Operation(summary = "Encerrar um Tratamento.",
            description = "Endpoint para encerrar um Tratamento, pelo ID.")
    public ResponseEntity<Void> encerrarTratamento(
            @PathVariable("tratamentoId") Integer tratamentoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Integer usuarioId = userDetails.getUserId();
        tratamentoService.encerrarTratamento(tratamentoId, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
