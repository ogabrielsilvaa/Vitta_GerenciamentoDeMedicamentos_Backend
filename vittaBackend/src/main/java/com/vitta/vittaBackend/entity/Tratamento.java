package com.vitta.vittaBackend.entity;

import com.vitta.vittaBackend.enums.TipoFrequencia;
import com.vitta.vittaBackend.enums.tratamento.TratamentoStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Entidade central que representa um tratamento para um usuário.
 * Contém todas as regras de uma prescrição, como dosagem, frequência e duração,
 * e serve como base para a geração dos agendamentos individuais.
 */
@Entity
@Table(name = "tratamento")
public class Tratamento {

    /**
     * Identificador único do tratamento, gerado automaticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tratamento_id")
    private Integer id;

    /**
     * Nome do tratamento, para facilitar a identificação de cada um.
     */
    @Column(name = "tratamento_nome")
    private String nome;

    /**
     * A dosagem a ser administrada em cada toma (ex: 1.00 para um comprimido, 10.50 para 10.5 ml).
     */
    @Column(name = "tratamento_dosagem")
    private BigDecimal dosagem;

    /**
     * Instruções de uso em formato de texto livre (ex: "Tomar com um copo de água, após as refeições").
     */
    @Column(name = "tratamento_instrucoes")
    private String instrucoes;

    /**
     * Data em que o tratamento se inicia.
     */

    @Column(name = "tratamento_data_de_inicio")
    private LocalDate dataDeInicio;

    /**
     * Data em que o tratamento termina.
     */
    @Column(name = "tratamento_data_de_termino")
    private LocalDate dataDeTermino;

    /**
     * Define o tipo de regra para a frequência das doses.
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "tratamento_tipo_de_frequencia")
    private TipoFrequencia tipoDeFrequencia;

    /**
     * Intervalo em horas entre as doses.
     * Este campo só é utilizado se o tipoDeFrequencia for {@link TipoFrequencia#INTERVALO_HORAS}.
     */
    @Column(name = "tratamento_intervalo_em_horas")
    private Integer intervaloEmHoras;

    /**
     * String contendo os horários específicos para as tomas, separados por vírgula (ex: "08:00,16:00,23:00").
     * Este campo só é utilizado se o tipoDeFrequencia for {@link TipoFrequencia#HORARIOS_ESPECIFICOS}.
     */
    @Column(name = "tratamento_horarios_especificos")
    private String horariosEspecificos;

    /**
     * Status atual do tratamento (ex: ATIVO, CONCLUIDO, CANCELADO).
     * O valor padrão para um novo tratamento é ATIVO.
     */
    @Column(name = "tratamento_status")
    private TratamentoStatus status = TratamentoStatus.ATIVO;

    /**
     * O usuário ao qual este tratamento pertence.
     * Relacionamento Muitos-para-Um com a entidade Usuario.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "usuario_id")
    private Usuario usuario;

    /**
     * O medicamento que faz parte deste tratamento.
     * Relacionamento Muitos-para-Um com a entidade Medicamento.
     */
    @ManyToOne
    @JoinColumn(name = "medicamento_id", referencedColumnName = "medicamento_id")
    private Medicamento medicamento;

    /**
     * A lista de todos os agendamentos individuais que foram gerados a partir deste tratamento.
     */
    @OneToMany(mappedBy = "tratamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Agendamento> agendamentos;

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

    public TipoFrequencia getTipoDeFrequencia() {
        return tipoDeFrequencia;
    }

    public void setTipoDeFrequencia(TipoFrequencia tipoDeFrequencia) {
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

    public TratamentoStatus getStatus() {
        return status;
    }

    public void setStatus(TratamentoStatus status) {
        this.status = status;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    public List<Agendamento> getAgendamentos() {
        return agendamentos;
    }

    public void setAgendamentos(List<Agendamento> agendamentos) {
        this.agendamentos = agendamentos;
    }
}
