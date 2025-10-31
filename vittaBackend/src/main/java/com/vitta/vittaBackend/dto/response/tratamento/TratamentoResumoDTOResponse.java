package com.vitta.vittaBackend.dto.response.tratamento;

import com.vitta.vittaBackend.dto.response.medicamento.MedicamentoResumoDTOResponse;
import com.vitta.vittaBackend.entity.Tratamento;
import com.vitta.vittaBackend.enums.tratamento.TratamentoStatus;

import java.time.LocalDate;

/**
 * DTO para representar uma visão simplificada de um Tratamento.
 * Ideal para ser aninhado dentro da resposta de um Usuário.
 */
public class TratamentoResumoDTOResponse {

    /**
     * O ID único do tratamento.
     */
    private Integer id;

    /**
     * Nome do tratamento.
     */
    private String nome;

    /**
     * A data em que o tratamento se inicia.
     */
    private LocalDate dataDeInicio;

    /**
     * A data em que o tratamento termina.
     */
    private LocalDate dataDeTermino;

    /**
     * O status atual do tratamento (ex: ATIVO, CONCLUIDO).
     */
    private TratamentoStatus status;

    /**
     * Objeto resumido do medicamento associado a este tratamento.
     */
    private MedicamentoResumoDTOResponse medicamento;

    /**
     * Construtor de mapeamento a partir da entidade Tratamento.
     * @param tratamentoEntity A entidade vinda do banco de dados.
     */
    public TratamentoResumoDTOResponse(Tratamento tratamentoEntity) {
        this.id = tratamentoEntity.getId();
        this.nome = tratamentoEntity.getNome();
        this.dataDeInicio = tratamentoEntity.getDataDeInicio();
        this.dataDeTermino = tratamentoEntity.getDataDeTermino();
        this.status = tratamentoEntity.getStatus();
        if (tratamentoEntity.getMedicamento() != null) {
            this.medicamento = new MedicamentoResumoDTOResponse(tratamentoEntity.getMedicamento());
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataDeInicio() {
        return dataDeInicio;
    }

    public void setDataDeInicio(LocalDate dataDeInicio) {
        this.dataDeInicio = dataDeInicio;
    }

    public LocalDate getDataDeTermino() {
        return dataDeTermino;
    }

    public void setDataDeTermino(LocalDate dataDeTermino) {
        this.dataDeTermino = dataDeTermino;
    }

    public TratamentoStatus getStatus() {
        return status;
    }

    public void setStatus(TratamentoStatus status) {
        this.status = status;
    }

    public MedicamentoResumoDTOResponse getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(MedicamentoResumoDTOResponse medicamento) {
        this.medicamento = medicamento;
    }
}
