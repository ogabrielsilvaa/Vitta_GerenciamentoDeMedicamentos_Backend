package com.vitta.vittaBackend.dto.request.tratamento;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TratamentoAtualizarDTORequest {

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

    /**
     * Valida se a data de término não é anterior à data de início, caso ambas sejam fornecidas.
     */
    @JsonIgnore
    public boolean isDatasValidas() {

        // isso aki obrigado que 2 datas estejam preenchidas
        if (dataDeInicio != null && dataDeTermino != null) {
            return !dataDeTermino.isBefore(dataDeInicio);
        }

        return true; // se nenhuma data for fornecida, a validação não se aplica.
    }

    /**
     * Método de validação customizada para garantir a consistência dos dados de frequência.
     * Esta validação é acionada pela anotação {@code @AssertTrue} e verifica a seguinte regra:
     * Se o campo {@code tipoDeFrequencia} for fornecido na requisição de atualização, então o campo
     * dependente correspondente ({@code intervaloEmHoras} ou {@code horariosEspecificos}) também
     * deve ser fornecido e válido.
     *
     * @return {@code true} se os dados de frequência forem consistentes ou se não forem fornecidos,
     * {@code false} caso contrário, o que irá acionar um erro de validação.
     */
    @JsonIgnore
    @AssertTrue(message = "A informação de frequência (intervalo ou horários) é inconsistente para o tipo fornecido.")
    public boolean isFrequenciaConsistente() {
        if (tipoDeFrequencia == null) {
            return true;
        }

        if (tipoDeFrequencia == 1) {
            return intervaloEmHoras != null && intervaloEmHoras > 0;
        }

        if (tipoDeFrequencia == 2) {
            return horariosEspecificos != null && !horariosEspecificos.trim().isEmpty();
        }

        return false;
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
