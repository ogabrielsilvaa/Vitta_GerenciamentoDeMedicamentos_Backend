package com.vitta.vittaBackend.dto.request.tratamento;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para receber os dados necessários para a criação de um novo Tratamento.
 * Representa o corpo da requisição para o endpoint de cadastro de tratamento.
 */
public class TratamentoDTORequest {

    /**
     * ID do medicamento que fará parte deste tratamento.
     * Deve corresponder a um medicamento existente no catálogo.
     */
    private Integer medicamentoId;

    /**
     * Nome do tratamento, para facilitar a identificação de cada um.
     */
    private String nome;

    /**
     * Dosagem a ser administrada em cada toma (ex: 1.00 para um comprimido).
     */
    private BigDecimal dosagem;

    /**
     * Instruções de uso em formato de texto livre.
     */
    private String instrucoes;

    /**
     * Data em que o tratamento se inicia. Não pode ser uma data no passado.
     */
    private LocalDate dataDeInicio;

    /**
     * Data em que o tratamento termina.
     */
    private LocalDate dataDeTermino;

    /**
     * Define o tipo de regra de frequência do tratamento.
     * Usar 1 para INTERVALO_HORAS ou 2 para HORARIOS_ESPECIFICOS.
     */
    private Integer tipoDeFrequencia;

    /**
     * Intervalo em horas entre as doses.
     * Obrigatório e deve ser maior que zero se o tipoDeFrequencia for 1 (INTERVALO_HORAS).
     */
    private Integer intervaloEmHoras;

    /**
     * Lista de horários específicos separados por vírgula (ex: "08:00,16:00,23:00").
     * Obrigatório e não pode ser vazio se o tipoDeFrequencia for 2 (HORARIOS_ESPECIFICOS).
     */
    private String horariosEspecificos;

    /**
     * Código numérico que define o tipo de alerta (Enum) para os agendamentos.
     * Este campo no DTO é para registrar a informação e poder gerar os agendamentos,
     * a partir do tratamento criado.
     */
    private Integer tipoDeAlerta;

    /**
     * Validação customizada que verifica a consistência entre o tipo de frequência
     * e os campos de intervalo/horários.
     */
    @JsonIgnore
    public boolean isFrequenciaValida() {
        if (tipoDeFrequencia == null) {
            return true;
        }

        // se for por intervalo, o campo de intervalo deve ser preenchido
        if (tipoDeFrequencia == 1) {
            return intervaloEmHoras != null && intervaloEmHoras > 0;
        }

        // se for por horários, o campo de horários deve ser preenchido
        if (tipoDeFrequencia == 2) {
            return horariosEspecificos != null && !horariosEspecificos.trim().isEmpty();
        }

        return false;
    }

    public Integer getMedicamentoId() {
        return medicamentoId;
    }

    public void setMedicamentoId(Integer medicamentoId) {
        this.medicamentoId = medicamentoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getDosagem() {
        return dosagem;
    }

    public void setDosagem(BigDecimal dosagem) {
        this.dosagem = dosagem;
    }

    public String getInstrucoes() {
        return instrucoes;
    }

    public void setInstrucoes(String instrucoes) {
        this.instrucoes = instrucoes;
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

    public Integer getTipoDeFrequencia() {
        return tipoDeFrequencia;
    }

    public void setTipoDeFrequencia(Integer tipoDeFrequencia) {
        this.tipoDeFrequencia = tipoDeFrequencia;
    }

    public Integer getIntervaloEmHoras() {
        return intervaloEmHoras;
    }

    public void setIntervaloEmHoras(Integer intervaloEmHoras) {
        this.intervaloEmHoras = intervaloEmHoras;
    }

    public String getHorariosEspecificos() {
        return horariosEspecificos;
    }

    public void setHorariosEspecificos(String horariosEspecificos) {
        this.horariosEspecificos = horariosEspecificos;
    }

    public Integer getTipoDeAlerta() {
        return tipoDeAlerta;
    }

    public void setTipoDeAlerta(Integer tipoDeAlerta) {
        this.tipoDeAlerta = tipoDeAlerta;
    }
}
