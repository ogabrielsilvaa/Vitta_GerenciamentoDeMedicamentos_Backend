import {
  concluirAgendamento,
  formatarData,
  listarAgendamentos,
} from "@/src/api/agendamentoApi";
import { gerarRelatorioPDF } from "@/src/api/medicamentoHistoricoApi";
import { ActionButton } from "@/src/components/actionButton/actionButton";
import Calendario from "@/src/components/agendaCalendar";
import { AuthContext, useSession } from "@/src/contexts/authContext";
import { AgendamentoStatus } from "@/src/enums/agendamento/agendamentoStatus";
import { AgendamentoResponse } from "@/src/types/agendamentoTypes";
import { MaterialCommunityIcons } from "@expo/vector-icons";
import { endOfMonth, format, isWithinInterval, startOfMonth } from "date-fns";
import { ptBR } from "date-fns/locale";
import { useFocusEffect } from "expo-router";
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import {
  ActivityIndicator,
  Alert,
  FlatList,
  Modal,
  Pressable,
  Text,
  TextInput,
  View,
} from "react-native";
import {
  SafeAreaView,
  useSafeAreaInsets,
} from "react-native-safe-area-context";
import {
  agendarLembreteMedicamento,
  cancelarTodasNotificacoes,
  registrarNotificacoesAsync
} from "../../services/notificacaoService";
import { styles } from "./styles";

const parseDateSafe = (dateInput: any): Date => {
  try {
    if (!dateInput) return new Date();

    const d = new Date(dateInput);

    if (isNaN(d.getTime())) return new Date();

    return d;
  } catch (e) {
    return new Date();
  }
};

export default function HomeScreen() {
  const { signOut, session } = useSession();

  const [agendamentos, setAgendamentos] = useState<AgendamentoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [agendamentosDoMes, setAgendamentosDoMes] = useState<
    AgendamentoResponse[]
  >([]);
  const [diaSelecionado, setDiaSelecionado] = useState<string | null>(null);
  const [mesVisivel, setMesVisivel] = useState(new Date());

  const [modalObsVisivel, setModalObsVisivel] = useState(false);
  const [agendamentoParaConcluir, setAgendamentoParaConcluir] =
    useState<AgendamentoResponse | null>(null);
  const [textoObservacao, setTextoObservacao] = useState("");
  const [loadingConclusao, setLoadingConclusao] = useState(false);

  const insets = useSafeAreaInsets();

  const { usuario } = useContext(AuthContext);

  const carregarAgendamentosDoMes = useCallback(async () => {
    if (session) {
      try {
        setError(null);

        const inicioDoMes = startOfMonth(mesVisivel);
        const fimDoMes = endOfMonth(mesVisivel);

        const dataInicio = formatarData(inicioDoMes);
        const dataFim = formatarData(fimDoMes);

        console.log("--- RECARREGANDO LISTA E NOTIFICAÇÕES ---");

        const data = await listarAgendamentos(session, dataInicio, dataFim);
        setAgendamentosDoMes(data);

        await cancelarTodasNotificacoes();

        const agora = new Date();
        
        data.forEach(async (ag) => {
            const dataAgendamento = parseDateSafe(ag.horarioDoAgendamento);
            
            if (String(ag.status) === AgendamentoStatus.PENDENTE && dataAgendamento > agora) {
                
                const nomeMed = ag.tratamento?.medicamento?.nome || "Medicamento";
                
                const doseValor = ag.tratamento?.dosagem; 
                
                const instrucoes = ag.tratamento?.instrucoes || "";

                let corpoMensagem = `Tomar ${nomeMed}`;
                
                if (doseValor) {
                    corpoMensagem += `. Dose: ${doseValor}`;
                }
                
                if (instrucoes) {
                    corpoMensagem += `. ${instrucoes}`;
                }

                await agendarLembreteMedicamento(
                    "Hora do Remédio! 💊",
                    corpoMensagem,
                    dataAgendamento
                );
            }
        });

      } catch (error) {
        setError("Falha ao carregar agendamentos.");
        console.error(error);
      } finally {
        setLoading(false);
      }
    } else if (!session) {
      setLoading(false);
      setError("Usuário não autenticado.");
    }
  }, [session, mesVisivel]);

  useEffect(() => {
    if (!session) {
      return;
    }

    registrarNotificacoesAsync();
  }, [session]);

  useFocusEffect(
    useCallback(() => {
      carregarAgendamentosDoMes();
    }, [carregarAgendamentosDoMes])
  );

  const agendamentosParaExibir = useMemo(() => {
    const pendentes = agendamentosDoMes.filter(
      (ag) => ag.status === AgendamentoStatus.PENDENTE
    );

    if (diaSelecionado) {
      return pendentes.filter(
        (ag) =>
          format(new Date(ag.horarioDoAgendamento), "yyyy-MM-dd") ===
          diaSelecionado
      );
    }

    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    const dataFim = new Date();
    dataFim.setDate(hoje.getDate() + 4);
    dataFim.setHours(23, 59, 59, 999);

    return pendentes.filter((ag) =>
      isWithinInterval(new Date(ag.horarioDoAgendamento), {
        start: hoje,
        end: dataFim,
      })
    );
  }, [diaSelecionado, agendamentosDoMes]);

  const handleDiaPressionado = useCallback(
    (dia: string) => {
      if (dia === diaSelecionado) {
        setDiaSelecionado(null);
      } else {
        setDiaSelecionado(dia);
      }
    },
    [diaSelecionado]
  );

  const handleMesMudou = useCallback((novoMes: Date) => {
    setMesVisivel(novoMes);
    setDiaSelecionado(null);
  }, []);

  const handleAbrirModalConclusao = (agendamento: AgendamentoResponse) => {
    setAgendamentoParaConcluir(agendamento);
    setTextoObservacao("");
    setModalObsVisivel(true);
  };

  const confirmarConclusao = async () => {
    if (!session || !agendamentoParaConcluir) return;

    setLoadingConclusao(true);
    try {
      setAgendamentosDoMes((prev) =>
        prev.map((ag) =>
          ag.id === agendamentoParaConcluir.id
            ? { ...ag, status: AgendamentoStatus.TOMADO }
            : ag
        )
      );

      const dados = {
        horaDoUso: new Date().toISOString(),
        doseTomada: Number(agendamentoParaConcluir.tratamento?.dosagem) || 1,
        observacao: textoObservacao.trim() || "Sem observações.",
      };

      await concluirAgendamento(session, agendamentoParaConcluir.id, dados);

      setModalObsVisivel(false);
      setAgendamentoParaConcluir(null);
      Alert.alert("Sucesso", "Medicamento registrado!");
    } catch (error: any) {
      Alert.alert("Erro", "Falha ao registrar.");
      carregarAgendamentosDoMes();
    } finally {
      setLoadingConclusao(false);
    }
  };

  const handleExportarPDF = async () => {
    if (!session) return;
    await gerarRelatorioPDF(session);
  };

  if (loading) {
    return (
      <SafeAreaView style={styles.container}>
        <ActivityIndicator size="large" color="#0000ff" />
        <Text>Carregando...</Text>
      </SafeAreaView>
    );
  }

  if (error) {
    return (
      <SafeAreaView style={styles.container}>
        <Text style={{ color: "red" }}>{error}</Text>
        <View style={[styles.button, { marginTop: 20 }]}>
          <ActionButton
            titulo="SAIR (LOGOUT)"
            onPress={() => {
              signOut();
            }}
          />
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>

      <FlatList
        style={{ flex: 1, marginBottom: 25 }}
        data={agendamentosParaExibir}
        keyExtractor={(item) => item.id.toString()}
        ListEmptyComponent={
          <View style={styles.emptyContainer}>
            <MaterialCommunityIcons 
              name="calendar-check-outline" 
              size={64} 
              color='#d0d0d0'
            />
            <Text style={styles.emptyText}>
              {diaSelecionado 
                ? "Nenhum agendamento para este dia." 
                : "Nenhum agendamento futuro encontrado."}
            </Text>
          </View>
        }
        ListHeaderComponent={
          <>
            <Calendario
              agendamentos={agendamentosDoMes}
              diaSelecionado={diaSelecionado}
              onDiaPressionado={handleDiaPressionado}
              onMesMudou={handleMesMudou}
            />

            <View style={styles.pdfContainer}>
              <Text
                style={[
                  styles.headerTextFlatList,
                  { marginTop: 0, marginBottom: 0 },
                ]}
              >
                {diaSelecionado
                  ? `PENDENTES EM ${format(
                      parseDateSafe(diaSelecionado + "T12:00:00"),
                      "dd/MM/yyyy",
                      { locale: ptBR }
                    )}`
                  : "PRÓXIMOS AGENDAMENTOS"}
              </Text>

              <Pressable
                onPress={handleExportarPDF}
                style={({ pressed }) => ({
                  opacity: pressed ? 0.5 : 1,
                  backgroundColor: "#f0f0f0",
                  padding: 8,
                  borderRadius: 8,
                })}
              >
                <MaterialCommunityIcons
                  name="file-pdf-box"
                  size={28}
                  color="#d32f2f"
                />
              </Pressable>
            </View>
          </>
        }
        renderItem={({ item }) => (
          <View style={styles.cardsContainer}>
            <View style={styles.cardContent}>
              <View>
                <Text style={[styles.textContent, { fontSize: 16 }]}>
                  {item.tratamento.nome}
                </Text>

                <Text style={[styles.textContent, { fontSize: 13 }]}>
                  Medicamento: {item.tratamento.medicamento.nome}
                </Text>

                <Pressable
                  style={styles.concluirPressable}
                  onPress={() => handleAbrirModalConclusao(item)}
                >
                  <Text style={styles.textPressableContent}>CONCLUIR</Text>
                </Pressable>
              </View>

              <View>
                <Text
                  style={[
                    styles.textContent,
                    { fontWeight: "bold", fontSize: 18 },
                  ]}
                >
                  {format(item.horarioDoAgendamento, "HH:mm", {
                    locale: ptBR,
                  })}
                </Text>
              </View>
            </View>
          </View>
        )}
      />

      <Modal
        visible={modalObsVisivel}
        transparent={true}
        animationType="fade"
        onRequestClose={() => setModalObsVisivel(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <Text style={styles.modalTitle}>Confirmar Dose</Text>
            <Text style={{ marginBottom: 10, color: "#666" }}>
              Como você está se sentindo? (Opcional)
            </Text>

            <TextInput
              style={styles.inputObservacao}
              placeholder="Ex: Senti um pouco de enjoo..."
              multiline={true}
              numberOfLines={4}
              value={textoObservacao}
              onChangeText={setTextoObservacao}
            />

            <View style={styles.modalPressables}>
              <Pressable
                style={styles.pressCancelar}
                onPress={() => setModalObsVisivel(false)}
                disabled={loadingConclusao}
              >
                <Text style={styles.pressTextCancelar}>Cancelar</Text>
              </Pressable>

              <Pressable
                style={styles.pressConfirmar}
                onPress={confirmarConclusao}
                disabled={loadingConclusao}
              >
                {loadingConclusao ? (
                  <ActivityIndicator size="small" color="white" />
                ) : (
                  <Text style={styles.pressTextConfirmar}>Confirmar</Text>
                )}
              </Pressable>
            </View>
          </View>
        </View>
      </Modal>
    </SafeAreaView>
  );
}
