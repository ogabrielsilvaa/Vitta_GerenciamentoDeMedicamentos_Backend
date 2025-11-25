import {
  listarTratamentos,
  tratamentoDeletar,
  tratamentoEncerrar,
} from "@/src/api/tratamentoApi";
import { ActionButton } from "@/src/components/actionButton/actionButton";
import { AlertModal } from "@/src/components/alertModal";
import { DeletePressable } from "@/src/components/deletePressable";
import FormularioTratamento from "@/src/components/formTratamento";
import { useSession } from "@/src/contexts/authContext";
import { TratamentoStatus } from "@/src/enums/tratamento/tratamentoStatus";
import { TratamentoResponse } from "@/src/types/tratamentoTypes";
import { Ionicons, MaterialCommunityIcons } from "@expo/vector-icons";
import { useCallback, useEffect, useMemo, useState } from "react";
import {
  ActivityIndicator,
  Alert,
  FlatList,
  KeyboardAvoidingView,
  LayoutAnimation,
  Modal,
  Platform,
  Pressable,
  Text,
  UIManager,
  View,
} from "react-native";
import {
  SafeAreaView,
  useSafeAreaInsets,
} from "react-native-safe-area-context";
import { styles } from "./styles";

const MAPA_FREQUENCIA: Record<string, string> = {
  INTERVALO_HORAS: "Intervalo de Horas",
  HORARIOS_ESPECIFICOS: "Horários Específicos",
};

const MAPA_ALERTA: Record<string, string> = {
  NOTIFICACAO_PUSH: "Notificação Push",
  ALARME: "Alarme",
};

const formatarEnum = (
  valor: string | undefined,
  mapa: Record<string, string>
) => {
  if (!valor) return "Não definido";
  return mapa[valor] || valor;
};

if (
  Platform.OS === "android" &&
  UIManager.setLayoutAnimationEnabledExperimental
) {
  UIManager.setLayoutAnimationEnabledExperimental(true);
}

export default function TratamentoScreen() {
  const { session } = useSession();
  const [tratamentos, setTratamentos] = useState<TratamentoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [modalVisivel, setModalVisivel] = useState(false);

  const [alertVisivel, setAlertVisivel] = useState(false);
  const [itemParaDeletar, setItemParaDeletar] = useState<number | null>(null);

  const [isDeleting, setIsDeleting] = useState(false);

  const [itemEmEdicao, setItemEmEdicao] = useState<TratamentoResponse | null>(
    null
  );

  const [modalVerVisivel, setModalVerVisivel] = useState(false);
  const [itemParaVer, setItemParaVer] = useState<TratamentoResponse | null>(
    null
  );

  const [filtroAtivo, setFitlroAtivo] = useState<"ativos" | "concluídos">(
    "ativos"
  );

  const [alertEncerrarVisivel, setAlertEncerrarVisivel] = useState(false);

  const [itemParaEncerrar, setItemParaEncerrar] =
    useState<TratamentoResponse | null>(null);

  const [successModalVisible, setSuccessModalVisible] = useState(false);

  const insets = useSafeAreaInsets();

  const carregarTratamentosCadastrados = useCallback(async () => {
    if (!session) return;

    setLoading(true);
    setError(null);

    try {
      const data = await listarTratamentos(session);
      setTratamentos(data);
    } catch (error) {
      setError("Falha ao carregar tratamentos.");
      console.error(error);
    } finally {
      setLoading(false);
    }
  }, [session]);

  useEffect(() => {
    carregarTratamentosCadastrados();
  }, [carregarTratamentosCadastrados]);

  const executarDelete = async (tratamentoId: number) => {
    if (!session) {
      setError("Sessão expirada. Faça login novamente.");
      return;
    }

    setIsDeleting(true);
    setError(null);

    try {
      await tratamentoDeletar(session, tratamentoId);

      setTratamentos((tratamentosAnteriores) =>
        tratamentosAnteriores.filter(
          (tratamentos) => tratamentos.id !== tratamentoId
        )
      );
    } catch (error) {
      console.error("Erro ao deletar tratamento: ", error);
      setError("Falha ao deletar o tratamento. Tente novamente.");
    } finally {
      setIsDeleting(false);
    }
  };

  const formatarData = (dataString: string | Date): string => {
    if (!dataString) return "Data não definida";

    try {
      const data = new Date(dataString);
      return data.toLocaleDateString("pt-BR", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        timeZone: "UTC",
      });
    } catch (error) {
      return "Data inválida";
    }
  };

  const handleDeletarTratamento = (tratamentoId: number) => {
    console.log("FUNÇÃO handleDeletarTratamento FOI CHAMADA");
    if (isDeleting) {
      console.log("Retornando porque isDeleting é true");
      return;
    }

    setItemParaDeletar(tratamentoId);
    setAlertVisivel(true);
  };

  const onConfirmEncerrar = async () => {
    if (!itemParaEncerrar || !session) return;

    try {
      await tratamentoEncerrar(session, itemParaEncerrar.id);

      setTratamentos((listaAtual) =>
        listaAtual.map((item) =>
          item.id === itemParaEncerrar.id
            ? { ...item, status: TratamentoStatus.CONCLUIDO }
            : item
        )
      );

      setAlertEncerrarVisivel(false);
      setItemParaEncerrar(null);

      setSuccessModalVisible(true);
    } catch (err) {
      console.error(err);
      Alert.alert("Erro", "Não foi possível encerrar o tratamento.");
      setAlertEncerrarVisivel(false);
      setItemParaEncerrar(null);
    }
  };

  const handleEncerrarTratamento = (tratamento: TratamentoResponse) => {
    setItemParaEncerrar(tratamento);
    setAlertEncerrarVisivel(true);
  };

  const onConfirmarDelete = () => {
    // se não tiver um item selecionado, não faz nada
    if (itemParaDeletar === null) {
      setAlertVisivel(false);
      return;
    }

    // chama função de deletar que já funciona
    executarDelete(itemParaDeletar);

    // limpa o estado
    setAlertVisivel(false);
    setItemParaDeletar(null);
  };

  // função para o botão "Cancelar" do AlertModal
  const onCancelarDelete = () => {
    setAlertVisivel(false);
    setItemParaDeletar(null);
  };

  const abrirModalCadastro = () => {
    setItemEmEdicao(null);
    setModalVisivel(true);
  };

  const abrirModalEdicao = (medicamento: TratamentoResponse) => {
    setItemEmEdicao(medicamento);
    setModalVisivel(true);
  };

  const abrirModalVer = (tratamento: TratamentoResponse) => {
    setItemParaVer(tratamento);
    setModalVerVisivel(true);
  };

  const fecharModalVer = () => {
    setModalVerVisivel(false);
    setItemParaVer(null);
  };

  const isTratamentoVencido = (dataTerminoString: string | Date): boolean => {
    if (!dataTerminoString) {
      return false;
    }

    try {
      const hoje = new Date();
      hoje.setHours(0, 0, 0, 0);

      const [ano, mes, dia] = String(dataTerminoString)
        .split("T")[0]
        .split("-")
        .map(Number);
      const dataTermino = new Date(ano, mes - 1, dia);

      return dataTermino < hoje;
    } catch (error) {
      console.error("Erro ao processar data de término: ", error);
      return false;
    }
  };

  const tratamentosFiltrados = useMemo(() => {
    const lista = tratamentos.filter((t) => {
      const dataVencida = isTratamentoVencido(t.dataDeTermino);

      const statusEncerrado =
        t.status === TratamentoStatus.CONCLUIDO ||
        t.status === TratamentoStatus.CANCELADO;

      const isConcluido = dataVencida || statusEncerrado;

      if (filtroAtivo === "ativos") {
        return !isConcluido;
      } else {
        return isConcluido;
      }
    });

    return lista.sort((a, b) => {
      const dataA = new Date(a.dataDeInicio).getTime();
      const dataB = new Date(b.dataDeInicio).getTime();
      return dataA - dataB;
    });
  }, [tratamentos, filtroAtivo]);

  const alterarFiltro = (novoFiltro: "ativos" | "concluídos") => {
    if (filtroAtivo !== novoFiltro) {
      LayoutAnimation.configureNext(LayoutAnimation.Presets.easeInEaseOut);
      setFitlroAtivo(novoFiltro);
    }
  };

  const contagem = useMemo(() => {
    let ativos = 0;
    let concluidos = 0;

    tratamentos.forEach((t) => {
      const dataVencida = isTratamentoVencido(t.dataDeTermino);

      const statusEncerrado =
        t.status === TratamentoStatus.CONCLUIDO ||
        t.status === TratamentoStatus.CANCELADO;

      if (dataVencida || statusEncerrado) {
        concluidos++;
      } else {
        ativos++;
      }
    });

    return { ativos, concluidos };
  }, [tratamentos]);

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
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <Modal
        visible={modalVisivel}
        animationType="slide"
        transparent={true}
        onRequestClose={() => setModalVisivel(false)}
      >
        <KeyboardAvoidingView
          behavior={Platform.OS === "ios" ? "padding" : "padding"}
          style={{ flex: 1 }}
        >
          <View style={styles.modalContainer}>
            <FormularioTratamento
              onClose={() => setModalVisivel(false)}
              onSaveSuccess={carregarTratamentosCadastrados}
              tratamentoParaEditar={itemEmEdicao}
            />
          </View>
        </KeyboardAvoidingView>
      </Modal>

      <Text style={styles.title}>SEUS TRATAMENTOS</Text>

      <View style={styles.filterButtonContainer}>
        <Pressable
          style={[
            styles.filterButton,
            filtroAtivo === "ativos" && styles.filterButtonActive,
          ]}
          onPress={() => alterarFiltro("ativos")}
        >
          <Text
            style={[
              styles.filterText,
              filtroAtivo === "ativos" && styles.filterTextActive,
            ]}
          >
            Em Andamento ({contagem.ativos})
          </Text>
        </Pressable>

        <Pressable
          style={[
            styles.filterButton,
            filtroAtivo === "concluídos" && styles.filterButtonActive,
          ]}
          onPress={() => alterarFiltro("concluídos")}
        >
          <Text
            style={[
              styles.filterText,
              filtroAtivo === "concluídos" && styles.filterTextActive,
            ]}
          >
            Histórico ({contagem.concluidos})
          </Text>
        </Pressable>
      </View>

      <FlatList
        style={styles.flatList}
        contentContainerStyle={{
          alignItems: "center",
          paddingBottom: insets.bottom,
        }}
        data={tratamentosFiltrados}
        keyExtractor={(item) => item.id.toString()}
        ListEmptyComponent={
          <View style={styles.emptyContainer}>
            <MaterialCommunityIcons
              name="medical-bag"
              size={64}
              color='#d0d0d0'
            />
            <Text style={styles.emptyText}>
              {filtroAtivo === "ativos"
                ? "Nenhum tratamento em andamento."
                : "Nenhum histórico de tratamentos concluídos."}
            </Text>
          </View>
        }
        renderItem={({ item }) => {
          const isConcluido = isTratamentoVencido(item.dataDeTermino);

          return (
            <View style={styles.cardsContainer}>
              <View style={styles.cardContent}>
                <View style={styles.cardTopRow}>
                  <View>
                    <Ionicons name={"medical"} size={36} color="white" />
                  </View>

                  <View style={styles.textContainer}>
                    <View>
                      <Text style={[styles.textContent, { fontSize: 22 }]}>
                        {item.nome}
                      </Text>

                      <Text
                        style={[
                          styles.textContent,
                          { fontSize: 20, marginBottom: 10 },
                        ]}
                      >
                        {item.medicamento.nome}
                      </Text>

                      <Text style={[styles.textContent, { fontSize: 18 }]}>
                        {item.instrucoes}
                      </Text>

                      {isConcluido ? (
                        <View style={styles.dateContainerVencido}>
                          <Text style={styles.concluidoText}>
                            <MaterialCommunityIcons
                              name="check-circle"
                              size={20}
                              color="#fff"
                            />{" "}
                            CONCLUÍDO
                          </Text>
                        </View>
                      ) : (
                        <View style={styles.dateContainer}>
                          <Text style={styles.dateText}>
                            {formatarData(item.dataDeInicio)}
                          </Text>
                          <Text style={styles.dateText}>
                            {formatarData(item.dataDeTermino)}
                          </Text>
                        </View>
                      )}
                    </View>
                  </View>
                </View>

                <View style={styles.cardBottomRow}>
                  <Pressable
                    style={styles.pressableButton}
                    onPress={() => abrirModalVer(item)}
                    disabled={isDeleting}
                  >
                    <MaterialCommunityIcons
                      name="eye"
                      size={24}
                      color="black"
                    />
                  </Pressable>

                  <View style={styles.rightButtonsContainer}>
                    {!isConcluido && (
                      <>
                        <Pressable
                          style={[styles.pressableButton, { marginRight: 8 }]}
                          onPress={() => handleEncerrarTratamento(item)}
                          disabled={isDeleting}
                        >
                          <MaterialCommunityIcons
                            name="stop-circle-outline"
                            size={24}
                            color="#d32f2f"
                          />
                        </Pressable>
                        <Pressable
                          style={styles.pressableButton}
                          onPress={() => abrirModalEdicao(item)}
                          disabled={isDeleting}
                        >
                          <MaterialCommunityIcons
                            name="pencil"
                            size={24}
                            color="black"
                          />
                        </Pressable>
                      </>
                    )}

                    <DeletePressable
                      onPress={() => handleDeletarTratamento(item.id)}
                      disabled={isDeleting}
                    />
                  </View>
                </View>
              </View>
            </View>
          );
        }}
      />

      <Modal
        visible={modalVerVisivel}
        animationType="slide"
        transparent={true}
        onRequestClose={() => setModalVisivel(false)}
      >
        <View style={styles.viewModalContainer}>
          <View style={styles.viewModalContent}>
            <Text style={styles.viewModalTitle}>{itemParaVer?.nome}</Text>

            <Text style={styles.viewModalSectionTitle}>Medicamento</Text>
            <Text style={styles.viewModalText}>
              Nome: {itemParaVer?.medicamento.nome}
            </Text>
            <Text style={styles.viewModalText}>
              Dosagem: {itemParaVer?.dosagem}
            </Text>
            <Text style={styles.viewModalText}>
              Instruções: {itemParaVer?.instrucoes || "Nenhuma"}
            </Text>

            <Text style={styles.viewModalSectionTitle}>Duração e Status</Text>
            {isTratamentoVencido(itemParaVer?.dataDeTermino || "") ? (
              <Text style={styles.viewModalText}>Status: Concluído</Text>
            ) : (
              <>
                <Text style={styles.viewModalText}>
                  Início: {formatarData(itemParaVer?.dataDeInicio || "")}
                </Text>
                <Text style={styles.viewModalText}>
                  Fim: {formatarData(itemParaVer?.dataDeTermino || "")}
                </Text>
              </>
            )}

            <Text style={styles.viewModalSectionTitle}>
              Frequência e Alerta
            </Text>
            <Text style={styles.viewModalText}>
              Tipo:{" "}
              {formatarEnum(itemParaVer?.tipoDeFrequencia, MAPA_FREQUENCIA)}
            </Text>

            {String(itemParaVer?.tipoDeFrequencia) === "INTERVALO_HORAS" && (
              <Text style={styles.viewModalText}>
                Intervalo: A cada {itemParaVer?.intervaloEmHoras} horas
              </Text>
            )}
            {String(itemParaVer?.tipoDeFrequencia) ===
              "HORARIOS_ESPECIFICOS" && (
              <Text style={styles.viewModalText}>
                Horários: {itemParaVer?.horariosEspecificos}
              </Text>
            )}

            <Text style={styles.viewModalText}>
              Alerta: {formatarEnum(itemParaVer?.tipoDeAlerta, MAPA_ALERTA)}
            </Text>

            <ActionButton
              style={{ marginTop: 20 }}
              titulo="FECHAR"
              onPress={fecharModalVer}
            />
          </View>
        </View>
      </Modal>

      <AlertModal
        visible={alertVisivel}
        title="CONFIRMAR EXCLUSÃO"
        message="Tem certeza que deseja excluir este tratamento ?"
        onClose={onCancelarDelete}
        onConfirm={onConfirmarDelete}
        confirmText="Excluir"
        isDestructive={true}
      />

      <AlertModal
        visible={alertEncerrarVisivel}
        title="ENCERRAR TRATAMENTO"
        message={`Deseja encerrar o tratamento "${itemParaEncerrar?.nome}"? Os lembretes futuros serão cancelados e ele irá para o histórico.`}
        onClose={() => {
          setAlertEncerrarVisivel(false);
          setItemParaEncerrar(null);
        }}
        onConfirm={onConfirmEncerrar}
        confirmText="Sim, Encerrar"
        cancelText="Voltar"
        isDestructive={false}
      />

      <AlertModal
        visible={successModalVisible}
        title="SUCESSO!"
        message="O tratamento foi encerrado e movido para o histórico."
        onClose={() => setSuccessModalVisible(false)}
        onConfirm={() => setSuccessModalVisible(false)}
        confirmText="OK"
        cancelText="Fechar"
        isDestructive={false}
      />

      <Pressable
        style={({ pressed }) => [styles.fab, pressed && styles.fabPressed]}
        onPress={abrirModalCadastro}
      >
        <MaterialCommunityIcons name="plus" size={30} color="#1CBDCF" />
      </Pressable>
    </SafeAreaView>
  );
}
