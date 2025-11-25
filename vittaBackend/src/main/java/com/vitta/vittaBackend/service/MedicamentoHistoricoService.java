package com.vitta.vittaBackend.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.vitta.vittaBackend.dto.request.medicamentoHistorico.MedicamentoHistoricoDTORequest;
import com.vitta.vittaBackend.dto.request.medicamentoHistorico.MedicamentoHistoricoAtualizarDTORequest;
import com.vitta.vittaBackend.dto.response.medicamentoHistorico.MedicamentoHistoricoDTOResponse;
import com.vitta.vittaBackend.entity.Agendamento;
import com.vitta.vittaBackend.entity.MedicamentoHistorico;
import com.vitta.vittaBackend.entity.Usuario;
import com.vitta.vittaBackend.repository.AgendamentoRepository;
import com.vitta.vittaBackend.repository.MedicamentoHistoricoRepository;
import com.vitta.vittaBackend.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.awt.*;
import com.lowagie.text.Font;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class MedicamentoHistoricoService {

    private final MedicamentoHistoricoRepository medicamentoHistoricoRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

    public MedicamentoHistoricoService(
            MedicamentoHistoricoRepository medicamentoHistoricoRepository,
            AgendamentoRepository agendamentoRepository,
            UsuarioRepository usuarioRepository,
            ModelMapper modelMapper
    ) {
        this.medicamentoHistoricoRepository = medicamentoHistoricoRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Lista o histórico de uso de medicamentos de um usuário específico.
     * @param usuarioId O ID do usuário autenticado.
     * @return Uma lista de MedicamentoHistoricoDTOResponse.
     */
    public List<MedicamentoHistoricoDTOResponse> listarMedicamentosHistoricos(Integer usuarioId) {
        return this.medicamentoHistoricoRepository.listarMedicamentosHistoricos(usuarioId)
                .stream()
                .map(MedicamentoHistoricoDTOResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Busca um único registro de histórico pelo seu ID, garantindo que ele pertença ao usuário.
     * @param historicoId O ID do registro de histórico a ser buscado.
     * @param usuarioId O ID do usuário autenticado.
     * @return O MedicamentoHistoricoDTOResponse correspondente.
     */
    public MedicamentoHistoricoDTOResponse listarMedicamentoHistoricoPorId(Integer historicoId, Integer usuarioId) {
        MedicamentoHistorico medicamentoHistorico = validarMedicamentoHistorico(historicoId, usuarioId);
        return new MedicamentoHistoricoDTOResponse(medicamentoHistorico);
    }

    /**
     * Cadastra um novo histórico para o usuário autenticado.
     * O ID do usuário é obtido do contexto de segurança, não do DTO.
     * @param medicamentoHistoricoDTORequest DTO com os dados do novo agendamento.
     * @param usuarioId O ID do usuário autenticado.
     * @return O AgendamentoDTOResponse do novo agendamento.
     */
    @Transactional
    public MedicamentoHistoricoDTOResponse cadastrarMedicamentoHistorico(MedicamentoHistoricoDTORequest medicamentoHistoricoDTORequest, Integer usuarioId) {

        Agendamento agendamento = agendamentoRepository.listarAgendamentoPorId(
                medicamentoHistoricoDTORequest.getAgendamentoId(),
                usuarioId
        );
        if (agendamento == null) {
            throw new EntityNotFoundException("Agendamento não encontrado ou não pertence a este usuário.");
        }

        Usuario usuario = usuarioRepository.getReferenceById(usuarioId);

        MedicamentoHistorico medicamentoHistorico = new MedicamentoHistorico();
        medicamentoHistorico.setHoraDoUso(medicamentoHistoricoDTORequest.getHoraDoUso());
        medicamentoHistorico.setDoseTomada(medicamentoHistoricoDTORequest.getDoseTomada());
        medicamentoHistorico.setObservacao(medicamentoHistoricoDTORequest.getObservacao());

        medicamentoHistorico.setAgendamento(agendamento);
        medicamentoHistorico.setUsuario(usuario);

        MedicamentoHistorico medicamentoHistoricoSalvo = medicamentoHistoricoRepository.save(medicamentoHistorico);
        return new MedicamentoHistoricoDTOResponse(medicamentoHistoricoSalvo);
    }

    /**
     * Atualiza um registro de histórico existente, garantindo que ele pertença ao usuário.
     * @param historicoId O ID do registro de histórico a ser atualizado.
     * @param usuarioId O ID do usuário autenticado.
     * @param historicoDTORequestAtualizar DTO com os dados de atualização.
     * @return O MedicamentoHistoricoDTOResponse atualizado.
     */
    @Transactional
    public MedicamentoHistoricoDTOResponse atualizarMedicamentoHistorico(Integer historicoId, Integer usuarioId, MedicamentoHistoricoAtualizarDTORequest historicoDTORequestAtualizar) {
        MedicamentoHistorico medicamentoHistoricoExistente = validarMedicamentoHistorico(historicoId, usuarioId);

        if (historicoDTORequestAtualizar.getHoraDoUso() != null) {
            medicamentoHistoricoExistente.setHoraDoUso(historicoDTORequestAtualizar.getHoraDoUso());
        }
        if (historicoDTORequestAtualizar.getDoseTomada() != null) {
            medicamentoHistoricoExistente.setDoseTomada(historicoDTORequestAtualizar.getDoseTomada());
        }
        if (historicoDTORequestAtualizar.getObservacao() != null) {
            medicamentoHistoricoExistente.setObservacao(historicoDTORequestAtualizar.getObservacao());
        }

        MedicamentoHistorico medicamentoHistoricoAtualizado = medicamentoHistoricoRepository.save(medicamentoHistoricoExistente);
        return new MedicamentoHistoricoDTOResponse(medicamentoHistoricoAtualizado);
    }

    /**
     * Realiza a exclusão lógica de um registro de histórico, garantindo que ele pertença ao usuário.
     * @param historicoId O ID do registro a ser deletado.
     * @param usuarioId O ID do usuário autenticado.
     */
    @Transactional
    public void deletarMedicamentoHistorico(Integer historicoId, Integer usuarioId) { medicamentoHistoricoRepository.apagarLogicoMedicamentoHistorico(historicoId, usuarioId); }



    /**
     * Valida a existência de um registro de histórico e a sua posse pelo usuário especificado.
     * <p>
     * Este é um método auxiliar privado que busca um registro de histórico no repositório
     * usando tanto o ID do histórico quanto o ID do usuário. Ele serve como uma
     * verificação de segurança e existência antes de operações como atualização ou exclusão.
     *
     * @param medicamentoHistoricoId O ID do registro de histórico a ser validado e buscado.
     * @param usuarioId O ID do usuário que deve ser o proprietário do histórico.
     * @return A entidade {@link MedicamentoHistorico} encontrada, caso seja válida e pertença ao usuário.
     * @throws RuntimeException se o registro de histórico não for encontrado ou não pertencer ao usuário.
     */
    private MedicamentoHistorico validarMedicamentoHistorico(Integer medicamentoHistoricoId, Integer usuarioId) {
        MedicamentoHistorico medicamentoHistorico = medicamentoHistoricoRepository.listarMedicamentoHistoricoPorId(medicamentoHistoricoId, usuarioId);
        if (medicamentoHistorico == null) {
            throw new RuntimeException("Histórico do medicamento não encontrado ou inativo.");
        }
        return medicamentoHistorico;
    }

    /**
     * Gera um relatório em formato PDF contendo o histórico de medicamentos tomados pelo usuário no mês corrente.
     * <p>
     * Este método orquestra a criação do documento PDF realizando as seguintes etapas:
     * <ul>
     * <li>Calcula automaticamente o início (dia 1 às 00:00) e o fim (último dia às 23:59:59) do mês atual.</li>
     * <li>Busca os registros no banco de dados filtrando por esse período.</li>
     * <li>Configura o documento PDF (tamanho A4) e adiciona um título com o mês e ano de referência.</li>
     * <li>Caso a lista esteja vazia, adiciona uma mensagem informativa de "Nenhum registro".</li>
     * <li>Caso existam dados, constrói uma tabela de 5 colunas (Tratamento, Medicamento, Dose, Data/Hora, Observação) estilizada.</li>
     * </ul>
     *
     * @param usuarioId O ID do usuário solicitante do relatório.
     * @return Um array de bytes ({@code byte[]}) representando o arquivo PDF gerado, pronto para download ou envio.
     * @throws RuntimeException Se ocorrer qualquer erro durante a manipulação do documento ou escrita do PDF (ex: erro de I/O).
     */
    public byte[] gerarRelatorioPdf(Integer usuarioId) {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioMes = hoje.withDayOfMonth(1).atStartOfDay();
        LocalDateTime fimMes = hoje.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);

        List<MedicamentoHistorico> historicos = medicamentoHistoricoRepository.listarHistoricoPorPeriodo(usuarioId, inicioMes, fimMes);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            String nomeMes = hoje.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
            String tituloTexto = String.format("Relatório de Medicamentos - %s/%d", nomeMes.substring(0, 1).toUpperCase() + nomeMes.substring(1), hoje.getYear());

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            Paragraph titulo = new Paragraph(tituloTexto, fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(Chunk.NEWLINE);

            if (historicos.isEmpty()) {
                Font fontAviso = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.GRAY);
                Paragraph aviso = new Paragraph("Nenhum medicamento foi registrado como tomado neste mês.", fontAviso);
                aviso.setAlignment(Element.ALIGN_CENTER);
                aviso.setSpacingBefore(20f);
                document.add(aviso);

                document.close();
                return out.toByteArray();
            }

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.5f, 2.5f, 1.0f, 1.5f, 2.5f});

            String[] headers = {"Tratamento", "Medicamento", "Dose", "Data/Hora", "Observação"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE)));
                cell.setBackgroundColor(new Color(28, 189, 207));
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
            Font fontDados = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

            for (MedicamentoHistorico hist : historicos) {
                String nomeTratamento = "-";
                String nomeMedicamento = "Medicamento Removido";

                if(hist.getAgendamento() != null && hist.getAgendamento().getTratamento() != null) {
                    nomeTratamento = hist.getAgendamento().getTratamento().getNome();
                    nomeMedicamento = hist.getAgendamento().getTratamento().getMedicamento().getNome();
                }

                table.addCell(new Phrase(nomeTratamento, fontDados));
                table.addCell(new Phrase(nomeMedicamento, fontDados));
                table.addCell(new Phrase(String.valueOf(hist.getDoseTomada()), fontDados));
                table.addCell(new Phrase(hist.getHoraDoUso().format(formatter), fontDados));
                table.addCell(new Phrase(hist.getObservacao() != null ? hist.getObservacao() : "-", fontDados));
            }

            document.add(table);
            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage());
        }
    }

    /**
     * Busca os dados históricos do mês atual e retorna como uma lista de DTOs.
     * Ideal para gerar relatórios no frontend ou exibir gráficos.
     *
     * @param usuarioId O ID do usuário autenticado.
     * @return Lista de MedicamentoHistoricoDTOResponse filtrada pelo mês corrente.
     */
    public List<MedicamentoHistoricoDTOResponse> listarDadosRelatorioMensal(Integer usuarioId) {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioMes = hoje.withDayOfMonth(1).atStartOfDay();
        LocalDateTime fimMes = hoje.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);

        List<MedicamentoHistorico> historicos = medicamentoHistoricoRepository.listarHistoricoPorPeriodo(
                usuarioId,
                inicioMes,
                fimMes
        );

        return historicos.stream()
                .map(MedicamentoHistoricoDTOResponse::new)
                .collect(Collectors.toList());
    }

}
