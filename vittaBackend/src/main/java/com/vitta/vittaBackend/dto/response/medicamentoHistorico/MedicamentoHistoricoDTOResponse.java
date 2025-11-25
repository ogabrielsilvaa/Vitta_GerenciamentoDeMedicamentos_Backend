package com.vitta.vittaBackend.dto.response.medicamentoHistorico;

import com.vitta.vittaBackend.dto.response.agendamento.AgendamentoResumoDTOResponse;
import com.vitta.vittaBackend.dto.response.medicamento.MedicamentoResumoDTOResponse;
import com.vitta.vittaBackend.dto.response.tratamento.TratamentoResumoDTOResponse;
import com.vitta.vittaBackend.entity.MedicamentoHistorico;
import com.vitta.vittaBackend.enums.GeralStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MedicamentoHistoricoDTOResponse {
    private Integer id;
    private LocalDateTime horaDoUso;
    private BigDecimal doseTomada;
    private String observacao;
    private GeralStatus historicoStatus;
    private TratamentoResumoDTOResponse tratamento;
    private AgendamentoResumoDTOResponse agendamento;
    private MedicamentoResumoDTOResponse medicamento;

    private String nomeTratamento;
    private String nomeMedicamento;

    public MedicamentoHistoricoDTOResponse() {
    }

    public MedicamentoHistoricoDTOResponse(MedicamentoHistorico historicoEntity) {
        this.id = historicoEntity.getId();
        this.horaDoUso = historicoEntity.getHoraDoUso();
        this.doseTomada = historicoEntity.getDoseTomada();
        this.observacao = historicoEntity.getObservacao();
        this.historicoStatus = historicoEntity.getHistoricoStatus();

        if (historicoEntity.getAgendamento() != null) {
            this.agendamento = new AgendamentoResumoDTOResponse(historicoEntity.getAgendamento());

            if (historicoEntity.getAgendamento().getTratamento() != null) {
                this.tratamento = new TratamentoResumoDTOResponse(historicoEntity.getAgendamento().getTratamento());

                if (historicoEntity.getAgendamento().getTratamento().getMedicamento() != null) {
                    this.medicamento = new MedicamentoResumoDTOResponse(historicoEntity.getAgendamento().getTratamento().getMedicamento());
                }
            }
        }

        if (historicoEntity.getAgendamento() != null && historicoEntity.getAgendamento().getTratamento() != null) {
            this.nomeTratamento = historicoEntity.getAgendamento().getTratamento().getNome();

            if (historicoEntity.getAgendamento().getTratamento().getMedicamento() != null) {
                this.nomeMedicamento = historicoEntity.getAgendamento().getTratamento().getMedicamento().getNome();
            } else {
                this.nomeMedicamento = "Medicamento Excluído";
            }
        } else {
            this.nomeTratamento = "Tratamento Excluído";
            this.nomeMedicamento = "-";
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getHoraDoUso() {
        return horaDoUso;
    }

    public void setHoraDoUso(LocalDateTime horaDoUso) {
        this.horaDoUso = horaDoUso;
    }

    public BigDecimal getDoseTomada() {
        return doseTomada;
    }

    public void setDoseTomada(BigDecimal doseTomada) {
        this.doseTomada = doseTomada;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public GeralStatus getHistoricoStatus() {
        return historicoStatus;
    }

    public void setHistoricoStatus(GeralStatus historicoStatus) {
        this.historicoStatus = historicoStatus;
    }

    public TratamentoResumoDTOResponse getTratamento() {
        return tratamento;
    }

    public void setTratamento(TratamentoResumoDTOResponse tratamento) {
        this.tratamento = tratamento;
    }

    public AgendamentoResumoDTOResponse getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(AgendamentoResumoDTOResponse agendamento) {
        this.agendamento = agendamento;
    }

    public MedicamentoResumoDTOResponse getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(MedicamentoResumoDTOResponse medicamento) {
        this.medicamento = medicamento;
    }

    public String getNomeTratamento() {
        return nomeTratamento;
    }

    public void setNomeTratamento(String nomeTratamento) {
        this.nomeTratamento = nomeTratamento;
    }

    public String getNomeMedicamento() {
        return nomeMedicamento;
    }

    public void setNomeMedicamento(String nomeMedicamento) {
        this.nomeMedicamento = nomeMedicamento;
    }
}
